package me.libme.fn.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static io.netty.util.internal.ObjectUtil.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * wrapped {@link ChannelFuture} , high layer can be independent to the concrete  
 * @author JIAZJ
 *
 * @param <V>
 */
public class DefaultCallPromise<V> implements CallPromise<V> {


	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCallPromise.class);

	private final String sequenceIdentity;

	private ChannelFuture channelFuture;

	private Future<Channel> channelFutureFromPool;
	
	private final ChannelRunnable channelRunnable;
	
	@SuppressWarnings("rawtypes")
	private static final AtomicReferenceFieldUpdater<DefaultCallPromise, Object> RESULT_UPDATER = AtomicReferenceFieldUpdater
			.newUpdater(DefaultCallPromise.class, Object.class, "result");

	private static final CauseHolder CANCELLATION_CAUSE_HOLDER = 
				new CauseHolder(ThrowableUtil.unknownStackTrace(new CancellationException(), DefaultCallPromise.class, "cancel(...)"));

	private static final StatusHolder REQUEST_UNCANCELLED_HOLDER=
			new StatusHolder(TransportStatus.UNCANCELLABLE);
	
	private static final StatusHolder REQUEST_SUCCESS_HOLDER=
			new StatusHolder(TransportStatus.SUCCESS);
	
	private static final ResponseHolder NOT_GET_RESPONSE_HOLDER=
			new ResponseHolder(null);
	
	private volatile Object result;

	/**
	 * Threading - synchronized(this). We are required to hold the monitor to
	 * use Java's underlying wait()/notifyAll().
	 */
	private short waiters;
	
	private List<GenericPromiseListener<? extends CallPromise<? super V>>> listeners=new ArrayList<>();
	
	DefaultCallPromise(ChannelRunnable channelRunnable,Future<Channel> channelFutureFromPool,String sequenceIdentity) {
		this.channelRunnable=channelRunnable;
		this.channelFutureFromPool=channelFutureFromPool;
		this.sequenceIdentity=sequenceIdentity;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		await();

		Throwable cause = cause();
		if (cause == null) {
			return getNow();
		}
		if (cause instanceof CancellationException) {
			throw (CancellationException) cause;
		}
		throw new ExecutionException(cause);
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (await(timeout, unit)) {
			Throwable cause = cause();
			if (cause == null) {
				return getNow();
			}
			if (cause instanceof CancellationException) {
				throw (CancellationException) cause;
			}
			throw new ExecutionException(cause);
		}
		throw new TimeoutException();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return channelFuture.cancel(mayInterruptIfRunning);
//		if (RESULT_UPDATER.compareAndSet(this, null, CANCELLATION_CAUSE_HOLDER)) {
//			checkNotifyWaiters();
//			notifyListeners();
//			return true;
//		}
//		return false;
	}

	boolean setRequestCancelled(){
		if (RESULT_UPDATER.compareAndSet(this, null, CANCELLATION_CAUSE_HOLDER)) {
			checkNotifyWaiters();
			notifyListeners();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled0(result);
	}

	private static boolean isCancelled0(Object result) {
		return result instanceof CauseHolder 
					&& ((CauseHolder) result).cause instanceof CancellationException;
	}

	@Override
	public boolean isDone() {
		return isDone0(result);
	}

	@Override
	public boolean isResponsed() {
		return isResponsed0(result);
	}
	
	private boolean isResponsed0(Object result) {
		return isDone0(result)
				&&result instanceof ResponseHolder;
	}
	
	private boolean isDone0(Object result) {
		return result != null 
				&& result != REQUEST_UNCANCELLED_HOLDER
					&&result != REQUEST_SUCCESS_HOLDER;
	}

	@Override
	public boolean isRequestSuccess() {
		return isRequestSuccess0(result);
	}
	
	private boolean isRequestSuccess0(Object result){
		return result==REQUEST_SUCCESS_HOLDER;
	}

	@Override
	public boolean isRequestCancellable() {
		return result == null;
	}

	@Override
	public Throwable cause() {
		Object result = this.result;
		return (result instanceof CauseHolder) ? ((CauseHolder) result).cause : null;
	}

	@Override
	public boolean setRequestSuccess() {
		if (RESULT_UPDATER.compareAndSet(this, 
				REQUEST_UNCANCELLED_HOLDER, 
				REQUEST_SUCCESS_HOLDER)) {
			return true;
		}
		Object result = this.result;
		return !isDone0(result) || !isCancelled0(result);
	}
	
	@Override
	public CallPromise<V> setResponse(V result) {
		if (setResponse0(result)) {
			notifyListeners();
			return this;
		}
		throw new IllegalStateException("complete already: " + this);
	}
	

	@Override
	public boolean tryResponse(V result) {
		if (setResponse0(result)) {
			notifyListeners();
			return true;
		}
		return false;
	}

	@Override
	public CallPromise<V> setFailure(Throwable cause) {
		if (setFailure0(cause)) {
			notifyListeners();
			return this;
		}
		throw new IllegalStateException("complete already: " + this, cause);
	}

	private boolean setFailure0(Throwable cause) {
		return setValue0(new CauseHolder(checkNotNull(cause, "cause")));
	}

	@SuppressWarnings("unchecked")
	private void notifyListeners() {
		for(GenericPromiseListener<?> listener :listeners){
			GenericPromiseListener<CallPromise<V>> promiseListener=(GenericPromiseListener<CallPromise<V>>) listener;
			 try {
				 promiseListener.operationComplete(this);
		        } catch (Throwable t) {
		        	LOGGER.warn("An exception was thrown by " +
		        			promiseListener.getClass().getName() + ".operationComplete()", t);
		        }
		}
	}

	private boolean setResponse0(V result) {
		return setValue0(result == null ? NOT_GET_RESPONSE_HOLDER : new ResponseHolder(result));
	}
	

	private boolean setValue0(Object objResult) {
		if (RESULT_UPDATER.compareAndSet(this, null, objResult)
//				|| RESULT_UPDATER.compareAndSet(this, TransportStatus.UNCANCELLABLE, objResult)
				|| RESULT_UPDATER.compareAndSet(this, REQUEST_SUCCESS_HOLDER, objResult)
				) {
			checkNotifyWaiters();
			return true;
		}
		return false;
	}

	private synchronized void checkNotifyWaiters() {
		if (waiters > 0) {
			notifyAll();
		}
	}

	private void incWaiters() {
		if (waiters == Short.MAX_VALUE) {
			throw new IllegalStateException("too many waiters: " + this);
		}
		++waiters;
	}

	private void decWaiters() {
		--waiters;
	}

	private void rethrowIfFailed() {
		Throwable cause = cause();
		if (cause == null) {
			return;
		}

		PlatformDependent.throwException(cause);
	}

	@Override
	public boolean tryFailure(Throwable cause) {
		if (setFailure0(cause)) {
			notifyListeners();
			return true;
		}
		return false;
	}

	@Override
	public boolean setRequestUncancellable() {
		if (RESULT_UPDATER.compareAndSet(this, null, REQUEST_UNCANCELLED_HOLDER)) {
			return true;
		}
		Object result = this.result;
		return !isDone0(result) || !isCancelled0(result);
	}

	@Override
	public CallPromise<V> addListener(GenericPromiseListener<? extends CallPromise<? super V>> listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
		
		if(isDone()){
			notifyListeners();
		}
		
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallPromise<V> addListeners(GenericPromiseListener<? extends CallPromise<? super V>>... listeners) {
		synchronized (this.listeners) {
			for(GenericPromiseListener<? extends CallPromise<? super V>> listener :listeners){
				this.listeners.add(listener);
			}
		}
		if(isDone()){
			notifyListeners();
		}
		return this;
	}

	@Override
	public CallPromise<V> removeListener(GenericPromiseListener<? extends CallPromise<? super V>> listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CallPromise<V> removeListeners(GenericPromiseListener<? extends CallPromise<? super V>>... listeners) {
		synchronized (listeners) {
			for(GenericPromiseListener<? extends CallPromise<? super V>> listener :listeners){
				removeListener(listener);
			}
		}
		return this;
	}

	@Override
	public CallPromise<V> await() throws InterruptedException {

		if (isDone()) {
			return this;
		}

		if (Thread.interrupted()) {
			throw new InterruptedException(toString());
		}

		// checkDeadLock();

		synchronized (this) {
			while (!isDone()) {
				incWaiters();
				try {
					wait();
				} finally {
					decWaiters();
				}
			}
		}
		return this;

	}

	@Override
	public CallPromise<V> awaitUninterruptibly() {

		if (isDone()) {
			return this;
		}

		// checkDeadLock();

		boolean interrupted = false;
		synchronized (this) {
			while (!isDone()) {
				incWaiters();
				try {
					wait();
				} catch (InterruptedException e) {
					// Interrupted while waiting.
					interrupted = true;
				} finally {
					decWaiters();
				}
			}
		}

		if (interrupted) {
			Thread.currentThread().interrupt();
		}

		return this;

	}

	@Override
	public CallPromise<V> sync() throws InterruptedException {
		await();
		rethrowIfFailed();
		return this;
	}

	@Override
	public CallPromise<V> syncUninterruptibly() {
		awaitUninterruptibly();
		rethrowIfFailed();
		return this;
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return await0(unit.toNanos(timeout), true);
	}

	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
		return await0(MILLISECONDS.toNanos(timeoutMillis), true);
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		try {
			return await0(unit.toNanos(timeout), false);
		} catch (InterruptedException e) {
			// Should not be raised at all.
			throw new InternalError();
		}
	}

	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		try {
			return await0(MILLISECONDS.toNanos(timeoutMillis), false);
		} catch (InterruptedException e) {
			// Should not be raised at all.
			throw new InternalError();
		}
	}

	private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
		if (isDone()) {
			return true;
		}

		if (timeoutNanos <= 0) {
			return isDone();
		}

		if (interruptable && Thread.interrupted()) {
			throw new InterruptedException(toString());
		}

		// checkDeadLock();

		long startTime = System.nanoTime();
		long waitTime = timeoutNanos;
		boolean interrupted = false;
		try {
			for (;;) {
				synchronized (this) {
					if (isDone()) {
						return true;
					}
					incWaiters();
					try {
						wait(waitTime / 1000000, (int) (waitTime % 1000000));
					} catch (InterruptedException e) {
						if (interruptable) {
							throw e;
						} else {
							interrupted = true;
						}
					} finally {
						decWaiters();
					}
				}
				if (isDone()) {
					return true;
				} else {
					waitTime = timeoutNanos - (System.nanoTime() - startTime);
					if (waitTime <= 0) {
						return isDone();
					}
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public V getNow() {
		Object result = this.result;
		if (result instanceof CauseHolder 
				|| !(result instanceof ResponseHolder)
					||(result==NOT_GET_RESPONSE_HOLDER)) {
			return null;
		}
		return _getResponse();
	}

	@SuppressWarnings("unchecked")
	V _getResponse() {
		return ((ResponseHolder<V>)result).response;
	}

	private static final class CauseHolder {
		final Throwable cause;
		CauseHolder(Throwable cause) {
			this.cause = cause;
		}
	}
	
	private static final class StatusHolder{
		final TransportStatus status;
		StatusHolder(TransportStatus status) {
			this.status=status;
		}
		
	}

	private static final class ResponseHolder<V>{
		final V response;
		ResponseHolder(V response) {
			this.response=response;
		}
		
	}
	
	ChannelRunnable getChannelRunnable() {
		return channelRunnable;
	}
	
	void setChannelFuture(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
	}
	
	public static class _DefaultCallPromiseUtil{
		public static ChannelFuture getChannelFuture(DefaultCallPromise<?> defaultCallPromise){
			return defaultCallPromise.channelFuture;
		}
	}

	public String getSequenceIdentity() {
		return sequenceIdentity;
	}
}

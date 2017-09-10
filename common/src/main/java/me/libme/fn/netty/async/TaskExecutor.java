package me.libme.fn.netty.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author J
 */
public class TaskExecutor{
	
	protected final Logger LOGGER=LoggerFactory.getLogger(getClass());

	private final ExecutorService poolExecutor;

	public TaskExecutor(ExecutorServiceFactory executorServiceFactory) {
		try {
			this.poolExecutor = executorServiceFactory.getObject();
		} catch (Exception e) {
			throw new IllegalStateException("cannot initialize executor service.");
		}
	}

	public Promise promise(Task task){
		_R promise = promise0(task);
		return promise;
	}

	private _R promise0(Object task) {
		_R promise=new _R();
		_Runnable tsk=new _Runnable(task,promise);
		promise.setTask(tsk);
		poolExecutor.execute(tsk);
		return promise;
	}

	public <V> Promise promise(CallableTask<V> task){
		_R promise = promise0(task);
		return promise;
	}
	
	public static interface Result<V>{
		V get();
	}

	public class _Runnable  implements Runnable,Result<Object>{
		
		private final Promise promise;
		
		private final Object obj;

		private Object result;
		
		public _Runnable(Object obj,Promise promise) {
			this.obj=obj;
			this.promise=promise;
		}
		
		private Exception exception;
		
		private boolean complete;
		
		private void setComplete(boolean complete) {
			this.complete = complete;
		}
		
		public Exception getException() {
			return exception;
		}
		
		public boolean isComplete() {
			return complete;
		}
		
		public boolean isError(){
			return isComplete()&&exception!=null;
		}
		
		public boolean isSuccess(){
			return isComplete()&&exception==null;
		}
		
		@Override
		public Object get() {
			return result;
		}

		@Override
		public void run() {
			try{
				if(Runnable.class.isInstance(obj)){
					((Runnable)obj).run();
				}else if(CallableTask.class.isInstance(obj)){
					this.result=((CallableTask)obj).call();
				}
				setComplete(true);
				promise.successSync();
			}catch (Exception e) {
				setComplete(true);
				exception=e;
				promise.errorSync();
			}
		}
		
	}
	
	private class _R extends Promise{
		
		private _Runnable task;
		
		@Override
		protected boolean isError() {
			return task.isError();
		}
		
		@Override
		protected boolean isSuccess() {
			return task.isSuccess();
		}
		
		void setTask(_Runnable task) {
			this.task = task;
		}
		
		@Override
		protected Object _get() {
			return task.get();
		}
		
		@Override
		protected Throwable _error() {
			return this.task.getException();
		}
		
	}
	
	
	public abstract class Promise {
		
		protected Call<? super Object, ? super Object> call;
		
		protected Catch cat;
		
		@SuppressWarnings("unchecked")
		public Promise then(Call<?,?> call){
			if(this.call==null){
				this.call=(Call<? super Object, ? super Object>) call;
				successAsync();
			}else{
				throw new IllegalStateException("only one call can be accept");
			}
			return this;
		}
		
		public Promise cat(Catch cat){
			if(this.cat==null){
				this.cat=cat;
				errorAsync();
			}else{
				throw new IllegalStateException("only one catch can be accept");
			}
			return this;
		}
		
		protected abstract boolean isSuccess();
		
		protected abstract boolean isError();
		
		protected abstract Object _get();
		
		protected abstract Throwable _error();
		
		/**
		 * register hook if the task is completed successfully
		 */
		protected void successAsync(){
			if(isSuccess()){
				if(call!=null&&!call.isDone()){
					TaskExecutor.this.promise(new Task() {
						@Override
						public void run() {
							success0();
						}
					});
				}
			}
		}
		
		private synchronized void success0(){
			if(isSuccess()){
				if(call!=null&&!call.isDone()){
					call.call(_get());
					call.setDone(true);
				}
			}
		}
		
		private synchronized void error0(){
			if(isError()){
				if(cat!=null&&!cat.isDone()){
					cat.cat(_error());
					cat.setDone(true);
				}
			}
		}
		
		/**
		 * register hook if the task is completed failure
		 */
		protected void errorAsync() {
			if(isError()){
				if(cat!=null&&!cat.isDone()){
					TaskExecutor.this.promise(new Task() {
						@Override
						public void run() {
							error0();
						}
					});
				}
			}
			
		}
		
		/**
		 * use caller thread
		 */
		protected void successSync() {
			success0();
		}
		/**
		 * use caller thread
		 */
		protected void errorSync() {
			error0();
		}
		
	}
	
}

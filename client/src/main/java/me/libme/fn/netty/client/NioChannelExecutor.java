package me.libme.fn.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import me.libme.fn.netty.msg.HeaderNames;
import me.libme.fn.netty.util.JUniqueUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class NioChannelExecutor implements ChannelExecutor<NioChannelRunnable>{

	public static final AttributeKey<NioChannelExecutor> NIO_CHANNEL_EXECUTOR_ATTRIBUTE_KEY = AttributeKey.newInstance("NIO_CHANNEL_EXECUTOR");

	private static final Logger LOGGER = LoggerFactory.getLogger(NioChannelExecutor.class);

	private final String host;

	private final int port;

	private SimpleChannelPool channelPool;

	private AtomicInteger threadIndicator=new AtomicInteger(0);
	
	private ChannelInitializer<? extends Channel> clientInitializer;

	private static final PromiseRepo PROMISE_REPO=PromiseRepo.get();

	public NioChannelExecutor(String host, int port) {
		this.host = host;
		this.port = port;
	}

	NioChannelExecutor addChannelInitializer(ChannelInitializer<? extends Channel> clientInitializer){
		this.clientInitializer=clientInitializer;
		return this;
	}
	
	NioChannelExecutor connect() {
		try {
			// Configure the client.
			int availableProcessors=Runtime.getRuntime().availableProcessors();
			int threadCount;
			if(availableProcessors<3){
				threadCount=availableProcessors*8;
			}else{
				threadCount=availableProcessors*5;
			}
			LOGGER.info("availableProcessors : "+availableProcessors+"  netty-client-io-thread-count : "+threadCount);
			EventLoopGroup group = new NioEventLoopGroup(threadCount,new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r,"netty-client-io-"+threadIndicator.incrementAndGet());
				}
			});
			Bootstrap b = new Bootstrap();
			b.group(group)
					.attr(NIO_CHANNEL_EXECUTOR_ATTRIBUTE_KEY,this)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.channel(NioSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.DEBUG))
						.remoteAddress(host, port);
			channelPool = new SimpleChannelPool(b, new ChannelPoolHandler() {
				@Override
				public void channelReleased(Channel ch) throws Exception {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("channelReleased " + ch);
					}
				}

				@Override
				public void channelCreated(Channel ch) throws Exception {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("channelCreated " + ch);
					}
					ch.pipeline().addLast(clientInitializer);
				}

				@Override
				public void channelAcquired(Channel ch) throws Exception {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("channelAcquired " + ch);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	private static ExecutorService executorService=Executors.newFixedThreadPool(10);
	 
	@SuppressWarnings("rawtypes")
	private static GenericPromiseListener DO=new GenericPromiseListener() {
		
		@Override
		public void operationComplete(final CallPromise callPromise) throws Exception {
			final DefaultCallPromise defaultCallPromise=(DefaultCallPromise) callPromise;
			if(defaultCallPromise.isResponsed()){
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						defaultCallPromise.getChannelRunnable()
							.response(defaultCallPromise._getResponse());
					}
				});
			}else if(defaultCallPromise.isDone()
					&&defaultCallPromise.cause()!=null){
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						defaultCallPromise.getChannelRunnable()
							.fail(defaultCallPromise.cause());
					}
				});
			}else if(defaultCallPromise.isCancelled()){
				executorService.execute(new Runnable() {
					@Override
					public void run() {
						defaultCallPromise.getChannelRunnable()
							.cancelled(null);
					}
				});
			}
			
		}
		
	};


	@Override
	public <V> CallPromise<V> execute(final NioChannelRunnable channelRunnable) throws Exception {
		io.netty.util.concurrent.Future<Channel> channelFuture= channelPool.acquire();
		String sequenceIdentity= "SQ-"+JUniqueUtils.sequence()+"-"+JUniqueUtils.unique();
		final DefaultCallPromise<V> callPromise=new DefaultCallPromise<>(channelRunnable,channelFuture,sequenceIdentity);
		PROMISE_REPO.expire(sequenceIdentity,callPromise,1800, TimeUnit.SECONDS);
		channelRunnable.addHeader(HeaderNames.SEQUENCE_IDENTITY,sequenceIdentity); // additional sequenceIdentity

		callPromise.addListener(DO);
		channelFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Channel>>() {
			@Override
			public void operationComplete(io.netty.util.concurrent.Future<? super Channel> future) throws Exception {
				Channel channel=(Channel) future.get();
//				ReturnResponseInboundHandler setResponse=new ReturnResponseInboundHandler(callPromise);
//				channel.pipeline().addLast(setResponse);
//				channel.pipeline().addLast("releaseChannelHandler",releaseChannel);
				ChannelFuture cf= channelRunnable.request(channel);
				callPromise.setChannelFuture(cf);
				cf.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
					@Override
					public void operationComplete(io.netty.util.concurrent.Future<? super Void> future) throws Exception {
						ChannelFuture channelFuture=(ChannelFuture) future;
						if(channelFuture.isSuccess()){
							callPromise.setRequestUncancellable();
							callPromise.setRequestSuccess();
						}else  if(channelFuture.isCancelled()){
							callPromise.setRequestCancelled();
						}

					}
				});
			}
		});
		return callPromise;
	}

	ChannelInitializer<? extends Channel> getClientInitializer() {
		return clientInitializer;
	}
	
	String getHost() {
		return host;
	}
	
	int getPort() {
		return port;
	}

	@Override
	public void close() throws IOException {
		channelPool.close();
	}

	SimpleChannelPool getChannelPool() {
		return channelPool;
	}
}

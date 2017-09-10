package me.libme.fn.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class NioChannelExecutor implements ChannelExecutor<NioChannelRunnable>{

	private static final Logger LOGGER = LoggerFactory.getLogger(NioChannelExecutor.class);

	private final String host;

	private final int port;

	private SimpleChannelPool channelPool;
	
	private ChannelInitializer<? extends Channel> clientInitializer;

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
			EventLoopGroup group = new NioEventLoopGroup(99,new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r,"netty-client-io");
				}
			});
			Bootstrap b = new Bootstrap();
			b.group(group)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.channel(NioSocketChannel.class)
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
	
	
	private ReleaseChannelInboundHandler releaseHandler=new ReleaseChannelInboundHandler();
	
	@Sharable
	private class ReleaseChannelInboundHandler extends ChannelInboundHandlerAdapter{
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			try{
				ctx.channel().pipeline().remove(this);
				channelPool.release(ctx.channel());
			}finally{
				super.channelReadComplete(ctx);
			}
		}
	}
	
	@SuppressWarnings({"rawtypes","unchecked"})
	private class ReturnResponseInboundHandler extends OnceChannelInboundHandler{
		
		private final DefaultCallPromise callPromise;
		
		public ReturnResponseInboundHandler(DefaultCallPromise callPromise) {
			this.callPromise=callPromise;
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			callPromise.setResponse(msg);
			super.channelRead(ctx, msg);
		}
		
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			super.channelReadComplete(ctx);
		}
	}
	
	
	@Override
	public <V> CallPromise<V> execute(final NioChannelRunnable channelRunnable) throws Exception {
		io.netty.util.concurrent.Future<Channel> channelFuture= channelPool.acquire();
		final DefaultCallPromise<V> callPromise=new DefaultCallPromise<>(channelRunnable,channelFuture);
		callPromise.addListener(DO);
		channelFuture.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Channel>>() {
			@Override
			public void operationComplete(io.netty.util.concurrent.Future<? super Channel> future) throws Exception {
				Channel channel=(Channel) future.get();
				ReturnResponseInboundHandler handler=new ReturnResponseInboundHandler(callPromise);
				channel.pipeline().addLast(handler);
				channel.pipeline().addLast(releaseHandler);
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
}

package me.libme.fn.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;

public class SimpleHttpNioChannelServer implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpNioChannelServer.class);
	
	private final ServerConfig serverConfig;
	
	private final boolean useSSL;
	
	private EventLoopGroup bossGroup;
	
	private EventLoopGroup workerGroup;

	public SimpleHttpNioChannelServer(ServerConfig serverConfig,boolean useSSL) {
		this.serverConfig = serverConfig;
		this.useSSL = useSSL;
	}
	
	public SimpleHttpNioChannelServer(ServerConfig serverConfig) {
		this(serverConfig,false);
	}
	
	public void start() throws Exception{
		
		 // Configure SSL.
        final SslContext sslCtx;
        if (useSSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(serverConfig.getLoopThread(),new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r,"netty-server-acceptor-io-port-"+serverConfig.getPort());
			}
		});
        EventLoopGroup workerGroup = new NioEventLoopGroup(serverConfig.getWorkerThread(),new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r,"netty-server-worker-io-port-"+serverConfig.getPort());
			}
		});
        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new SimpleHttpServerInitializer(sslCtx))
             .bind(serverConfig.getPort());
            this.bossGroup=bossGroup;
            this.workerGroup=workerGroup;
			LOGGER.info("Open your web browser and navigate to " +
                    (useSSL? "https" : "http") + "://127.0.0.1:" + serverConfig.getPort() + '/');
            
        } catch (Exception e){
        	LOGGER.error(e.getMessage(), e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
	}

	@Override
	public void close() throws IOException {
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
	}
	
}

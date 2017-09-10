package me.libme.fn.netty.client;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

public class ChannelExecutors {

	public static NioChannelExecutor newNioChannelExecutor(String host, int port){
		return newNioChannelExecutor(host,port,null);
	}
	
	public static SimpleHttpClientInitializer newSimpleHttpClientInitializer(boolean ssl){
		SslContext sslCtx=null;
		if(ssl){
			try {
				sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
			} catch (SSLException e) {
			}
		}
		return new SimpleHttpClientInitializer(sslCtx);
	}
	
	public static NioChannelExecutor newNioChannelExecutor(String host, int port,SimpleHttpClientInitializer clientInitializer){
		try{
			if(clientInitializer==null){
				clientInitializer=new SimpleHttpClientInitializer(null);
			}
			return new NioChannelExecutor(host, port)
								.addChannelInitializer(clientInitializer)
									.connect();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

package me.libme.fn.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;

import java.util.ArrayList;
import java.util.List;

public class SimpleHttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    
    private List<ChannelHandler> channelHandlers=new ArrayList<>();

    public SimpleHttpClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }
    
    public SimpleHttpClientInitializer addChannelHandlers(ChannelHandler... channelHandlers) {
    	for (ChannelHandler channelHandler : channelHandlers) {
    		addChannelHandler(channelHandler);
		}
    	return this;
	}
    
    public SimpleHttpClientInitializer addChannelHandler(ChannelHandler channelHandler) {
    	if(!channelHandlers.contains(channelHandler)){
    		this.channelHandlers.add(channelHandler);
    	}
    	return this;
	}
    
    boolean ssl(){
    	return sslCtx!=null;
    }
    
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        // Enable HTTPS if necessary.
        if (ssl()) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        p.addLast(new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
//        p.addLast(new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpContents.
        p.addLast(new HttpObjectAggregator(1048576));
        
        for(ChannelHandler channelHandler:channelHandlers){
        	p.addLast(channelHandler);
        }

//        p.addLast(new SimpleHttpClientHandler());
        
//        p.addLast(new KryoClientHandler());
        
    }
}

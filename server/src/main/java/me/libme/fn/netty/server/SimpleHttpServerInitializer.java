package me.libme.fn.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class SimpleHttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public SimpleHttpServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());
//        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast(new HttpObjectAggregator(1048576));
//        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
//        p.addLast(new HttpContentCompressor());
        p.addLast(new ComplexServerHandler());
//        p.addLast(new HttpSnoopServerHandler());
    }
}
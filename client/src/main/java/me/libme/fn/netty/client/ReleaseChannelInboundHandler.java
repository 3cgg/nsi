package me.libme.fn.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.pool.SimpleChannelPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by J on 2017/9/12.
 */
@ChannelHandler.Sharable
@Deprecated
public class ReleaseChannelInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseChannelInboundHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        try {
            SimpleChannelPool channelPool = ctx.channel().attr(NioChannelExecutor.NIO_CHANNEL_EXECUTOR_ATTRIBUTE_KEY).get().getChannelPool();
            LOGGER.info("release channel " + ctx.channel());
            ctx.channel().pipeline().remove(this);
            channelPool.release(ctx.channel());
        } finally {
            super.channelReadComplete(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            SimpleChannelPool channelPool = ctx.channel().attr(NioChannelExecutor.NIO_CHANNEL_EXECUTOR_ATTRIBUTE_KEY).get().getChannelPool();
            LOGGER.info("release channel with exception : " + cause.getMessage() + ";" + ctx.channel());
            ctx.channel().pipeline().remove(this);
            channelPool.release(ctx.channel());
        } finally {
            super.exceptionCaught(ctx, cause);
        }

    }
}

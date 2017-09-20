package me.libme.fn.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnceChannelInboundHandler extends ChannelInboundHandlerAdapter{

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		try{
			ctx.channel().pipeline().remove(this);
		}finally{
			super.channelReadComplete(ctx);
		}
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		try{
			ctx.channel().pipeline().remove(this);
		}finally {
			LOGGER.error(cause.getMessage(),cause);
			super.exceptionCaught(ctx, cause);
		}

	}
}

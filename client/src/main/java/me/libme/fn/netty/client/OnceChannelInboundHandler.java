package me.libme.fn.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class OnceChannelInboundHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		try{
			ctx.channel().pipeline().remove(this);
		}finally{
			super.channelReadComplete(ctx);
		}
	}
	
}

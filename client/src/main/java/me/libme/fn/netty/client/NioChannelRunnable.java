package me.libme.fn.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultFullHttpRequest;

public class NioChannelRunnable extends ChannelRunnable {
	
	public NioChannelRunnable(SimpleRequest request, ChannelResponseCall responseCall) {
		super(request,responseCall);
	}
	
	public NioChannelRunnable(SimpleRequest request) {
		super(request,ChannelResponseCall.NOTHING);
	}
	
	@Override
	protected ChannelFuture doRequest(Channel channel, DefaultFullHttpRequest fullHttpRequest) throws Exception {
		ChannelFuture channelFuture= channel.writeAndFlush(fullHttpRequest);
		return channelFuture;
	}
	
	
	
}

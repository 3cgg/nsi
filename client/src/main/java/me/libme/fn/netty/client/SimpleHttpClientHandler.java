package me.libme.fn.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import me.libme.fn.netty.client.msg.MsgHeader;
import me.libme.fn.netty.client.msg.ResponseBody;
import me.libme.fn.netty.client.msg.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ChannelHandler.Sharable
public class SimpleHttpClientHandler extends SimpleChannelInboundHandler<FullHttpMessage> {

	private static final Logger LOGGER= LoggerFactory.getLogger(SimpleHttpClientHandler.class);

	public SimpleHttpClientHandler() {
	}
	
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	super.channelReadComplete(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) {

		SimpleResponse simpleResponse=new SimpleResponse();

		MsgHeader msgHeader=new MsgHeader();

    	HttpHeaders headers = msg.headers();
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> h: headers) {
                String key = h.getKey();
                String value = h.getValue();
				msgHeader.addEntry(key,value);
            }
        }
		simpleResponse.setMsgHeader(msgHeader);
    	
    	ByteBuf content=msg.content();
    	if(content.isReadable()){
    		byte[] bytes=new byte[content.capacity()];
    		content.readBytes(bytes);
			ResponseBody responseBody=new ResponseBody(bytes);
			simpleResponse.setBody(responseBody);
    	}

    	ctx.fireChannelRead(simpleResponse);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	try{
    		cause.printStackTrace();
            ctx.close();
    	}finally{
    		super.exceptionCaught(ctx, cause);
    	}
    }
}
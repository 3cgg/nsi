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
import me.libme.fn.netty.msg.HeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ChannelHandler.Sharable
public class SimpleHttpClientHandler extends SimpleChannelInboundHandler<FullHttpMessage> {

	private static final Logger LOGGER= LoggerFactory.getLogger(SimpleHttpClientHandler.class);

	private static final PromiseRepo PROMISE_REPO=PromiseRepo.get();

//	private static final ReleaseChannelInboundHandler RELEASE_CHANNEL_INBOUND_HANDLER=new ReleaseChannelInboundHandler();

	private static final String title="------------%s------------";

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


		if(LOGGER.isDebugEnabled()){
			StringBuffer stringBuffer=new StringBuffer();
			if (!headers.isEmpty()) {
				stringBuffer.append(String.format(title, "RESPONSE HEADER")).append("\r\n");
				for (Map.Entry<String, String> h: headers) {
					String key = h.getKey();
					String value = h.getValue();
					stringBuffer.append(key).append(" = ").append(value).append("\r\n");
				}
				stringBuffer.append("\r\n");
			}
			LOGGER.debug(stringBuffer.toString());
		}

		simpleResponse.setMsgHeader(msgHeader);
    	
    	ByteBuf content=msg.content();
    	if(content.isReadable()){
    		byte[] bytes=new byte[content.capacity()];
    		content.readBytes(bytes);
			ResponseBody responseBody=new ResponseBody(bytes);
			simpleResponse.setBody(responseBody);
    	}

    	try {
			String sequence=simpleResponse.sequenceIdentity();
			CallPromise callPromise=PROMISE_REPO.get(sequence);
			LOGGER.info("receive message ["+sequence+" , "+simpleResponse.getHeader(HeaderNames.REQUEST_URL_IDENTITY)+"], in channel : " +ctx.channel());

//		ChannelHandler channelHandler=ctx.pipeline().get("releaseChannelHandler");
//		if(channelHandler==null){
//			LOGGER.error("pipeline error[missing releaseChannelHandler] : "+ JJSON.get().format(simpleResponse));
//		}

//		ctx.channel().pipeline()
//				.addLast("setResponseHandler" ,
//				new ReturnResponseInboundHandler(callPromise))
//				.addLast("releaseChannelHandler",RELEASE_CHANNEL_INBOUND_HANDLER);
			callPromise.setResponse(simpleResponse);
		}catch (Exception e){
    		throw new RuntimeException(e);
		}finally {
			PROMISE_REPO.remove(simpleResponse.sequenceIdentity());
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
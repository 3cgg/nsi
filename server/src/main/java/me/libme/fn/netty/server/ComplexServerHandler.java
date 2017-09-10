package me.libme.fn.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;

public class ComplexServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger LOGGER= LoggerFactory.getLogger(ComplexServerHandler.class);

	private static final QueryStringParser QUERY_STRING_PARSER=new QueryStringParser();

	private static final FormParamParser FORM_PARAM_PARSER=new FormParamParser();

	private static final String title="------------%s------------";

    private static final SimpleRequestHandler SIMPLE_REQUEST_HANDLER=new SimpleRequestHandler();



	public ComplexServerHandler() {

	}
	
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
    	
    	if(LOGGER.isDebugEnabled()){
    		LOGGER.debug("access to url : "+msg.uri());
    	}
    	
    	HttpHeaders headers = msg.headers();
    	if(LOGGER.isDebugEnabled()){
    		StringBuffer stringBuffer=new StringBuffer();
            if (!headers.isEmpty()) {
            	stringBuffer.append(String.format(title, "REQUEST HEADER")).append("\r\n");
                for (Entry<String, String> h: headers) {
                    String key = h.getKey();
                    String value = h.getValue();
                    stringBuffer.append(key).append(" = ").append(value).append("\r\n");
                }
                stringBuffer.append("\r\n");
            }
            LOGGER.debug(stringBuffer.toString());
    	}

    	final SimpleHttpRequest httpRequest=new SimpleHttpRequest(msg, ctx);

		QUERY_STRING_PARSER.parse(httpRequest);

		FORM_PARAM_PARSER.parse(httpRequest);

        SIMPLE_REQUEST_HANDLER.handle(httpRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
package me.libme.fn.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import me.libme.fn.netty.util.JStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;

import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspHeaders.Names.CONNECTION;
import static io.netty.handler.codec.stomp.StompHeaders.CONTENT_LENGTH;

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
    	

        LOGGER.debug("access to url : "+msg.uri());

    	HttpHeaders headers = msg.headers();

        String val= headers.get("HEAD-FOR-TEST");
        if("HEAD-FOR-TEST".equals(val)){
            StringBuffer stringBuffer=new StringBuffer();
            if (!headers.isEmpty()) {
                stringBuffer.append(String.format(title, "REQUEST HEADER")).append("\r\n");
                for (Entry<String, String> h: headers) {
                    String key = h.getKey();
                    String value = h.getValue();
                    stringBuffer.append(key).append(" = ").append(value).append("\r\n");
                }
                stringBuffer.append("\r\n");

                boolean keepAlive = HttpUtil.isKeepAlive(msg);
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(JStringUtils.utf8(stringBuffer.toString())));
                response.headers().set(CONTENT_TYPE, "text/plain");
                response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

                if (!keepAlive) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
                return;
            }

        }

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
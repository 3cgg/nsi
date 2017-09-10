package me.libme.fn.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SimpleHttpServerHandler extends SimpleChannelInboundHandler<FullHttpMessage> {

	private static final Logger LOGGER= LoggerFactory.getLogger(SimpleHttpServerHandler.class);
	
	private static final String title="------------%s------------";
	
	public SimpleHttpServerHandler() {
	}
	
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) {
    	StringBuffer stringBuffer=new StringBuffer();
    	HttpHeaders headers = msg.headers();
        if (!headers.isEmpty()) {
        	stringBuffer.append(String.format(title, "REQUEST HEADER")).append("\r\n");
            for (Map.Entry<String, String> h: headers) {
                String key = h.getKey();
                String value = h.getValue();
                stringBuffer.append(key).append(" = ").append(value).append("\r\n");
            }
            stringBuffer.append("\r\n");
        }
    	
    	ByteBuf content=msg.content();
    	if(content.isReadable()){
    		stringBuffer.append(String.format(title, "REQUEST CONTENT")).append("\r\n");
    		byte[] bytes=new byte[content.capacity()];
    		content.readBytes(bytes);
    		stringBuffer.append(new String(bytes,Charset.forName("utf-8")));
    	}
        
        writeResponse((HttpObject) msg, ctx,stringBuffer);
    }
    
    private void writeResponse(HttpObject currentObj, ChannelHandlerContext ctx,StringBuffer stringBuffer) {
        // Decide whether to close the connection or not.
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, currentObj.decoderResult().isSuccess()? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(stringBuffer.toString(), CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Add 'Content-Length' header only for a keep-alive connection.
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        // Add keep alive header as per:
        // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
//        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        
        // Write the response.
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
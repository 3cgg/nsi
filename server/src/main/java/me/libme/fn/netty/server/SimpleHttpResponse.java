package me.libme.fn.netty.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import me.libme.fn.netty.util.JStringUtils;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by J on 2017/9/7.
 */
public class SimpleHttpResponse implements HttpResponse {

    private final ChannelHandlerContext ctx;

    private final SimpleHttpRequest httpRequest;

    private final MsgHeader msgHeader=new MsgHeader();

    public SimpleHttpResponse(ChannelHandlerContext ctx,SimpleHttpRequest httpRequest) {
        this.ctx = ctx;
        this.httpRequest=httpRequest;
    }

//    private List<String> contents=new ArrayList<>();
//
//    @Override
//    public void append(String content) {
//        contents.add(content);
//    }

    @Override
    public void write(byte[] content) {

        HttpObject httpObject= httpRequest.getFullHttpRequest();
        // Decide whether to close the connection or not.
        // Build the response object.
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, httpObject.decoderResult().isSuccess()? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(content));

        String contentType=msgHeader.get(HttpHeaderNames.CONTENT_TYPE.toString());

        if(JStringUtils.isNullOrEmpty(contentType)){
            contentType="text/plain; charset=UTF-8";
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,contentType);

        // Add 'Content-Length' header only for a keep-alive connection.
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        // Add keep alive header as per:
        // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
        final boolean keepAlive= HttpUtil.isKeepAlive((HttpMessage) httpObject);
        if(keepAlive){
            response.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.KEEP_ALIVE);
        }else{
            response.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.CLOSE);
        }
        // Write the response, should use embedded IO thread.
        if(ctx.executor().inEventLoop()){
            ctx.writeAndFlush(response);
            if(!keepAlive){
                ctx.close();
            }
        }else{
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    ctx.writeAndFlush(response);
                    if(!keepAlive){
                        ctx.close();
                    }
                }
            });
        }
    }

    /**
     *
     * @param name HttpHeaderNames.CONTENT_TYPE
     * @param value
     */
    @Override
    public void addHeader(String name, String value) {
        msgHeader.addEntry(name,value);
    }


}

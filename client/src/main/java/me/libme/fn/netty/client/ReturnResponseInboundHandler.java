package me.libme.fn.netty.client;

import io.netty.channel.ChannelHandlerContext;
import me.libme.fn.netty.client.msg.SimpleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by J on 2017/9/12.
 */
@Deprecated
public class ReturnResponseInboundHandler extends OnceChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReturnResponseInboundHandler.class);

    private final CallPromise callPromise;

    private static final PromiseRepo PROMISE_REPO=PromiseRepo.get();

    public ReturnResponseInboundHandler(CallPromise callPromise) {
        this.callPromise = callPromise;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        SimpleResponse simpleResponse=(SimpleResponse)msg;
        callPromise.setResponse(simpleResponse);
        PROMISE_REPO.remove(simpleResponse.sequenceIdentity());
        LOGGER.info("consume response :  " + simpleResponse);
        super.channelRead(ctx, simpleResponse);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}

package me.libme.fn.netty.client.msg;

import me.libme.fn.netty.msg.BodyDecoder;
import me.libme.fn.netty.msg.HeaderNames;

/**
 * Created by J on 2017/9/7.
 */
public class SimpleResponse implements IResponse {

    private MsgHeader msgHeader;

    private ResponseBody body;

    public <T> T decode(BodyDecoder<T> bodyDecoder){
        return bodyDecoder.decode(body.body());
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public String getHeader(String name){
        return msgHeader.get(name);
    }

    @Override
    public String sequenceIdentity() {
        return getHeader(HeaderNames.SEQUENCE_IDENTITY);
    }
}

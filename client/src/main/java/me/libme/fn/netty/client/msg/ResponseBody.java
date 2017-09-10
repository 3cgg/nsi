package me.libme.fn.netty.client.msg;

/**
 * Created by J on 2017/9/7.
 */
public class ResponseBody implements IMessageBody {

    private final byte[] body;

    public ResponseBody(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] body() {
        return body;
    }



}

package me.libme.fn.netty.client.msg;

import me.libme.fn.netty.MessageBody;
import me.libme.fn.netty.util.JStringUtils;

/**
 * Created by J on 2017/9/6.
 */
@MessageBody
public class PlainMsgBody implements IMessageBody {

    private Object body;


    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public byte[] body() {
        if(body instanceof byte[]){
            return (byte[]) body;
        }else if(body instanceof String){
            return JStringUtils.utf8((String) body);
        }else{
            throw new RuntimeException("only accept string , byte[]");
        }
    }
}

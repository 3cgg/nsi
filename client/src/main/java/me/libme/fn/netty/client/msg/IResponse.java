package me.libme.fn.netty.client.msg;

import me.libme.fn.netty.msg.BodyDecoder;

/**
 * Created by J on 2017/9/7.
 */
public interface IResponse {

    public <T> T decode(BodyDecoder<T> bodyDecoder);


    public String getHeader(String name);


    String sequenceIdentity();

}

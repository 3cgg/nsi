package me.libme.fn.netty.server;

/**
 * Created by J on 2017/9/7.
 */
public interface HttpResponse {

    void write(byte[] content);

    void addHeader(String name ,String value);


}

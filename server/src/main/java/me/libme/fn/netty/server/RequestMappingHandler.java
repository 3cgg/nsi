package me.libme.fn.netty.server;

/**
 * Created by J on 2017/9/7.
 */
public interface RequestMappingHandler {

    void handle(HttpRequest httpRequest);

}

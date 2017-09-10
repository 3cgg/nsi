package me.libme.fn.netty.server.spring;

import me.libme.fn.netty.json.JJSON;
import me.libme.fn.netty.server.HttpRequest;
import me.libme.fn.netty.server.HttpResponse;
import me.libme.fn.netty.server.RequestMappingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * Created by J on 2017/9/7.
 */
@Component
public class SimpleControllerHandler implements RequestMappingHandler{

    @Autowired
    private _Test4NettyController_ test4NettyController;

    @Override
    public void handle(HttpRequest httpRequest) {

        String name= httpRequest.getParam("name");
        String result=test4NettyController.name(name);

        HttpResponse httpResponse=httpRequest.getHttpResponse();

        httpResponse.addHeader("RECEIVER","J");

        httpResponse.write(JJSON.get().format(result).getBytes(Charset.forName("utf-8")));

    }


}

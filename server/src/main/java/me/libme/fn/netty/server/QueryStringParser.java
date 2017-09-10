package me.libme.fn.netty.server;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * Created by J on 2017/9/7.
 */
public class QueryStringParser implements ParamParser {


    @Override
    public void parse(HttpRequest httpRequest) {

        SimpleHttpRequest simpleHttpRequest=(SimpleHttpRequest)httpRequest;
        QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.getUrl());
        for(Map.Entry<String, List<String>> entry:decoder.parameters().entrySet()){
            List<String> values=entry.getValue();
            simpleHttpRequest.addQueryParam(entry.getKey(),entry.getValue());
        }

    }


}

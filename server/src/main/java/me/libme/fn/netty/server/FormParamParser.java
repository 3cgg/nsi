package me.libme.fn.netty.server;

import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;

/**
 * Created by J on 2017/9/7.
 */
public class FormParamParser implements ParamParser {

    @Override
    public void parse(HttpRequest httpRequest) {

        SimpleHttpRequest simpleHttpRequest=(SimpleHttpRequest)httpRequest;
        HttpPostRequestDecoder httpPostRequestDecoder=new HttpPostRequestDecoder(simpleHttpRequest.getFullHttpRequest());
        try{
            while(httpPostRequestDecoder.hasNext()){
                InterfaceHttpData interfaceHttpData= httpPostRequestDecoder.next();
                if (interfaceHttpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) interfaceHttpData;
                    try {
                        simpleHttpRequest.addFormAttibute(attribute.getName(), attribute.getValue());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }catch (HttpPostRequestDecoder.EndOfDataDecoderException e){
            //ignore...
        }

    }


}

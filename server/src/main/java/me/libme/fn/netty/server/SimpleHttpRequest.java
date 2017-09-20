package me.libme.fn.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import me.libme.fn.netty.msg.HeaderNames;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by J on 2017/9/7.
 */
public class SimpleHttpRequest implements HttpRequest {

    private final FullHttpRequest fullHttpRequest;

    private final SimpleHttpResponse httpResponse;

    public SimpleHttpRequest(FullHttpRequest fullHttpRequest,ChannelHandlerContext ctx) {
        this.fullHttpRequest = fullHttpRequest;
        this.httpResponse=new SimpleHttpResponse(ctx,this);
    }

    private Map<String,List<String>> queryStrings=new HashMap<>();

    private Map<String,List<String>> form=new HashMap<>();


    void addQueryParam(String name,List<String> value){
        queryStrings.put(name,value);
    }


    void addFormAttibute(String name,String value){

        List<String> values=form.get(name);
        if(values==null){
            values=new ArrayList<>(1);
            values.add(value);
            form.put(name,values);
        }else {
            values.add(value);
        }
    }

    void addFormAttibute(String name,List<String> value){

        List<String> values=form.get(name);
        if(values==null){
            values=new ArrayList<>(1);
            values.addAll(value);
            form.put(name,values);
        }else {
            values.addAll(value);
        }
    }




    FullHttpRequest getFullHttpRequest() {
        return fullHttpRequest;
    }

    @Override
    public String getHeader(String name) {
        HttpHeaders headers = fullHttpRequest.headers();
        return headers.get(name);
    }

    @Override
    public String[] getParams(String name) {

        //from query string first
        List<String> value=queryStrings.get(name);
        if(value==null){
            //then from form
            value=form.get(name);
            if(value==null){
                return null;
            }
        }

        if(value.size()==1){
            throw new RuntimeException("only one value found.");
        }

        String[] val=new String[value.size()];
        value.toArray(val);
        return val;
    }

    @Override
    public String getParam(String name) {

        //from query string first
        List<String> value=queryStrings.get(name);
        if(value==null){
            //then from form
            value=form.get(name);
            if(value==null){
                return null;
            }
        }

        if(value.size()==1){
            return value.get(0);
        }else{
            throw new RuntimeException("more than one value found.");
        }
    }

    @Override
    public String getPath() {
        try {
            return new java.net.URI(getUrl()).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUrl() {
        return fullHttpRequest.uri();
    }

    @Override
    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public String sequenceIdentity() {
        return getHeader(HeaderNames.SEQUENCE_IDENTITY);
    }


}

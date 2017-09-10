package test.me.libme.fn.netty.client;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import me.libme.fn.netty.client.CallPromise;
import me.libme.fn.netty.client.NioChannelRunnable;
import me.libme.fn.netty.client.SimpleChannelExecutor;
import me.libme.fn.netty.client.SimpleRequest;
import me.libme.fn.netty.client.msg.FormMsgBody;
import me.libme.fn.netty.client.msg.IResponse;
import me.libme.fn.netty.msg.BodyDecoder;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by J on 2017/9/7.
 */

public class ClientTest {

    private SimpleChannelExecutor simpleChannelExecutor;

    private final BodyDecoder.SimpleJSONDecoder<String> DECODER=new BodyDecoder.SimpleJSONDecoder(String.class);


    private ExecutorService executorService= Executors.newFixedThreadPool(20);

    @Before
    public void prepare(){
        simpleChannelExecutor= new SimpleChannelExecutor("127.0.0.1",8655);
    }

    @Test
    public void call(){


        for(int i=0;i<1000;i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    SimpleRequest simpleRequest=SimpleRequest.post();
                    simpleRequest.setUrl(simpleChannelExecutor.uri()+"/_test4netty_/name");
                    FormMsgBody formMsgBody=new FormMsgBody();
                    final String name="J-"+new Random().nextInt(9999);
                    formMsgBody.addEntry("name",name);

                    formMsgBody.addEntry("age",20);
                    formMsgBody.addEntry("sex","&8%Male%^&");
                    formMsgBody.addEntry("seq","09");

                    simpleRequest.setMessageBody(formMsgBody);

                    simpleRequest.addHeader(HttpHeaderNames.CONNECTION.toString(),HttpHeaderValues.KEEP_ALIVE.toString())
                            .addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                            .addHeader(HttpHeaderNames.ACCEPT_ENCODING.toString(),HttpHeaderValues.GZIP.toString());
                    try {
                        CallPromise callPromise= simpleChannelExecutor.execute(new NioChannelRunnable(simpleRequest));
                        IResponse response= (IResponse) callPromise.get();
                        String resultName= response.decode(DECODER);
                        if(!resultName.contains(name)){
                            throw new RuntimeException("response : "+resultName +"; request : "+name);
                        }
                        System.out.println("response : "+resultName +"; request : "+name);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }



        System.out.println("end-");
        try {
            Thread.sleep(1000*60*60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}

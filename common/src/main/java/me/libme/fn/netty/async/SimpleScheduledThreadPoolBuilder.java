package me.libme.fn.netty.async;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by J on 2017/8/8.
 */
public class SimpleScheduledThreadPoolBuilder extends
        ThreadPoolBuilder<SimpleScheduledThreadPoolBuilder,ScheduledThreadPoolExecutor> {


    private SimpleScheduledThreadPoolBuilder() {

    }

    private static final SimpleScheduledThreadPoolBuilder INSTANCE=
            new SimpleScheduledThreadPoolBuilder();

    public static SimpleScheduledThreadPoolBuilder get(){
        return INSTANCE;
    }


    protected ScheduledThreadPoolExecutor _build(){

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor
                =new ScheduledThreadPoolExecutor(
                        aliveCount(),threadFactory(),rejectedExecutionHandler());
        return scheduledThreadPoolExecutor;
    }



}

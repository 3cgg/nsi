package me.libme.fn.netty.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by J on 2017/8/8.
 */
public class SimpleThreadPoolBuilder extends
        ThreadPoolBuilder<SimpleThreadPoolBuilder,ThreadPoolExecutor> {


    private SimpleThreadPoolBuilder() {

    }

    private static final SimpleThreadPoolBuilder INSTANCE=
            new SimpleThreadPoolBuilder();

    public static SimpleThreadPoolBuilder get(){
        return INSTANCE;
    }


    protected ThreadPoolExecutor _build(){

        ThreadPoolExecutor executor =new ThreadPoolExecutor
                (aliveCount(), maxCount(), aliveTime(), TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(), threadFactory()
                        ,rejectedExecutionHandler());
        return executor;
    }



}

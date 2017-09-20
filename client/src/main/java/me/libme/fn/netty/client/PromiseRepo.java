package me.libme.fn.netty.client;

import me.libme.fn.netty.cache.JCacheService;
import me.libme.fn.netty.cache.JMapCacheService;

import java.util.concurrent.TimeUnit;

/**
 * Created by J on 2017/9/12.
 */
public class PromiseRepo implements JCacheService<String,CallPromise> {

    private static final PromiseRepo INSTANCE=new PromiseRepo();

    private PromiseRepo(){}

    public static PromiseRepo get(){
        return INSTANCE;
    }

    private JMapCacheService<String,CallPromise> backed=new JMapCacheService<>();

    @Override
    public CallPromise expire(String key, CallPromise object, long time, TimeUnit timeUnit) {
        return backed.expire(key,object,time,timeUnit);
    }

    @Override
    public CallPromise expire(String key, CallPromise object) {
        return backed.expire(key, object);
    }

    @Override
    public CallPromise put(String key, CallPromise object) {
        return backed.put(key, object);
    }

    @Override
    public CallPromise get(String key) {
        return backed.get(key);
    }

    @Override
    public CallPromise remove(String key) {
        return backed.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return backed.contains(key);
    }


}

package me.libme.fn.netty.async;

import java.util.concurrent.*;

/**
 * Created by J on 2017/8/8.
 */
public abstract class ThreadPoolBuilder<O extends ThreadPoolBuilder,T extends Executor> {

    protected ThreadConfig threadConfig;

    private RejectedExecutionHandler rejectedExecutionHandler;

    public O corePoolSize(int corePoolSize) {
        this.threadConfig.setAliveCount(corePoolSize);
        return (O) this;
    }

    public O maximumPoolSize(int maximumPoolSize) {
        this.threadConfig.setMaxCount(maximumPoolSize);
        return (O) this;
    }

    public O threadName(String threadName) {
        this.threadConfig.setName(threadName);
        return (O) this;
    }

    public O abortIfLess(){
        rejectedExecutionHandler=new
                ScheduledThreadPoolExecutor.AbortPolicy();
        return (O)this;
    }

    public O callerRunsIfLess(){
        rejectedExecutionHandler=new
                ScheduledThreadPoolExecutor.CallerRunsPolicy();
        return (O)this;
    }

    public O discardOldestOIfLess(){
        rejectedExecutionHandler=new
                ScheduledThreadPoolExecutor.DiscardOldestPolicy();
        return (O)this;
    }


    public O discardOIfLess(){
        rejectedExecutionHandler=new
                ScheduledThreadPoolExecutor.DiscardPolicy();
        return (O)this;
    }

    public O daemon(boolean daemon){
        this.threadConfig.setDaemon(daemon);
        return (O) this;
    }


    protected ThreadFactory threadFactory(){
        return new ThreadFactory() {
            int index=0;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread= new Thread(r,"thread-("+(threadConfig.getName())+")-"+(++index));
                thread.setDaemon(threadConfig.isDaemon());
                return thread;
            }
        };
    }

    public final T build(){
        T executor= _build();
        if(ThreadPoolExecutor.class.isInstance(executor)){
            ThreadPoolExecutor threadPoolExecutor= (ThreadPoolExecutor) executor;
            threadPoolExecutor.setMaximumPoolSize(maxCount());
            threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler());
            threadPoolExecutor.setCorePoolSize(aliveCount());
            threadPoolExecutor.setKeepAliveTime(aliveTime(),TimeUnit.SECONDS);
            threadPoolExecutor.setThreadFactory(threadFactory());
        }
        return executor;
    }

    protected abstract T _build();

    public O threadConfig(ThreadConfig threadConfig) {
        this.threadConfig = threadConfig;
        return (O)this;
    }

    protected int aliveCount(){
        return this.threadConfig.getAliveCount();
    }

    protected int maxCount(){
        int max= this.threadConfig.getMaxCount();
        if(max==0){
            max=new Double(Math.ceil(this.aliveCount()*1.8)).intValue();
        }
        return max;
    }

    protected int aliveTime(){
        return this.threadConfig.getAliveCount();
    }

    protected RejectedExecutionHandler rejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

}

package me.libme.fn.netty.async;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SimpleScheduledThreadPoolExecutorFactory implements
		ExecutorServiceFactory<ScheduledThreadPoolExecutor>{

	private ThreadConfig config;

	public SimpleScheduledThreadPoolExecutorFactory(ThreadConfig config) {
		this.config = config;
	}

	@Override
	public ScheduledThreadPoolExecutor getObject() throws Exception {

		ScheduledThreadPoolExecutor executor=
				SimpleScheduledThreadPoolBuilder.get()
				.threadConfig(config)
				.callerRunsIfLess()
				.build();

		/*
		ThreadPoolExecutor executor =new ThreadPoolExecutor
				(config.getAliveCount(), config.getMaxCount(), config.getAliveTime(), TimeUnit.SECONDS, 
						new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
							int index=0;
							@Override
							public Thread newThread(Runnable r) {
								return new Thread(r,"thread-("+(config.getName())+")-"+(++index));
							}
						},new ThreadPoolExecutor.CallerRunsPolicy());
						*/
		
		return executor;
	
	}
	
}

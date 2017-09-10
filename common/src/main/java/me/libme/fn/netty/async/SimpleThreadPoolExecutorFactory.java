package me.libme.fn.netty.async;

import java.util.concurrent.ThreadPoolExecutor;

public class SimpleThreadPoolExecutorFactory  implements
		ExecutorServiceFactory<ThreadPoolExecutor>{
	
	private ThreadConfig config;
	
	public SimpleThreadPoolExecutorFactory(ThreadConfig config) {
		this.config = config;
	}

	@Override
	public ThreadPoolExecutor getObject() throws Exception {

		ThreadPoolExecutor executor=
				SimpleThreadPoolBuilder.get()
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

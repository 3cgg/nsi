package me.libme.fn.netty.async;


import java.util.concurrent.ExecutorService;

public interface ExecutorServiceFactory<T extends ExecutorService> {
	
	public T getObject() throws Exception;

}

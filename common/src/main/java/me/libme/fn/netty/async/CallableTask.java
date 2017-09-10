package me.libme.fn.netty.async;


import me.libme.fn.netty.util.JUniqueUtils;

import java.util.concurrent.Callable;

public abstract class CallableTask<V> implements Callable<V> {
	
	 protected String name(){
		 return JUniqueUtils.unique();
	 }
	
}

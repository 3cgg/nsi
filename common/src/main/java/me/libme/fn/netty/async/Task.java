package me.libme.fn.netty.async;


import me.libme.fn.netty.util.JUniqueUtils;

public abstract class Task implements Runnable {
	
	 protected String name(){
		 return JUniqueUtils.unique();
	 }
	
}

package me.libme.fn.netty.client;

import java.lang.reflect.Method;

public class SimpleControllerAsyncCall implements ControllerAsyncCall {

	@Override
	public void success(Object proxy, Method method, Object[] args, Object returnVal) {
	}

	@Override
	public void fail(Object proxy, Method method, Object[] args, Throwable throwable) {
		throw new RuntimeException(throwable);
	}

}

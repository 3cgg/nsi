package me.libme.fn.netty.async;

public abstract class Call<I,O> {
	
	private boolean done;
	
	public abstract O call(I input);

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	
	
	
}

package me.libme.fn.netty.async;

public abstract class Catch {
	
	private boolean done;
	
	public abstract void cat(Throwable error);

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	
}

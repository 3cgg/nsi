package me.libme.fn.netty.async;

import java.util.concurrent.TimeUnit;

public class ThreadConfig {
	
	/**
	 * the name
	 */
	private String name;
	
	private int aliveCount;
	
	private int maxCount;
	
	private long aliveTime;

	private boolean daemon=false;
	
	public long getAliveTime() {
		return aliveTime;
	}

	/**
	 * TimeUnit.SECONDS
	 * @param aliveTime {@link TimeUnit#SECONDS}
	 */
	public void setAliveTime(long aliveTime) {
		this.aliveTime = aliveTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAliveCount() {
		return aliveCount;
	}

	public void setAliveCount(int aliveCount) {
		this.aliveCount = aliveCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}
}

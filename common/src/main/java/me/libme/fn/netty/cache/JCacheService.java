package me.libme.fn.netty.cache;

import java.util.concurrent.TimeUnit;

public interface JCacheService<K ,V>{

	/**
	 * put object into cache, with an expired time
	 * @param key
	 * @param object
	 * @return the previous object related to the key if any
	 */
	public V expire(K key, V object, long time, TimeUnit timeUnit);

	/**
	 * put object into cache, with a default expired time
	 * @param key
	 * @param object
	 * @return the previous object related to the key if any
	 */
	public V expire(K key, V object);


	/**
	 * put object into cache，generally the object is never expired
	 * @param key
	 * @param object
	 * @return the previous object related to the key if any
	 */
	public V put(K key, V object);
	
	/**
	 * always get cached object if the key is existing.  
	 * @param key
	 * @return
	 */
	public V get(K key);
	
	/**
	 * remove object from cache, it means frequently the method {@link #get(Object)} never get anyone
	 * @param key
	 * @return
	 */
	public V remove(K key);
	
	/**
	 * test whether the key is cached.
	 * @param key
	 * @return
	 */
	public boolean contains(K key);
	
}

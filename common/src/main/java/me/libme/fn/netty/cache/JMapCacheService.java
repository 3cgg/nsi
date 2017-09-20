package me.libme.fn.netty.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JMapCacheService<K,V> implements JCacheService<K,V> {

	private Map<K, Entry> repo=new ConcurrentHashMap<>();

	private ScheduledExecutorService executorService= Executors.newScheduledThreadPool(1);
	{
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				for(Map.Entry<K,Entry> entryEntry : repo.entrySet()){
					Entry entry=entryEntry.getValue();
					if((System.currentTimeMillis()-entry.start)>entry.expired){
						repo.remove(entry.getKey());
					}
				}
			}
		},10,60,TimeUnit.SECONDS);
	}

	public class Entry{
		private K key;
		private V value;

		private long start; // millisecond

		private long expired; // millisecond


		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}
	}

	
	@Override
	public V expire(K key, V object, long time, TimeUnit timeUnit) {

		long expired=timeUnit.toMillis(time);
		Entry entry=new Entry();
		entry.key=key;
		entry.value=object;
		entry.start=System.currentTimeMillis();
		entry.expired=expired;
		Entry org=repo.put(key, entry);
		return org==null?null:org.value;
	}

	@Override
	public V expire(K key, V object) {
		return expire(key, object, -1, TimeUnit.SECONDS);
	}

	@Override
	public V put(K key, V object) {
		Entry entry=new Entry();
		entry.key=key;
		entry.value=object;
		Entry org=repo.put(key, entry);
		return org==null?null:org.value;
	}

	@Override
	public V get(K key) {
		return repo.get(key).value;
	}

	@Override
	public V remove(K key) {
		return repo.remove(key).value;
	}

	@Override
	public boolean contains(K key) {
		return repo.containsKey(key);
	}
	
	
}

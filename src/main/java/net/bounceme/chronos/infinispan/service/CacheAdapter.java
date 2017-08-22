package net.bounceme.chronos.infinispan.service;

import org.infinispan.commons.api.BasicCache;

public class CacheAdapter<K, V> {
	private BasicCache<K, V> cache;
	
	public CacheAdapter(BasicCache<K, V> cache) {
		this.cache = cache;
	}

	public void put(K key, V value) {
		cache.put(key, value);
	}
	
	public V get(K key) {
		return cache.get(key);
	}
	
	public void remove(K key) {
		cache.remove(key);
	}
}

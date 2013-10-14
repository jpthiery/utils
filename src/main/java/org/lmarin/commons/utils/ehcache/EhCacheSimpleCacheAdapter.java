package org.lmarin.commons.utils.ehcache;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class EhCacheSimpleCacheAdapter<K extends Serializable, V extends Serializable> implements SimpleCache<K, V>{

	private final Cache cache;	
	
	public EhCacheSimpleCacheAdapter(Cache cache) {
		super();
		if (cache == null) {
			throw new IllegalArgumentException("cache can't be null.");
		}
		this.cache = cache;
	}


	@Override
	public void put(K key, V value) {
		if (key == null) {
			throw new IllegalArgumentException("key can't be null.");
		}
		Element element = new Element(key, value);
		cache.put(element);		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V getValue(K key) {
		Element element = cache.get(key);
		if (element != null) {
			return (V) element.getObjectValue();
		}
		return null;
	}

}

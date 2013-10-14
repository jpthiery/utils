package org.lmarin.commons.utils.ehcache;

import java.io.Serializable;

public interface SimpleCache<K extends Serializable,V extends Serializable> {

	void put(K key, V value);
	
	V getValue(K key);
	
}

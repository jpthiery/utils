package org.lmarin.commons.utils.collection;

import java.util.concurrent.Callable;

public interface CallableFactory<K,V> {

	public Callable<V> createCallable(K key);
	
}

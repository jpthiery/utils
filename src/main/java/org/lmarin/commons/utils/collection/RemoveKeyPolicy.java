package org.lmarin.commons.utils.collection;

import java.util.List;

public interface RemoveKeyPolicy<K> {

	public K getKeyToRemove(List<K> keySet);
	
}

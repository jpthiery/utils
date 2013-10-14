package org.lmarin.commons.utils.ehcache.sring;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.lmarin.commons.utils.ehcache.EhCacheSimpleCacheAdapter;
import org.lmarin.commons.utils.ehcache.SimpleCache;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class EhCacheSimpleCacheAdapterFactory<K extends Serializable, V extends Serializable> extends
				AbstractFactoryBean<SimpleCache<K, V>> implements BeanNameAware , InitializingBean {

	private String beanName;
	
	private CacheManager cacheManager;
	
	public EhCacheSimpleCacheAdapterFactory() {
		super();
	}
	
	@Override
	public void setBeanName(String name) {
		beanName = name;		
	}
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (cacheManager == null) {
			throw new IllegalArgumentException("cacheManager can't be null.");
		}
		super.afterPropertiesSet();
	}
	
	@Override
	public Class<?> getObjectType() {
		return SimpleCache.class;
	}

	@Override
	protected SimpleCache<K, V> createInstance() throws Exception {
		Cache cache = cacheManager.getCache(beanName);
		EhCacheSimpleCacheAdapter<K, V> res = new EhCacheSimpleCacheAdapter<>(cache);
		return res;
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}

}

package org.lmarin.commons.utils.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A capped cache which allow concurrent access. This class is experimental.
 * @author Jean-Pascal THIERY
 *
 * @param <K> key Type
 * @param <V> value Type
 */
public class ConcurrentCache<K, V>  {
	
	private static Log log = LogFactory.getLog(ConcurrentCache.class);
	
	private static final int DEFAULT_NBELEMENT = 100;
	
	private static final boolean DEFAULT_REMOVE_BEHAVIOR = false;
	
	private final LinkedList<K> keyOrder;
	
	private final Map<K, FutureTask<V>> cache;
	
	private final ConcurrentHashMap<K, DestroyableReentrantReadWriteLock> valueLocks;
	
	private final CallableFactory<K, V> valueCreatorFactory;
	
	private final ReentrantReadWriteLock lock;
	
	private final WriteLock write;
	
	private final ReadLock read;
	
	private final ReentrantReadWriteLock orderKeyLock;
	
	private final WriteLock orderKeyWrite;
	
	private final ReadLock orderKeyRead;
	
	private final int nbElement;
	
	private final boolean removeDuringResolution;
	
	private final RemoveKeyPolicy<K> removeKeyPolicy;

	/**
	 * 
	 * @param valueCreatorFactory
	 * @param nbElement
	 * @param removeDuringResolution
	 */
	public ConcurrentCache(CallableFactory<K, V> valueCreatorFactory,
			int nbElement , boolean removeDuringResolution, RemoveKeyPolicy<K> removeKeyPolicy) {
		super();
		if (valueCreatorFactory == null) {
			throw new IllegalArgumentException("valueCreatorFactory is null.");
		}
		if (nbElement < 2) {
			throw new IllegalArgumentException("nbElement must be upper than 1.");
		}		
		this.valueCreatorFactory = valueCreatorFactory;
		this.nbElement = nbElement;
		this.removeKeyPolicy = removeKeyPolicy;
		this.removeDuringResolution = removeDuringResolution;
		this.keyOrder = new LinkedList<K>();
		this.cache = new HashMap<K, FutureTask<V>>(nbElement);
		this.valueLocks = new ConcurrentHashMap<K, DestroyableReentrantReadWriteLock>(nbElement);
		this.lock = new ReentrantReadWriteLock();
		write = lock.writeLock();		
		read = lock.readLock();
		this.orderKeyLock = new ReentrantReadWriteLock();
		orderKeyWrite = orderKeyLock.writeLock();
		orderKeyRead = orderKeyLock.readLock();
	}
	
	
	public ConcurrentCache(CallableFactory<K, V> valueCreatorFactory,
			int nbElement , boolean removeDuringResolution) {
		this(valueCreatorFactory, nbElement, removeDuringResolution, null);
	}
	
	/**
	 * 
	 * @param valueCreatorFactory
	 */
	public ConcurrentCache(CallableFactory<K, V> valueCreatorFactory) {
		this(valueCreatorFactory , DEFAULT_NBELEMENT, DEFAULT_REMOVE_BEHAVIOR, null);
	}	
	
	/**
	 * 
	 * @param valueCreatorFactory
	 * @param nbElement
	 */
	public ConcurrentCache(CallableFactory<K, V> valueCreatorFactory,
			int nbElement) {
		this(valueCreatorFactory, nbElement , DEFAULT_REMOVE_BEHAVIOR, null);
	}

	
	public final V get(K key) throws ExecutionException {
		if (key == null) {
			throw new IllegalArgumentException("key is null.");
		}
		FutureTask<V> task = null;
		FutureTask<V> previousTask = null; 
		read.lock();
		try {
			if (cache.containsKey(key)) {
				task = cache.get(key);
				setLastUseInOrdrKey(key);
			}
		} finally {
			read.unlock();
		}
		if (task == null) {
			Callable<V> call = valueCreatorFactory.createCallable(key);
			write.lock();
			try {
				while (cache.size() >= nbElement) {
					K keyRemoved = getRemovableKey(key);
					if (keyRemoved != null)
						removeKey(keyRemoved);
				}
				task = new FutureTask<V>(call);
				previousTask = cache.put(key, task);
				setLastUseInOrdrKey(key);
			} finally {
				write.unlock();
			}
			if (previousTask != null) {
				task = previousTask;
			} else {
				task.run();
			}
		}
		
		V res = null;
		try {
			res = task.get();
		} catch (InterruptedException e) {
			removeKey(key);
		} 
		
		return res;
	}
	
	public LinkedList<K> getKeyOrder() {
		LinkedList<K> res = null;
		orderKeyRead.lock();
		try {
			res = new LinkedList<K>(keyOrder);
		} finally {
			orderKeyRead.unlock();
		}
		return res;
	}
	
	private void setLastUseInOrdrKey(K key) {
		read.lock();
		try {
			orderKeyWrite.lock();
			try {
				if (cache.containsKey(key)) {
					keyOrder.remove(key);
					keyOrder.add(key);
				}
			} finally {
				orderKeyWrite.unlock();
			}
		} finally {
			read.unlock();
		}
	}
	
	private void removeKey(K key) {
		if (key == null) {
			throw new IllegalArgumentException("key is null.");
		}
		
		DestroyableReentrantReadWriteLock valueLock = getValueLock(key);
		WriteLock keyLock = valueLock.writeLock();
		keyLock.lock();
		try {
			write.lock();
			try {
				FutureTask<V> task = cache.get(key);
				if (task != null) {
					if (!task.isDone() && removeDuringResolution) {
						task.cancel(true);
					}
					cache.remove(key);
					removeKeyfromOrderKey(key);
				} else {
					if (log.isDebugEnabled())
						log.debug("Key " + key + " already removed !");
				}
			} finally {
				write.unlock();
			}
			valueLock.destroy();
		} finally {
			keyLock.unlock();
		}
		
	}
	
	private void removeKeyfromOrderKey(K key) {
		assert(key != null);
		orderKeyWrite.lock();
		try {
			keyOrder.remove(key);
		} finally {
			orderKeyWrite.unlock();
		}
	}
	
	private K getRemovableKey(K currentUseKey) {
		if (log.isTraceEnabled()) {
			log.trace("KeyOrder : " + getKeyOrder().toString());
		}
		if (removeKeyPolicy != null) {
			return removeKeyPolicy.getKeyToRemove(getKeyOrder());
		}
		orderKeyRead.lock();
		try {
			if (keyOrder.size() > 0) {
				Iterator<K> it = keyOrder.iterator();
				K old = it.next();
				while (old.equals(currentUseKey)) {
					old = it.next();
				}
				
				return old;
			}
		} finally {
			orderKeyRead.unlock();
		}
		return null;
	}
	
	private DestroyableReentrantReadWriteLock getValueLock(K key) {
		assert(key != null);
		
		DestroyableReentrantReadWriteLock res = new DestroyableReentrantReadWriteLock(key);
		DestroyableReentrantReadWriteLock previous = valueLocks.putIfAbsent(key, res);
		if (previous != null) res = previous;
		
		return res;
	}
	
	private final class DestroyableReentrantReadWriteLock extends ReentrantReadWriteLock {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private boolean delete = false;
		
		private final K key;
		
		private final DestroyableReentrantReadLock readLock;
		
		private final DestroyableReentrantWriteLock writeLock;
		
		public DestroyableReentrantReadWriteLock(K key) {
			super();
			this.key = key;
			readLock = new DestroyableReentrantReadLock(this);
			writeLock = new DestroyableReentrantWriteLock(this);
		}

		public boolean isDelete() {
			return delete;
		}

		public void destroy() {
			this.delete = true;
			if (log.isTraceEnabled())
				log.trace("Requiere destroy valueLock for key " + key.toString());
			if (getQueueLength() == 0) {
				valueLocks.remove(key, this);
				if (log.isTraceEnabled())
					log.trace("Remove valueLock for key " + key.toString() + " from master lock");
				
			}
		}
		
		public K getKey() {
			return key;
		}

		@Override
		public WriteLock writeLock() {
			return writeLock;
		}

		@Override
		public ReadLock readLock() {
			return readLock;
		}				
		
	}

	private final class DestroyableReentrantReadLock extends ReadLock {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final DestroyableReentrantReadWriteLock parent;
		
		protected DestroyableReentrantReadLock(DestroyableReentrantReadWriteLock arg0) {
			super(arg0);
			parent = arg0;
		}		
		
		@Override
		public void lock() {
			if (log.isTraceEnabled())
				log.trace("Lock read for key " + parent.getKey().toString());
			super.lock();
		}

		@Override
		public void unlock() {
			if (log.isTraceEnabled())
				log.trace("Unlock write for key " + parent.getKey().toString());
			super.unlock();
			if (parent.isDelete() && parent.getQueueLength() == 0) {
				valueLocks.remove(parent.getKey(), parent);
				if (log.isTraceEnabled())
					log.trace("Remove valueLock for key " + parent.getKey().toString() + " from read Lock");
			}
		}		
		
	}
	
	private final class DestroyableReentrantWriteLock extends WriteLock {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final DestroyableReentrantReadWriteLock parent;
		
		protected DestroyableReentrantWriteLock(DestroyableReentrantReadWriteLock arg0) {
			super(arg0);
			parent = arg0;
		}
		
		@Override
		public void lock() {
			if (log.isTraceEnabled())
				log.trace("Lock write for key " + parent.getKey().toString());
			super.lock();
		}
		
		@Override
		public void unlock() {
			if (log.isTraceEnabled())
				log.trace("Unlock write for key " + parent.getKey().toString());
			super.unlock();
			if (parent.isDelete() && parent.getQueueLength() == 0) {
				valueLocks.remove(parent.getKey(), parent);
				if (log.isTraceEnabled())
					log.trace("Remove valueLock for key " + parent.getKey().toString() + " from write Lock");
			}
		}		
		
	}
	
	/**
	 * 
	 * @return
	 */
	public final int size() {
		return cache.size();
	}	
	
	
}
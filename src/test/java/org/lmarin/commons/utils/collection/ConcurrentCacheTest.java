package org.lmarin.commons.utils.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ConcurrentCacheTest {

	private static Log log = LogFactory.getLog(ConcurrentCacheTest.class);

	@Test
	public void test() throws InterruptedException, ExecutionException,
			TimeoutException {
		CallableFactory<String, Long> valueCreatorFactory = new CallableFactory<String, Long>() {

			@Override
			public Callable<Long> createCallable(String key) {
				return new StringToIntegerValueCreator(key);
			}

		};
		ConcurrentCache<String, Long> cache = new ConcurrentCache<String, Long>(valueCreatorFactory, 30, false);

		List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
		tasks.add(new CallCache(cache, "20"));
		tasks.add(new CallCache(cache, "5"));
		tasks.add(new CallCache(cache, "6"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "1"));
		tasks.add(new CallCache(cache, "8"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "9"));
		tasks.add(new CallCache(cache, "1"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "7"));
		tasks.add(new CallCache(cache, "11"));
		tasks.add(new CallCache(cache, "10"));
		tasks.add(new CallCache(cache, "6"));
		tasks.add(new CallCache(cache, "4"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "2"));
		tasks.add(new CallCache(cache, "3"));
		tasks.add(new CallCache(cache, "3"));
		tasks.add(new CallCache(cache, "3"));
		tasks.add(new CallCache(cache, "5"));
		tasks.add(new CallCache(cache, "10"));

		ExecutorService executorService = Executors.newFixedThreadPool(8);
		CompletionService<Long> completionService = new ExecutorCompletionService<Long>(
				executorService);

		for (Callable<Long> task : tasks) {
			completionService.submit(task);
		}

		for (int i = 0; i < tasks.size(); i++) {
			Future<Long> future = completionService.take();
			if (future.isDone() && !future.isCancelled()) {
				try {
					Long res = future.get();
					log.info("Resultat : " + res);
				} catch (ExecutionException e) {
					if (e.getCause() instanceof CancellationException) {
						log.warn("- Task Cancelled !");
						log.warn(e.getCause().getMessage());
//						e.printStackTrace();
					} else {
						e.printStackTrace();
					}
				} finally {
					log.info("Get print " + i + " tasks.");
				}
			} else {
				log.warn("task concelled");
			}
			// log.info("Cache size : " + cache.size());
		}

		executorService.shutdown();

	}

	private final class CallCache implements Callable<Long> {

		private final ConcurrentCache<String, Long> cache;

		private final String key;

		public CallCache(ConcurrentCache<String, Long> cache, String key) {
			super();
			this.cache = cache;
			this.key = key;
		}

		@Override
		public Long call() throws Exception {
			log.info("Call cache for key " + key);
			return cache.get(key);
		}

	}

	private final class StringToIntegerValueCreator implements
			Callable<Long> {

		private final String key;

		public StringToIntegerValueCreator(String key) {
			super();
			this.key = key;
		}		

		@Override
		public Long call() throws Exception {

			log.info("- Contruct for key " + key);
			Long res = Long.parseLong(key);
			int cpt = 0;
			while (!Thread.currentThread().isInterrupted() && cpt++ < res) {
				log.info("Wait 1000ms more (" + cpt + "/" + res + ")");
				Thread.sleep(1000);
			}
			log.info("- Stop wait for " + key);
			return res;
		}

	}

}

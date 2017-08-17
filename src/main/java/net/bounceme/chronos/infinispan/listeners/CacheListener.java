package net.bounceme.chronos.infinispan.listeners;

import java.util.concurrent.CountDownLatch;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryExpired;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryExpiredEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.Event;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@SuppressWarnings("rawtypes")
@Listener(clustered = true)
public class CacheListener {

	Logger logger = LoggerFactory.getLogger(CacheListener.class);

	@Value("${infinispan.expectedNodes}")
	private Integer expectedNodes;

	private CountDownLatch clusterFormedLatch = new CountDownLatch(1);

	private CountDownLatch shutdownLatch = new CountDownLatch(1);

	@CacheStarted
	@CacheStopped
	public void doSomething(Event event) {
		if (event.getType() == Event.Type.CACHE_STARTED) {
			this.logger.info("Cache started.  Details = {}", event);
		}
		else if (event.getType() == Event.Type.CACHE_STOPPED) {
			this.logger.info("Cache stopped.  Details = {}", event);
		}
	}

	@CacheEntryCreated
	public void created(CacheEntryCreatedEvent event) {
		if (!event.isOriginLocal()) {
			this.logger.info("-- Entry for {} modified by another node in the cluster\n", event.getKey());
		}
	}

	@CacheEntryExpired
	public void expired(CacheEntryExpiredEvent event) {
		if (!event.isPre()) {
			this.logger.info("Entry {}, {} expired in the cache {}", event.getKey(), event.getValue(),
					event.getCache().getName());
			this.logger.info("Cache {} has {} entries", event.getCache().getName(), event.getCache().size());
		}
	}

	@ViewChanged
	public void viewChanged(ViewChangedEvent event) {
		this.logger.info("---- View changed: {} ----\n", event.getNewMembers());

		if (event.getCacheManager().getMembers().size() == expectedNodes) {
			clusterFormedLatch.countDown();
		}
		else if (event.getNewMembers().size() < event.getOldMembers().size()) {
			shutdownLatch.countDown();
		}
	}

	/**
	 * @return the clusterFormedLatch
	 */
	public CountDownLatch getClusterFormedLatch() {
		return clusterFormedLatch;
	}
}

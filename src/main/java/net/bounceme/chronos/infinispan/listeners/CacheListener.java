package net.bounceme.chronos.infinispan.listeners;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.Event;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
@Listener
public class CacheListener {

	Logger logger = LoggerFactory.getLogger(CacheListener.class);

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
		if (!event.isPre()) {
			this.logger.info("New entry {}, {} created in the cache {}", event.getKey(), event.getValue(), event.getCache().getName());
			this.logger.info("Cache {} has {} entries", event.getCache().getName(), event.getCache().size());
		}
	}

	@ViewChanged
	public void viewChanged(ViewChangedEvent event) {
		this.logger.info("Entry {} has changed", event.getViewId());
	}
}

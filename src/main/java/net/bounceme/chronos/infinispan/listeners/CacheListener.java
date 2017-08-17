package net.bounceme.chronos.infinispan.listeners;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
@Listener(clustered = true)
public class CacheListener {

	Logger logger = LoggerFactory.getLogger(CacheListener.class);

	@CacheEntryCreated
	public void created(CacheEntryCreatedEvent event) {
		if (!event.isOriginLocal()) {
			this.logger.info("-- Entry for {} modified by another node in the cluster\n", event.getKey());
		}
	}
}

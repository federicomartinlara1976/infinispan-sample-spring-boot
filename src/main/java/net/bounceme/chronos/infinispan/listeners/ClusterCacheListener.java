package net.bounceme.chronos.infinispan.listeners;

import java.util.concurrent.CountDownLatch;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Listener(clustered = true)
public class ClusterCacheListener {

	Logger logger = LoggerFactory.getLogger(ClusterCacheListener.class);

	@Value("${infinispan.expectedNodes}")
	private Integer expectedNodes;

	private CountDownLatch clusterFormedLatch = new CountDownLatch(1);

	private CountDownLatch shutdownLatch = new CountDownLatch(1);

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

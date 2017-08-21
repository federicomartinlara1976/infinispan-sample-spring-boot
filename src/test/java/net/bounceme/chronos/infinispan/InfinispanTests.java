package net.bounceme.chronos.infinispan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import net.bounceme.chronos.infinispan.config.AppConfig;
import net.bounceme.chronos.infinispan.model.LocationWeather;
import net.bounceme.chronos.infinispan.service.WeatherService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@TestPropertySource(locations = "classpath:test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InfinispanTests {
	Logger logger = LoggerFactory.getLogger(InfinispanTests.class);

	static final String[] locations = { "Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
			"London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
			"Toronto, Canada", "Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA" };

	@Autowired
	WeatherService weatherService;

	@Autowired
	Cache<String, LocationWeather> weatherCache;
	
	@Autowired
	Cache<String, String> stringCache;
	
	@Autowired
	DefaultCacheManager cacheManager;

	@Test
	public void test_AA_Setup() throws Exception {
		Assert.assertFalse(CollectionUtils.isEmpty(fetchWeathers()));
	}

	@Test
	@Ignore
	public void test_AB_CacheExpire() throws Exception {
		Assert.assertFalse(CollectionUtils.isEmpty(fetchWeathers()));

		TimeUnit.SECONDS.sleep(5);

		Assert.assertTrue(weatherCache.isEmpty());

		Assert.assertFalse(CollectionUtils.isEmpty(fetchWeathers()));
	}

	@Test
	public void test_AC_clustered() throws Exception {
		// Store the current node address in some random keys
		for (int i = 0; i < 10; i++) {
			String uuid = UUID.randomUUID().toString();
			stringCache.put(uuid, cacheManager.getNodeAddress());
			logger.info("UUID: {}, Node: {}", uuid, cacheManager.getNodeAddress());
			TimeUnit.SECONDS.sleep(10);
		}
		// Display the current cache contents for the whole cluster
		stringCache.entrySet().forEach(entry -> logger.info("{} = {}\n", entry.getKey(), entry.getValue()));
		// Display the current cache contents for this node
		stringCache.getAdvancedCache().withFlags(Flag.SKIP_REMOTE_LOOKUP).entrySet()
				.forEach(entry -> logger.info("{} = {}\n", entry.getKey(), entry.getValue()));
		// Stop the cache manager and release all resources
		cacheManager.stop();
	}

	private List<LocationWeather> fetchWeathers() {
		List<LocationWeather> weathers = new ArrayList<>();

		Arrays.asList(locations).forEach(location -> {
			LocationWeather weather = weatherService.getWeatherForLocation(location);
			logger.info("{} - {}", location, weather);
			weathers.add(weather);
		});

		return weathers;
	}
}

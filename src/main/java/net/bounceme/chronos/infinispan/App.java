package net.bounceme.chronos.infinispan;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.groupingBy;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.stream.CacheCollectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.bounceme.chronos.infinispan.listeners.ClusterCacheListener;
import net.bounceme.chronos.infinispan.model.LocationWeather;
import net.bounceme.chronos.infinispan.service.WeatherService;

@SpringBootApplication
public class App implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(App.class);

	@Autowired
	WeatherService weatherService;

	@Autowired
	ClusterCacheListener clusterCacheListener;

	@Autowired
	EmbeddedCacheManager cacheManager;
	
	@Autowired
	Cache<String, LocationWeather> cache;

	static final String[] locations = { "Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
			"London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
			"Toronto, Canada", "Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA" };

	public static void main(String[] args) {
		// disabled banner, don't want to see the spring logo
		SpringApplication app = new SpringApplication(App.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("---- Waiting for cluster to form ----");
		clusterCacheListener.getClusterFormedLatch().await();

		if (cacheManager.isCoordinator()) {
			fetchWeather();

			fetchWeather();

			TimeUnit.SECONDS.sleep(5);

			fetchWeather();
			
			computeCountryAverages();
		}
		//
		// TimeUnit.SECONDS.sleep(5);
		//
		// fetchWeather();
	}

	private void fetchWeather() {
		Arrays.asList(locations).forEach(location -> {
			LocationWeather weather = weatherService.getWeatherForLocation(location);
			logger.info("{} - {}", location, weather);
		});
	}

	private void computeCountryAverages() {
		logger.info("---- Average country temperatures ----");
		Map<String, Double> averages = cache.entrySet().stream()
	              .collect(CacheCollectors.serializableCollector(() -> groupingBy(e -> e.getValue().getCountry(),
	                      averagingDouble(e -> e.getValue().getTemperature()))));
		for (Entry<String, Double> entry : averages.entrySet()) {
			logger.info("Average temperature in {} is {}f° C\n", entry.getKey(), entry.getValue());
		}
	}
}

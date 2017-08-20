package net.bounceme.chronos.infinispan.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.remote.configuration.ExhaustedAction;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.bounceme.chronos.infinispan.listeners.CacheListener;
import net.bounceme.chronos.infinispan.listeners.ClusterCacheListener;
import net.bounceme.chronos.infinispan.model.LocationGrouper;
import net.bounceme.chronos.infinispan.model.LocationWeather;
import net.bounceme.chronos.infinispan.service.OpenWeatherMapService;
import net.bounceme.chronos.infinispan.service.RandomWeatherService;
import net.bounceme.chronos.infinispan.service.WeatherService;

@Configuration
public class AppConfig {

	@Value("${infinispan.owmapikey}")
	private String apiKey;

	@Value("${infinispan.remote}")
	private String isRemote;

	@Bean
	public GlobalConfigurationBuilder clusteredConfigurationBuilder() {
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
		global.transport().clusterName("WeatherApp");
		return global;
	}

	@Bean
	public ConfigurationBuilder configurationBuilder() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		if (!Boolean.getBoolean(isRemote)) {
			config.expiration().lifespan(5, TimeUnit.SECONDS);
			config.clustering().cacheMode(CacheMode.DIST_SYNC).hash().groups().enabled()
					.addGrouper(new LocationGrouper());
		}
		else {
			config.persistence().addStore(RemoteStoreConfigurationBuilder.class).fetchPersistentState(false)
					.ignoreModifications(false).purgeOnStartup(false).remoteCacheName("WeatherApp").rawValues(true)
					.addServer().host("anubis.salasierra12.net")
					.connectionPool().maxActive(10)
					.exhaustedAction(ExhaustedAction.CREATE_NEW).async().enable();
		}
		return config;
	}

	@Bean
	public EmbeddedCacheManager cacheManager(ConfigurationBuilder config, GlobalConfigurationBuilder global,
			ClusterCacheListener listener) throws IOException {
		EmbeddedCacheManager manager = new DefaultCacheManager(global.build(), config.build());
		// EmbeddedCacheManager manager = new
		// DefaultCacheManager(App.class.getResourceAsStream("/weatherapp-infinispan.xml"));
		manager.addListener(listener);
		return manager;
	}

	@Bean
	public CacheListener cacheListener() {
		return new CacheListener();
	}

	@Bean
	public ClusterCacheListener clusterCacheListener() {
		return new ClusterCacheListener();
	}

	@Bean
	public Cache<String, LocationWeather> cache(EmbeddedCacheManager manager, CacheListener listener) {
		manager.defineConfiguration("weather", new ConfigurationBuilder().build());
		Cache<String, LocationWeather> cache = manager.getCache("weather");
		cache.addListener(listener);
		return cache;
	}

	@Bean
	public WeatherService weatherService() {
		return (StringUtils.isNotBlank(apiKey)) ? new OpenWeatherMapService() : new RandomWeatherService();
	}
}

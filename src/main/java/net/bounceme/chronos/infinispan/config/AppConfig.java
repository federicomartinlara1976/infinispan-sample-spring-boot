package net.bounceme.chronos.infinispan.config;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.bounceme.chronos.infinispan.listeners.CacheListener;
import net.bounceme.chronos.infinispan.listeners.ClusterCacheListener;
import net.bounceme.chronos.infinispan.model.LocationGrouper;
import net.bounceme.chronos.infinispan.model.LocationWeather;
import net.bounceme.chronos.infinispan.service.CacheAdapter;
import net.bounceme.chronos.infinispan.service.OpenWeatherMapService;
import net.bounceme.chronos.infinispan.service.RandomWeatherService;
import net.bounceme.chronos.infinispan.service.WeatherService;

@Configuration
public class AppConfig {

	@Value("${infinispan.owmapikey}")
	private String apiKey;

	@Value("${infinispan.remote}")
	private String remote;
	
	@Value("${infinispan.server}")
	private String server;

	@Bean
	public GlobalConfigurationBuilder clusteredConfigurationBuilder() {
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
		global.transport().clusterName("WeatherApp");
		return global;
	}

	@Bean
	public ConfigurationBuilder configurationBuilder() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.expiration().lifespan(5, TimeUnit.SECONDS);
		config.clustering().cacheMode(CacheMode.DIST_SYNC).hash().groups().enabled().addGrouper(new LocationGrouper());
		return config;
	}

	@Bean
	public DefaultCacheManager cacheManager(ConfigurationBuilder config, GlobalConfigurationBuilder global,
			ClusterCacheListener listener) {
		DefaultCacheManager manager = new DefaultCacheManager(global.build(), config.build());
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
	public Cache<String, LocationWeather> weatherCache(EmbeddedCacheManager manager, CacheListener listener) {
		manager.defineConfiguration("weather", new ConfigurationBuilder().build());
		Cache<String, LocationWeather> cache = manager.getCache("weather");
		cache.addListener(listener);
		return cache;
	}

	@Bean
	public Cache<String, String> stringCache(EmbeddedCacheManager manager, CacheListener listener) {
		manager.defineConfiguration("string", new ConfigurationBuilder().build());
		Cache<String, String> cache = manager.getCache("string");
		cache.addListener(listener);
		return cache;
	}
	
	@Bean
	public RemoteCacheManager remoteCacheManager(org.infinispan.client.hotrod.configuration.ConfigurationBuilder builder) {
		return new RemoteCacheManager(builder.build());
	}
	
	@Bean
	public org.infinispan.client.hotrod.configuration.ConfigurationBuilder remoteCacheConfiguration() {
		org.infinispan.client.hotrod.configuration.ConfigurationBuilder remoteBuilder = new org.infinispan.client.hotrod.configuration.ConfigurationBuilder();
		remoteBuilder.addServer().host(server).port(ConfigurationProperties.DEFAULT_HOTROD_PORT);
		return remoteBuilder;
	}
	
	@Bean
	public CacheAdapter<String, String> cache(RemoteCacheManager remoteManager, DefaultCacheManager manager, ConfigurationBuilder config) {
		if (Boolean.parseBoolean(remote)) {
			return new CacheAdapter<>(remoteManager.getCache("string"));
		}
		else {
			Cache<String, String> cache = manager.getCache("string");
			cache.addListener(new CacheListener());
			return new CacheAdapter<>(cache);
		}
	}

	@Bean
	public WeatherService weatherService() {
		return (StringUtils.isNotBlank(apiKey)) ? new OpenWeatherMapService() : new RandomWeatherService();
	}
}

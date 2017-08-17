package net.bounceme.chronos.infinispan.config;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.bounceme.chronos.infinispan.listeners.CacheListener;
import net.bounceme.chronos.infinispan.model.LocationWeather;
import net.bounceme.chronos.infinispan.service.OpenWeatherMapService;
import net.bounceme.chronos.infinispan.service.RandomWeatherService;
import net.bounceme.chronos.infinispan.service.WeatherService;

@Configuration
public class AppConfig {

	@Value("${infinispan.owmapikey}")
	private String apiKey;
	
	@Bean
	public GlobalConfigurationBuilder clusteredConfigurationBuilder() {
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
		global.transport().clusterName("WeatherApp");
		return global;
	}
	
	@Bean
	public ConfigurationBuilder configurationBuilder() {
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.expiration().lifespan(5, TimeUnit.MINUTES);
		config.clustering().cacheMode(CacheMode.DIST_SYNC);
		return config;
	}

	@Bean
	public EmbeddedCacheManager cacheManager(ConfigurationBuilder config, GlobalConfigurationBuilder global) {
		 return new DefaultCacheManager(global.build(), config.build());
	}
	
	@Bean
	public CacheListener cacheListener() {
		return new CacheListener();
	}
	
	@Bean
	public Cache<String, LocationWeather> cache(EmbeddedCacheManager manager, CacheListener cacheListener) {
		manager.defineConfiguration("weather", new ConfigurationBuilder().build());
		Cache<String, LocationWeather> cache = manager.getCache("weather");
		cache.addListener(cacheListener);
		return cache;
	}
	
	@Bean
	public WeatherService weatherService() {
		return (StringUtils.isNotBlank(apiKey)) ? new OpenWeatherMapService() : new RandomWeatherService();
	}
}

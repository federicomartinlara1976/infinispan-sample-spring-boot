package net.bounceme.chronos.infinispan.service;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.springframework.beans.factory.annotation.Autowired;

import net.bounceme.chronos.infinispan.model.LocationWeather;

public abstract class CachingWeatherService implements WeatherService {
	
	@Autowired
	private Cache<String, LocationWeather> cache;

	@Override
	public final LocationWeather getWeatherForLocation(String location) {
		LocationWeather weather = cache.get(location);
		if (weather == null) {
			weather = fetchWeather(location);
			cache.put(location, weather, 5, TimeUnit.SECONDS);
		}
		return weather;
	}

	protected abstract LocationWeather fetchWeather(String location);
}

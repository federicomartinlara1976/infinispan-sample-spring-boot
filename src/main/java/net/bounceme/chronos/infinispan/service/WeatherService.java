package net.bounceme.chronos.infinispan.service;

import net.bounceme.chronos.infinispan.model.LocationWeather;

public interface WeatherService {
   LocationWeather getWeatherForLocation(String location);
}

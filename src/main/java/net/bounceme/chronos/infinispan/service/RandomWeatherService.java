package net.bounceme.chronos.infinispan.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import net.bounceme.chronos.infinispan.model.LocationWeather;

@Service
public class RandomWeatherService implements WeatherService {
   final Random random;

   public RandomWeatherService() {
      random = new Random();
   }

   @Override
   public LocationWeather getWeatherForLocation(String location) {
      try {
         TimeUnit.MILLISECONDS.sleep(200);
      } catch (InterruptedException e) {}
      String[] split = location.split(",");
      return new LocationWeather(random.nextFloat() * 20f + 5f, "sunny", split[1].trim());
   }

}

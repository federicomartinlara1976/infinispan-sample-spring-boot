package net.bounceme.chronos.infinispan.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.bounceme.chronos.infinispan.service.OpenWeatherMapService;
import net.bounceme.chronos.infinispan.service.RandomWeatherService;
import net.bounceme.chronos.infinispan.service.WeatherService;

@Configuration
public class AppConfig {

	@Value("${infinispan.owmapikey}")
	private String apiKey;

	@Bean
	public WeatherService weatherService() {
		return (StringUtils.isNotBlank(apiKey)) ? new OpenWeatherMapService() : new RandomWeatherService();
	}
}

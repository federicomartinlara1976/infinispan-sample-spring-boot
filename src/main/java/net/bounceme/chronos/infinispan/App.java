package net.bounceme.chronos.infinispan;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import net.bounceme.chronos.infinispan.model.LocationWeather;
import net.bounceme.chronos.infinispan.service.WeatherService;

@SpringBootApplication
public class App implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(App.class);

	@Autowired
	WeatherService weatherService;

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
		fetchWeather();
		
		TimeUnit.SECONDS.sleep(5);
		
		fetchWeather();
	}
		
	private void fetchWeather() {
		Arrays.asList(locations).forEach(location -> {
			LocationWeather weather = weatherService.getWeatherForLocation(location);
			logger.info("{} - {}", location, weather);
		});
	}
}

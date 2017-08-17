package net.bounceme.chronos.infinispan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import net.bounceme.chronos.infinispan.config.AppConfig;
import net.bounceme.chronos.infinispan.model.LocationWeather;
import net.bounceme.chronos.infinispan.service.WeatherService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InfinispanTests {
	Logger logger = LoggerFactory.getLogger(InfinispanTests.class);
	
	static final String[] locations = { "Rome, Italy", "Como, Italy", "Basel, Switzerland", "Bern, Switzerland",
	         "London, UK", "Newcastle, UK", "Bucarest, Romania", "Cluj-Napoca, Romania", "Ottawa, Canada",
	         "Toronto, Canada", "Lisbon, Portugal", "Porto, Portugal", "Raleigh, USA", "Washington, USA" };
	
	@Autowired 
	WeatherService weatherService;

    @Test
    public void test_AA_Setup() throws Exception {
    	List<LocationWeather> weathers = new ArrayList<>();
    	
    	Arrays.asList(locations).forEach(location -> {
			LocationWeather weather = weatherService.getWeatherForLocation(location);
			logger.info("{} - {}", location, weather);
			weathers.add(weather);
		});
    	
    	Assert.assertFalse(CollectionUtils.isEmpty(weathers));
    }
}

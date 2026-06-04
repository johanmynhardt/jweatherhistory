package za.co.johanmynhardt.jweatherhistory.impl.service;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import za.co.johanmynhardt.jweatherhistory.impl.config.AppConfig;
import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class WeatherHistoryServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(WeatherHistoryServiceTest.class);

    @Autowired
    private WeatherHistoryService weatherHistoryService;

    @Test
    public void testCreateRainEntry() throws Exception {
        assertNotNull("WeatherHistoryService should be injected by Spring", weatherHistoryService);

        RainEntry rainEntry = new RainEntry(-1, 10, "Rain Entry description", null);
        WindEntry windEntry = new WindEntry(-1, "Wind entry description", WindDirection.NORTH, 0, null);

        WeatherEntry weatherEntry = weatherHistoryService.createWeatherEntry("WeatherEntry description", new Date(), 0, 0, rainEntry, windEntry);

        logger.info("weatherEntry = {}", weatherEntry);
        
        assertNotNull("Created WeatherEntry should not be null", weatherEntry);
        assertTrue("Created WeatherEntry should have an ID > 0", weatherEntry.getId() > 0);
        
        weatherHistoryService.getAllWeatherEntries();
    }
}
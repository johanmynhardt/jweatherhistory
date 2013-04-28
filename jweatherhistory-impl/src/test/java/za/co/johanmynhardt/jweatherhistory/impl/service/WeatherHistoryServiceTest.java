package za.co.johanmynhardt.jweatherhistory.impl.service;

import java.util.Date;
import java.util.logging.Logger;

import org.junit.Test;
import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
public class WeatherHistoryServiceTest {
	public static final String CONNECTION_URL = "jdbc:derby:/tmp/weatherhistory";
	private Logger logger = Logger.getLogger(WeatherHistoryServiceTest.class.getName());

	@Test
	public void testCreateRainEntry() throws Exception {
		WeatherHistoryService weatherHistoryService = new WeatherHistoryService();

		RainEntry rainEntry = new RainEntry(-1, 10, "Rain Entry description", null);

		WindEntry windEntry = new WindEntry();
		windEntry.setWindspeed(0);
		windEntry.setWindDirection(WindDirection.NORTH);
		windEntry.setDescription("Wind entry description");

		WeatherEntry weathertry = weatherHistoryService.createWeatherEntry("WeatherEntry description", new Date(), 0, 0, rainEntry, windEntry);

		logger.info("weathertry = " + weathertry);

		weatherHistoryService.getAllWeatherEntries();
	}
}

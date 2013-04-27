package za.co.johanmynhardt.jweatherhistory.impl.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

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

	@Test
	public void testCreateRainEntry() throws Exception {
		WeatherHistoryService weatherHistoryService = new WeatherHistoryService();

		RainEntry rainEntry = new RainEntry();
		rainEntry.setVolume(10);
		rainEntry.setDescription("Rain Entry description");

		WindEntry windEntry = new WindEntry();
		windEntry.setWindspeed(0);
		windEntry.setWindDirection(WindDirection.NORTH);
		windEntry.setDescription("Wind entry description");

		WeatherEntry weathertry = weatherHistoryService.createWeatherEntry("WeatherEntry description", new Date(), rainEntry, windEntry);

		System.out.println("weathertry = " + weathertry);

		weatherHistoryService.getAllWeatherEntries();
	}
}

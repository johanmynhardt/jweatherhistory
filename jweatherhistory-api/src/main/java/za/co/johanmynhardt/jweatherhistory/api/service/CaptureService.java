package za.co.johanmynhardt.jweatherhistory.api.service;

import java.util.Date;

import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
public interface CaptureService {
	WeatherEntry createWeatherEntry(String description, Date entryDate, int minimumTemperature, int maximumTemperature, RainEntry rainEntry, WindEntry windEntry);
	RainEntry createRainEntry(String description, Integer volume, WeatherEntry weatherEntry);
	WindEntry createWindEntry(String description, WindDirection direction, Integer windSpeed, WeatherEntry weatherEntry);

	WeatherEntry createWeatherEntry(WeatherEntry weatherEntry);
}

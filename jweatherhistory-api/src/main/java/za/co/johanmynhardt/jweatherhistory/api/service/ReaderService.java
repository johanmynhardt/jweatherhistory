package za.co.johanmynhardt.jweatherhistory.api.service;

import java.util.List;

import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author Johan Mynhardt
 */
public interface ReaderService {
	List<WeatherEntry> getAllWeatherEntries();
}

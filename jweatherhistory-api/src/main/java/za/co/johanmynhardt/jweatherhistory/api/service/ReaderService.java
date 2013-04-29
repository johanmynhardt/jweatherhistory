package za.co.johanmynhardt.jweatherhistory.api.service;

import java.util.List;

import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;

/**
 * @author Johan Mynhardt
 */
public interface ReaderService {
	List<WeatherEntry> getAllWeatherEntries();
	WindEntry getWindEntry(int id);
	RainEntry getRainEntry(int id);
}

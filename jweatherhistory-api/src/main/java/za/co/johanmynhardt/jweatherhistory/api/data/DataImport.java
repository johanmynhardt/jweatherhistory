package za.co.johanmynhardt.jweatherhistory.api.data;

import java.util.List;

import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author Johan Mynhardt
 */
public interface DataImport {
	List<WeatherEntry> readEntries();
}

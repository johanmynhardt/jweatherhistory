package za.co.johanmynhardt.jweatherhistory.api.service;

import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author Johan Mynhardt
 */
public interface UpdateService {
	public WeatherEntry update(WeatherEntry weatherEntry);
	public WeatherEntry updateFromEdit(WeatherEntry weatherEntry);
}

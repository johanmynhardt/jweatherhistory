package za.co.johanmynhardt.jweatherhistory.model;

import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
public class WindEntry {
	public final long id;
	public final String description;
	public final WindDirection windDirection;
	public final int windspeed;
	public final WeatherEntry weatherEntry;

	public WindEntry(long id, String description, WindDirection windDirection, int windspeed, WeatherEntry weatherEntry) {
		this.id = id;
		this.description = description;
		this.windDirection = windDirection;
		this.windspeed = windspeed;
		this.weatherEntry = weatherEntry;
	}

	@Override
	public String toString() {
		return "WindEntry{" +
				"id=" + id +
				", description='" + description + '\'' +
				", windDirection=" + windDirection +
				", windspeed=" + windspeed +
				'}';
	}
}

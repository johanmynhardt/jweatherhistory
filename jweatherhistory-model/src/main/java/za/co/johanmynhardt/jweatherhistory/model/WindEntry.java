package za.co.johanmynhardt.jweatherhistory.model;

import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
public class WindEntry {
	private long id;
	private String description;
	private WindDirection windDirection;
	private int windspeed;
	private WeatherEntry weatherEntry;

	public WindEntry() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WindDirection getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(WindDirection windDirection) {
		this.windDirection = windDirection;
	}

	public int getWindspeed() {
		return windspeed;
	}

	public void setWindspeed(int windspeed) {
		this.windspeed = windspeed;
	}

	public WeatherEntry getWeatherEntry() {
		return weatherEntry;
	}

	public void setWeatherEntry(WeatherEntry weatherEntry) {
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

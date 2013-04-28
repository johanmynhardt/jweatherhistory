package za.co.johanmynhardt.jweatherhistory.model;

import java.util.Date;

/**
 * @author Johan Mynhardt
 */
public class WeatherEntry {
	public final long id;
	public final String description;
	public final Date entryDate;
	public final Date captureDate;
	public final int minimumTemperature;
	public final int maximumTemperature;
	public final WindEntry windEntry;
	public final RainEntry rainEntry;

	public WeatherEntry(long id, String description, Date entryDate, Date captureDate, int minimumTemperature, int maximumTemperature, WindEntry windEntry, RainEntry rainEntry) {
		this.id = id;
		this.description = description;
		this.entryDate = entryDate;
		this.captureDate = captureDate;
		this.minimumTemperature = minimumTemperature;
		this.maximumTemperature = maximumTemperature;
		this.windEntry = windEntry;
		this.rainEntry = rainEntry;
	}

	@Override
	public String toString() {
		return "WeatherEntry{" +
				"id=" + id +
				", description='" + description + '\'' +
				", entryDate=" + entryDate +
				", captureDate=" + captureDate +
				", minimumTemperature=" + minimumTemperature +
				", maximumTemperature=" + maximumTemperature +
				", windEntry=" + windEntry +
				", rainEntry=" + rainEntry +
				"} " + super.toString();
	}
}

package za.co.johanmynhardt.jweatherhistory.model;

import java.util.Date;

/**
 * @author Johan Mynhardt
 */
public class WeatherEntry {
	private long id;
	private String description;
	private Date entryDate;
	private Date captureDate;
	private int minimumTemperature;
	private int maximumTemperature;
	private WindEntry windEntry;
	private RainEntry rainEntry;

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

	public WeatherEntry() {
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

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Date getCaptureDate() {
		return captureDate;
	}

	public void setCaptureDate(Date captureDate) {
		this.captureDate = captureDate;
	}

	public int getMinimumTemperature() {
		return minimumTemperature;
	}

	public void setMinimumTemperature(int minimumTemperature) {
		this.minimumTemperature = minimumTemperature;
	}

	public int getMaximumTemperature() {
		return maximumTemperature;
	}

	public void setMaximumTemperature(int maximumTemperature) {
		this.maximumTemperature = maximumTemperature;
	}

	public WindEntry getWindEntry() {
		return windEntry;
	}

	public void setWindEntry(WindEntry windEntry) {
		this.windEntry = windEntry;
	}

	public RainEntry getRainEntry() {
		return rainEntry;
	}

	public void setRainEntry(RainEntry rainEntry) {
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

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
	private WindEntry windEntry;
	private RainEntry rainEntry;

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
				", date=" + captureDate +
				", windEntry=" + windEntry +
				", rainEntry=" + rainEntry +
				'}';
	}
}

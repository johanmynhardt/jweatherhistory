package za.co.johanmynhardt.jweatherhistory.model;

/**
 * @author Johan Mynhardt
 */
public class RainEntry {
	private int id;
	private int volume;
	private String description;
	private WeatherEntry weatherEntry;

	public RainEntry() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public WeatherEntry getWeatherEntry() {
		return weatherEntry;
	}

	public void setWeatherEntry(WeatherEntry weatherEntry) {
		this.weatherEntry = weatherEntry;
	}

	@Override
	public String toString() {
		return "RainEntry{" +
				"id=" + id +
				", volume=" + volume +
				", description='" + description + '\'' +
				'}';
	}
}

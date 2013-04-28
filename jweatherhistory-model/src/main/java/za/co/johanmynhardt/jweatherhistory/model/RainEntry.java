package za.co.johanmynhardt.jweatherhistory.model;

/**
 * @author Johan Mynhardt
 */
public class RainEntry {
	public final long id;
	public final int volume;
	public final String description;
	private WeatherEntry weatherEntry;

	public RainEntry(int id, int volume, String description, WeatherEntry weatherEntry) {
		this.id = id;
		this.volume = volume;
		this.description = description;
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

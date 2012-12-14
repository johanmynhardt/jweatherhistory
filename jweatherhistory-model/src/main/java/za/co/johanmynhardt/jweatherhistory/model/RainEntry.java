package za.co.johanmynhardt.jweatherhistory.model;

/**
 * @author Johan Mynhardt
 */
public class RainEntry {
	private int id;
	private int volume;

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

	@Override
	public String toString() {
		return "RainEntry{" +
				"volume=" + volume +
				"} " + super.toString();
	}
}

package za.co.johanmynhardt.jweatherhistory.model;

import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
public class WindEntry {
	private long id;
	private WindDirection windDirection;
	private int windspeed;

	public WindEntry() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
}

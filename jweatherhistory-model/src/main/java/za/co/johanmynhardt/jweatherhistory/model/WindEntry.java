package za.co.johanmynhardt.jweatherhistory.model;

import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
public record WindEntry(
    long id,
    String description,
    WindDirection windDirection,
    int windspeed,
    WeatherEntry weatherEntry
) {}

package za.co.johanmynhardt.jweatherhistory.model;

/**
 * @author Johan Mynhardt
 */
public record RainEntry(
    long id,
    int volume,
    String description,
    WeatherEntry weatherEntry
) {}
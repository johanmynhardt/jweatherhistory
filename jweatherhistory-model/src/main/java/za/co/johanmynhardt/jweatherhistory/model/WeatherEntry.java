package za.co.johanmynhardt.jweatherhistory.model;

import java.util.Date;

/**
 * @author Johan Mynhardt
 */
public record WeatherEntry(
    long id,
    String description,
    Date entryDate,
    Date captureDate,
    int minimumTemperature,
    int maximumTemperature,
    WindEntry windEntry,
    RainEntry rainEntry
) {}

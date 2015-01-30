package za.co.johanmynhardt.jweatherhistory.api.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author johan
 */
public interface IDataImport {
    public List<WeatherEntry> importWeatherEntries(InputStream inputStream) throws IOException;

    public String getFormat();
}

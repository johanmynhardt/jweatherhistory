package za.co.johanmynhardt.jweatherhistory.impl.data;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import za.co.johanmynhardt.jweatherhistory.api.data.IDataSerializer;
import za.co.johanmynhardt.jweatherhistory.impl.service.WeatherHistoryService;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author johan
 */
@Service
public class JsonDataSerializer implements IDataSerializer{

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JsonDataSerializer.class);
    @Inject
    private WeatherHistoryService service;

    @Override
    public void exportWeatherEntries(OutputStream outputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().writeValue(outputStream, service.getAllWeatherEntries());
    }

    @Override
    public String getFormat() {
        return "json";
    }

    @Override
    public String getExtension() {
        return ".".concat(getFormat());
    }

    @Override
    public List<WeatherEntry> importWeatherEntries(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final List<WeatherEntry> entries = Arrays.asList(mapper.reader(WeatherEntry[].class).readValue(inputStream));
        LOG.debug("Imported weatherEntries={}", entries);

        for (WeatherEntry entry : entries) {
            service.createWeatherEntry(entry);
        }

        return entries;
    }
}

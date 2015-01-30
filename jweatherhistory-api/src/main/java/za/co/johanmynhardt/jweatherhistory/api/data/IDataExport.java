package za.co.johanmynhardt.jweatherhistory.api.data;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author johan
 */
public interface IDataExport {

    public void exportWeatherEntries(OutputStream outputStream) throws IOException;

    public String getFormat();

    public String getExtension();
}

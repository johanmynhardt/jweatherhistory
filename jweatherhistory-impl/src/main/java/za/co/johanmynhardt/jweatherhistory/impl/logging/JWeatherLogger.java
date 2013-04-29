package za.co.johanmynhardt.jweatherhistory.impl.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * @author Johan Mynhardt
 */
public class JWeatherLogger extends SimpleFormatter{
	@Override
	public synchronized String format(LogRecord record) {
		return String.format("[%s] %s\n", record.getLevel(), record.getMessage());
	}
}

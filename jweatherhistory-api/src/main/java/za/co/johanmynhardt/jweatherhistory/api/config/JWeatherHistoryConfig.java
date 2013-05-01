package za.co.johanmynhardt.jweatherhistory.api.config;

import java.nio.file.Path;
import java.sql.SQLException;

/**
 * @author Johan Mynhardt
 */
public interface JWeatherHistoryConfig {
	public static final String SEPARATOR = System.getProperty("file.separator");
	public static final String CONFIG_DIR = String.format(".config%sjweatherhistory", SEPARATOR);
	public static final String DB_NAME = "jweatherhistory";
	public Path getConfigPath();
	public Path getDbPath();
	public Path getDbDir();
	public String getConnectionUrl();
	public void bootstrapLog();
	public void bootstrapSQL() throws SQLException;
}

package za.co.johanmynhardt.jweatherhistory.impl.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import za.co.johanmynhardt.jweatherhistory.api.config.JWeatherHistoryConfig;

import static java.lang.String.format;

/**
 * Configuration bootstrapping that will create a configuration directory and run
 * SQL statements if it's the first run.
 *
 * @author Johan Mynhardt
 */
public class JWeatherHistoryConfigImpl implements JWeatherHistoryConfig {
	Logger logger = Logger.getLogger(JWeatherHistoryConfigImpl.class.getName());

	@Override
	public Path getDbPath() {
		return Paths.get(format("%s%s%s%s%sdb%s%s", System.getProperty("user.home"), SEPARATOR, DB_DIR, SEPARATOR, SEPARATOR, SEPARATOR, DB_NAME));
	}

	@Override
	public Path getDbDir() {
		return Paths.get(format("%s%s%s%s%sdb", System.getProperty("user.home"), SEPARATOR, DB_DIR, SEPARATOR, SEPARATOR));
	}

	@Override
	public String getConnectionUrl() {
		return format("jdbc:derby:%s;create=true", getDbPath());
	}

	@Override
	public void bootstrap() {
		if (!getDbDir().toFile().exists()) {
			logger.info("Creating DB directory: %s" + getDbDir());
			if (!getDbDir().toFile().mkdirs()) throw new RuntimeException("Could not create directory " + getDbDir());
			logger.info("Created directory " + getDbDir());
		}

		if (!getDbPath().toFile().exists()) {
			logger.info("Bootstrapping Derby DB...");
			try {
				runSQL();
			} catch (SQLException | IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("Found Derby DB!");
		}
	}

	private boolean runSQL() throws SQLException, URISyntaxException, IOException {
		Connection connection = DriverManager.getConnection(getConnectionUrl());
		logger.warning("Got connection: " + connection);

		Statement statement = connection.createStatement();

		StringBuilder stringBuilder = new StringBuilder();
		for (String line : Files.readAllLines(
				Paths.get(JWeatherHistoryConfigImpl.class.getResource("/sql/weatherhistory.sql").toURI()),
				Charset.forName("UTF-8"))) {
			stringBuilder.append(line).append("\n");
		}

		for (String statementSting : stringBuilder.toString().split(";")) {
			if (statementSting.trim().isEmpty()) {
				logger.warning("Empty statement. Not executing.");
			} else {
				logger.info("Executing statement: " + statementSting);
				statement.execute(statementSting);
			}
		}
		logger.warning("Derby initialising complete!");
		connection.close();
		return true;
	}
}

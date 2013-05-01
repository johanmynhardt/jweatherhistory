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
	private final String workingDirectory;
	Logger logger = Logger.getLogger(JWeatherHistoryConfigImpl.class.getName());

	public JWeatherHistoryConfigImpl(boolean testing) {
		workingDirectory = workingDirectory(testing);
	}

	public JWeatherHistoryConfigImpl() {
		this(false);
	}

	@Override
	public Path getConfigPath() {
		return Paths.get(format("%s%s%s", workingDirectory, SEPARATOR, CONFIG_DIR));
	}

	@Override
	public Path getDbDir() {
		return Paths.get(format("%s%sdb", getConfigPath(), SEPARATOR));
	}

	@Override
	public Path getDbPath() {
		return Paths.get(format("%s%sdb%s%s", getConfigPath(), SEPARATOR, SEPARATOR, DB_NAME));
	}

	@Override
	public String getConnectionUrl() {
		return format("jdbc:derby:%s;create=true", getDbPath());
	}

	@Override
	public void bootstrap() throws SQLException {
		if (!getDbDir().toFile().exists()) {
			logger.info(format("Creating DB directory: %s", getDbDir()));
			if (!getDbDir().toFile().mkdirs()) throw new RuntimeException("Could not create directory " + getDbDir());
			logger.info("Directory created successfully: " + getDbDir());
		}

		if (!getDbPath().toFile().exists()) {
			logger.info("Bootstrapping Derby DB...");
			try {
				runSQL();
			} catch (IOException | URISyntaxException e) {
				logger.severe(format("Could not bootstrap the Derby DB! (%s: %s)", e.getClass().getSimpleName(), e.getMessage()));
				e.printStackTrace();
			}
		} else {
			logger.info("Found Derby DB at " + getDbPath());
		}
	}

	private boolean runSQL() throws SQLException, URISyntaxException, IOException {
		Connection connection = DriverManager.getConnection(getConnectionUrl());
		logger.info("Got connection: " + connection);

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

	private String workingDirectory(boolean testing) {
		String directory = System.getProperty("user.home");
		if (testing) {
			try {
				directory = Files.createTempDirectory("jweather").toString();
			} catch (IOException e) {
				directory = "/tmp";
			}
		}
		return directory;
	}
}

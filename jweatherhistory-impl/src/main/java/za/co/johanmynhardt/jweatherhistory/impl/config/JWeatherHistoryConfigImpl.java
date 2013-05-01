package za.co.johanmynhardt.jweatherhistory.impl.config;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.LogManager;
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
	public void bootstrapSQL() throws SQLException {
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

	@Override
	public void bootstrapLog() {
		System.out.println("Setting up logger...");
		try {
			if (Files.notExists(getConfigPath()) && Files.notExists(Files.createDirectory(getConfigPath())))
				throw new IOException("Could not set up config path.");
			LogManager.getLogManager().readConfiguration(JWeatherHistoryConfig.class.getResourceAsStream("/logging.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean runSQL() throws SQLException, URISyntaxException, IOException {
		Connection connection = DriverManager.getConnection(getConnectionUrl());
		logger.info("Got connection: " + connection);

		Statement statement = connection.createStatement();

		StringBuilder stringBuilder = new StringBuilder();
		logger.info("Loading /sql/weatherhistory.sql");
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
						JWeatherHistoryConfig.class.getResourceAsStream("/sql/weatherhistory.sql")
				)
		);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
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

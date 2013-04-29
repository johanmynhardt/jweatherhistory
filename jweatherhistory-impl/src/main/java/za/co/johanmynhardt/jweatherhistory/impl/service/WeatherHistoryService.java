package za.co.johanmynhardt.jweatherhistory.impl.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import za.co.johanmynhardt.jweatherhistory.api.config.JWeatherHistoryConfig;
import za.co.johanmynhardt.jweatherhistory.api.service.CaptureService;
import za.co.johanmynhardt.jweatherhistory.api.service.ReaderService;
import za.co.johanmynhardt.jweatherhistory.api.service.UpdateService;
import za.co.johanmynhardt.jweatherhistory.impl.config.JWeatherHistoryConfigImpl;
import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

import static java.lang.String.format;

/**
 * @author Johan Mynhardt
 */
public class WeatherHistoryService implements CaptureService, ReaderService, UpdateService {
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final Logger logger = Logger.getLogger(WeatherHistoryService.class.getName());
	private static SimpleDateFormat simpleDateFormat;
	private static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Connection connection;

	static {
		try {
			JWeatherHistoryConfig config = new JWeatherHistoryConfigImpl();
			config.bootstrap();
			connection = DriverManager.getConnection(config.getConnectionUrl());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.err.println("Closing connection " + connection);
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	@Override
	public WeatherEntry createWeatherEntry(String description, Date entryDate, int minimumTemperature, int maximumTemperature, RainEntry rainEntry, WindEntry windEntry) {
		WeatherEntry weatherEntry = new WeatherEntry(
				0,
				description,
				entryDate,
				new Date(),
				minimumTemperature,
				maximumTemperature,
				windEntry,
				rainEntry
		);

		return createWeatherEntry(weatherEntry);
	}

	@Override
	public WeatherEntry createWeatherEntry(WeatherEntry weatherEntry) {
		logger.info("Creating weatherEntry");
		logger.info(weatherEntry.toString());

		try {
			connection.setAutoCommit(false);
			PreparedStatement preparedStatement = connection.prepareStatement(
					"INSERT INTO WEATHERENTRY (DESCRIPTION, MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE, ENTRY_DATE, CAPTURE_DATE) values (?, ?, ?, ? ,?)",
					Statement.RETURN_GENERATED_KEYS
			);

			preparedStatement.setString(1, weatherEntry.description);
			preparedStatement.setInt(2, weatherEntry.minimumTemperature);
			preparedStatement.setInt(3, weatherEntry.maximumTemperature);
			preparedStatement.setDate(4, new java.sql.Date(weatherEntry.entryDate.getTime()));
			preparedStatement.setDate(5, new java.sql.Date(weatherEntry.captureDate.getTime()));
			preparedStatement.execute();

			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			int generatedId = resultSet.next() ? resultSet.getInt(1) : -1;
			logger.info("GeneratedId: " + generatedId);

			weatherEntry = new WeatherEntry(
					generatedId,
					weatherEntry.description,
					weatherEntry.entryDate,
					weatherEntry.captureDate,
					weatherEntry.minimumTemperature,
					weatherEntry.maximumTemperature,
					weatherEntry.windEntry,
					weatherEntry.rainEntry
			);

			WindEntry createdWindEntry = null;
			RainEntry createdRainEntry = null;

			boolean updated = false;
			if (weatherEntry.rainEntry != null) {
				createdRainEntry = createRainEntry(weatherEntry.rainEntry.description, weatherEntry.rainEntry.volume, weatherEntry);
				updated = true;
			}
			if (weatherEntry.windEntry != null) {
				createdWindEntry = createWindEntry(weatherEntry.windEntry.description, weatherEntry.windEntry.windDirection, weatherEntry.windEntry.windspeed, weatherEntry);
				updated = true;
			}

			if (updated) {
				weatherEntry = new WeatherEntry(
						weatherEntry.id,
						weatherEntry.description,
						weatherEntry.entryDate,
						weatherEntry.captureDate,
						weatherEntry.minimumTemperature,
						weatherEntry.maximumTemperature,
						createdWindEntry,
						createdRainEntry
				);
				update(weatherEntry);
			}
			connection.commit();
			return weatherEntry;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public RainEntry createRainEntry(String description, Integer volume, WeatherEntry weatherEntry) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(
					"INSERT INTO RAINENTRY (DESCRIPTION, VOLUME, WEATHERENTRY_ID) values (?,?,?)",
					Statement.RETURN_GENERATED_KEYS
			);

			preparedStatement.setString(1, description);
			preparedStatement.setInt(2, volume);
			preparedStatement.setLong(3, weatherEntry.id);

			preparedStatement.executeUpdate();

			ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
			RainEntry rainEntry = new RainEntry(generatedKeys.next() ? generatedKeys.getInt(1) : -1, volume, description, weatherEntry);
			logger.info("RainEntry created: " + rainEntry);
			return rainEntry;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public WindEntry createWindEntry(String description, WindDirection direction, Integer windSpeed, WeatherEntry weatherEntry) throws SQLException {
		connection.setAutoCommit(false);
		PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO WINDENTRY (DESCRIPTION, WINDDIRECTION, WINDSPEED, WEATHERENTRY_ID) values (?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS
		);

		statement.setString(1, description);
		statement.setString(2, direction.name());
		statement.setInt(3, windSpeed);
		statement.setLong(4, weatherEntry.id);
		statement.executeUpdate();

		ResultSet resultSet = statement.getGeneratedKeys();

		if (resultSet.next()) {
			int id = resultSet.getInt(1);

			WindEntry windEntry = new WindEntry(id, description, direction, windSpeed, weatherEntry);
			connection.commit();
			connection.setAutoCommit(true);
			return windEntry;
		} else {
			connection.rollback();
			connection.setAutoCommit(true);
			throw new SQLException("Unexpected condition: no key generated for new WindEntry.");
		}
	}

	@Override
	public List<WeatherEntry> getAllWeatherEntries() {
		try {
			Statement statement = connection.createStatement();
			String QUERY = format("SELECT * FROM WEATHERENTRY");
			ResultSet resultSet = statement.executeQuery(QUERY);

			List<WeatherEntry> results = new ArrayList<>();
			while (resultSet.next()) {
				//TODO fetch rain entry and wind entry
				//TODO fetch temperature
				WeatherEntry weatherEntry = new WeatherEntry(resultSet.getLong(1), resultSet.getString(2), resultSet.getDate(5), resultSet.getTimestamp(6), resultSet.getInt(3), resultSet.getInt(4), null, null);

				results.add(weatherEntry);
			}
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public WeatherEntry update(WeatherEntry weatherEntry) {
		try {
			connection.setAutoCommit(false);
			String updateStatement = "UPDATE WEATHERENTRY set DESCRIPTION = ?, WINDENTRY_ID = ?, RAINENTRY_ID = ? where ID = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(updateStatement);

			preparedStatement.setString(1, weatherEntry.description);
			preparedStatement.setLong(2, weatherEntry.windEntry.id);
			preparedStatement.setLong(3, weatherEntry.rainEntry.id);
			preparedStatement.setLong(4, weatherEntry.id);

			preparedStatement.executeUpdate();
			connection.commit();
			return weatherEntry;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return weatherEntry;
	}
}

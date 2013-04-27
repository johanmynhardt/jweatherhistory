package za.co.johanmynhardt.jweatherhistory.impl.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.johanmynhardt.jweatherhistory.api.service.CaptureService;
import za.co.johanmynhardt.jweatherhistory.api.service.ReaderService;
import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

import static java.lang.String.format;

/**
 * @author Johan Mynhardt
 */
public class WeatherHistoryService implements CaptureService, ReaderService {
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	private static SimpleDateFormat simpleDateFormat;
	private static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static Connection connection;

	static {
		try {
			connection = DriverManager.getConnection("jdbc:derby:/tmp/weatherhistory");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}));
	}

	@Override
	public WeatherEntry createWeatherEntry(String description, Date entryDate, RainEntry rainEntry, WindEntry windEntry) {
		WeatherEntry weatherEntry = new WeatherEntry();
		weatherEntry.setCaptureDate(new Date());
		weatherEntry.setEntryDate(entryDate);
		weatherEntry.setDescription(description);


		try {
			String entryDateFormat = simpleDateFormat.format(weatherEntry.getEntryDate());
			String captureDateFormat = timestampFormat.format(weatherEntry.getCaptureDate());
			Statement statement = connection.createStatement();
			String SQL = format("INSERT INTO WEATHERENTRY (DESCRIPTION, ENTRY_DATE, CAPTURE_DATE) values ('%s', '%s', '%s')",
					description, simpleDateFormat.format(weatherEntry.getEntryDate()), timestampFormat.format(weatherEntry.getCaptureDate()));
			System.out.println("SQL = " + SQL);
			statement.execute(SQL, Statement.RETURN_GENERATED_KEYS);
			ResultSet resultSet = statement.getGeneratedKeys();

			if (resultSet.next()) {
				weatherEntry.setId(resultSet.getInt(1));
			}

			boolean updated = false;
			if (rainEntry != null) {
				weatherEntry.setRainEntry(createRainEntry(rainEntry.getDescription(), rainEntry.getVolume(), weatherEntry));
				updated = true;
			}
			if (windEntry != null) {
				weatherEntry.setWindEntry(createWindEntry(windEntry.getDescription(), windEntry.getWindDirection(), windEntry.getWindspeed(), weatherEntry));
				updated = true;
			}

			if (updated) {

			}

			return weatherEntry;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public RainEntry createRainEntry(String description, Integer volume, WeatherEntry weatherEntry) {
		try {
			Statement statement = connection.createStatement();
			statement.execute("INSERT INTO RAINENTRY (DESCRIPTION, VOLUME, WEATHERENTRY_ID) values ('" + description + "', " + volume + ", "+ weatherEntry.getId()+")", Statement.RETURN_GENERATED_KEYS);
			ResultSet resultSet = statement.getGeneratedKeys();

			if (resultSet.next()) {
				int id = resultSet.getInt(1);

				RainEntry rainEntry = new RainEntry();
				rainEntry.setId(id);
				rainEntry.setDescription(description);
				rainEntry.setVolume(volume);
				rainEntry.setWeatherEntry(weatherEntry);

				return rainEntry;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public WindEntry createWindEntry(String description, WindDirection direction, Integer windSpeed, WeatherEntry weatherEntry) {
		try {
			Statement statement = connection.createStatement();
			String QUERY = format("INSERT INTO WINDENTRY (DESCRIPTION, WINDDIRECTION, WINDSPEED, WEATHERENTRY_ID) values ('%s', '%s', %s, %s)",
					description, direction.name(), windSpeed, weatherEntry.getId());
			statement.execute(QUERY, Statement.RETURN_GENERATED_KEYS);
			ResultSet resultSet = statement.getGeneratedKeys();

			if (resultSet.next()) {
				int id = resultSet.getInt(1);

				WindEntry windEntry = new WindEntry();
				windEntry.setId(id);
				windEntry.setDescription(description);
				windEntry.setWindDirection(direction);
				windEntry.setWindspeed(windSpeed);
				windEntry.setWeatherEntry(weatherEntry);

				return windEntry;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<WeatherEntry> getAllWeatherEntries() {
		try {
			Statement statement = connection.createStatement();
			String QUERY = format("SELECT * FROM WEATHERENTRY");
			ResultSet resultSet = statement.executeQuery(QUERY);

			List<WeatherEntry> results = new ArrayList<>();
			ResultSetMetaData metadata = resultSet.getMetaData();

			System.out.println("Columns:");
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				System.out.print(metadata.getColumnName(i) + " | ");
			}
			System.out.println();
			while (resultSet.next()) {
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					System.out.println(resultSet.getObject(i));
				}
			}

			return results;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}

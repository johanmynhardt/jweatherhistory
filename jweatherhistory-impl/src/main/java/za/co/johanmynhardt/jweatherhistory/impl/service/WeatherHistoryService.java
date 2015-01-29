package za.co.johanmynhardt.jweatherhistory.impl.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import za.co.johanmynhardt.jweatherhistory.api.service.CaptureService;
import za.co.johanmynhardt.jweatherhistory.api.service.ReaderService;
import za.co.johanmynhardt.jweatherhistory.api.service.UpdateService;
import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
@Service
public class WeatherHistoryService implements CaptureService, ReaderService, UpdateService {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final Logger LOG = LoggerFactory.getLogger(WeatherHistoryService.class);
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Inject
    private TransactionTemplate transactionTemplate;

    public WeatherHistoryService() {
    }

    @PostConstruct
    public void init() {
        LOG.info("WeatherHistoryService.init()");
        LOG.debug("jdbcTemplate={}", jdbcTemplate);
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
    public WeatherEntry createWeatherEntry(final WeatherEntry weatherEntry) {
        LOG.info("Creating weatherEntry");
        LOG.debug("weatherEntry={}", weatherEntry.toString());

        final WeatherEntry execute = transactionTemplate.execute(status -> {

            KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement preparedStatement = con.prepareStatement(
                        "INSERT INTO WEATHERENTRY (DESCRIPTION, MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE, ENTRY_DATE, CAPTURE_DATE) VALUES (?, ?, ?, ? ,?)",
                        Statement.RETURN_GENERATED_KEYS
                );

                preparedStatement.setString(1, weatherEntry.getDescription());
                preparedStatement.setInt(2, weatherEntry.getMinimumTemperature());
                preparedStatement.setInt(3, weatherEntry.getMaximumTemperature());
                preparedStatement.setDate(4, new java.sql.Date(weatherEntry.getEntryDate().getTime()));
                preparedStatement.setDate(5, new java.sql.Date(weatherEntry.getCaptureDate().getTime()));

                return preparedStatement;
            }, generatedKeyHolder);

            LOG.debug("GeneratedKey for weatherEntry={}", generatedKeyHolder);

            final WeatherEntry weatherEntry1 = new WeatherEntry(
                    generatedKeyHolder.getKey().longValue(),
                    weatherEntry.getDescription(),
                    weatherEntry.getEntryDate(),
                    weatherEntry.getCaptureDate(),
                    weatherEntry.getMinimumTemperature(),
                    weatherEntry.getMaximumTemperature(),
                    weatherEntry.getWindEntry(),
                    weatherEntry.getRainEntry()
            );

            WindEntry createdWindEntry = null;
            RainEntry createdRainEntry = null;

            boolean updated = false;
            if (weatherEntry1.getRainEntry() != null) {
                createdRainEntry = createRainEntry(weatherEntry1.getRainEntry().description, weatherEntry1.getRainEntry().volume, weatherEntry1);
                updated = true;
            }
            if (weatherEntry1.getWindEntry() != null) {
                createdWindEntry = createWindEntry(weatherEntry1.getWindEntry().description, weatherEntry1.getWindEntry().windDirection, weatherEntry1.getWindEntry().windspeed, weatherEntry1);
                updated = true;
            }

            WeatherEntry weatherEntry2 = null;

            if (updated) {
                weatherEntry2 = new WeatherEntry(
                        weatherEntry1.getId(),
                        weatherEntry1.getDescription(),
                        weatherEntry1.getEntryDate(),
                        weatherEntry1.getCaptureDate(),
                        weatherEntry1.getMinimumTemperature(),
                        weatherEntry1.getMaximumTemperature(),
                        createdWindEntry,
                        createdRainEntry
                );
                update(weatherEntry2);
                return weatherEntry2;
            }

            return weatherEntry1;
        });

        LOG.debug("created weatherEntry={}", execute);

        return execute;
    }

    @Override
    public RainEntry createRainEntry(final String description, final Integer volume, final WeatherEntry weatherEntry) {
        LOG.debug("Creating RainEntry description={}, volume={}, weatherEntry={}", description, volume, weatherEntry);

        final GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {

            final PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO RAINENTRY (DESCRIPTION, VOLUME, WEATHERENTRY_ID) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, description);
            preparedStatement.setInt(2, volume);
            preparedStatement.setLong(3, weatherEntry.getId());

            LOG.debug("Returning preparedStatement={}", preparedStatement);

            return preparedStatement;
        }, generatedKeyHolder);

        final Number key = generatedKeyHolder.getKey();
        final RainEntry rainEntry = new RainEntry(key.longValue(), volume, description, weatherEntry);

        LOG.debug("created rainEntry={}", rainEntry);

        return rainEntry;
    }

    @Override
    public WindEntry createWindEntry(final String description, final WindDirection direction, final Integer windSpeed, final WeatherEntry weatherEntry) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO WINDENTRY (DESCRIPTION, WINDDIRECTION, WINDSPEED, WEATHERENTRY_ID) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, description);
            statement.setString(2, direction.name());
            statement.setInt(3, windSpeed);
            statement.setLong(4, weatherEntry.getId());

            return statement;
        }, keyHolder);

        WindEntry windEntry = new WindEntry(keyHolder.getKey().longValue(), description, direction, windSpeed, weatherEntry);

        return windEntry;
    }

    @Override
    public List<WeatherEntry> getAllWeatherEntries() {

        List<WeatherEntry> results = jdbcTemplate.query("SELECT * FROM WEATHERENTRY", (rs, rowNum) -> new WeatherEntry(rs.getLong(1),
                rs.getString("DESCRIPTION"),
                rs.getDate("ENTRY_DATE"),
                rs.getDate("CAPTURE_DATE"),
                rs.getInt("MINIMUM_TEMPERATURE"),
                rs.getInt("MAXIMUM_TEMPERATURE"),
                getWindEntry(rs.getInt("WINDENTRY_ID")),
                getRainEntry(rs.getInt("RAINENTRY_ID"))
        ));
        return results;
    }

    private void printResultSetMetaData(ResultSetMetaData resultSetMetaData) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                stringBuilder.append(i).append(". ").append(resultSetMetaData.getColumnName(i)).append(" | ");
            }
            LOG.trace("Columns: {}", stringBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WindEntry getWindEntry(int id) {

        LOG.debug("retrieving windEntry with id={}", id);

        WindEntry windEntry = jdbcTemplate.queryForObject("SELECT * FROM WINDENTRY WHERE ID = ?", (rs, rowNum) -> new WindEntry(
                rs.getInt("ID"),
                rs.getString("DESCRIPTION"),
                WindDirection.valueOf(rs.getString("WINDDIRECTION")),
                rs.getInt("WINDSPEED"),
                null
        ), id);

        return windEntry;
    }

    @Override
    public RainEntry getRainEntry(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM RAINENTRY WHERE ID = ?", (rs, rowNum) -> new RainEntry(
                rs.getInt("ID"),
                rs.getInt("VOLUME"),
                rs.getString("DESCRIPTION"),
                null
        ), id);
    }

    @Override
    public WeatherEntry update(final WeatherEntry weatherEntry) {

        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE WEATHERENTRY SET DESCRIPTION = ?, WINDENTRY_ID = ?, RAINENTRY_ID = ? WHERE ID = ?");

            preparedStatement.setString(1, weatherEntry.getDescription());
            preparedStatement.setLong(2, weatherEntry.getWindEntry().id);
            preparedStatement.setLong(3, weatherEntry.getRainEntry().id);
            preparedStatement.setLong(4, weatherEntry.getId());
            return preparedStatement;
        });

        return weatherEntry;
    }

    @Override
    public WeatherEntry updateFromEdit(final WeatherEntry weatherEntry) {

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(
                            "UPDATE WEATHERENTRY SET DESCRIPTION = ?, MINIMUM_TEMPERATURE = ?, MAXIMUM_TEMPERATURE = ?, " +
                                    "ENTRY_DATE = ?" +
                                    "WHERE ID = ?"
                    );

                    preparedStatement.setString(1, weatherEntry.getDescription());
                    preparedStatement.setInt(2, weatherEntry.getMinimumTemperature());
                    preparedStatement.setInt(3, weatherEntry.getMaximumTemperature());
                    preparedStatement.setDate(4, new java.sql.Date(weatherEntry.getEntryDate().getTime()));
                    preparedStatement.setLong(5, weatherEntry.getId());
                    return preparedStatement;
                });

                jdbcTemplate.update(con -> {
                    final PreparedStatement preparedStatement = con.prepareStatement(
                            "UPDATE WINDENTRY SET DESCRIPTION = ?, WINDDIRECTION = ?, WINDSPEED = ? WHERE ID = ?"
                    );

                    preparedStatement.setString(1, weatherEntry.getWindEntry().description);
                    preparedStatement.setString(2, weatherEntry.getWindEntry().windDirection.name());
                    preparedStatement.setInt(3, weatherEntry.getWindEntry().windspeed);
                    preparedStatement.setLong(4, weatherEntry.getWindEntry().id);
                    return preparedStatement;
                });

                jdbcTemplate.update(con -> {
                    final PreparedStatement preparedStatement = con.prepareStatement(
                            "UPDATE RAINENTRY SET DESCRIPTION = ?, VOLUME = ? WHERE ID = ?"
                    );
                    preparedStatement.setString(1, weatherEntry.getRainEntry().description);
                    preparedStatement.setInt(2, weatherEntry.getRainEntry().volume);
                    preparedStatement.setLong(3, weatherEntry.getRainEntry().id);
                    return preparedStatement;
                });
            }
        });

        return weatherEntry;
    }
}

package za.co.johanmynhardt.jweatherhistory.impl.config;

import org.springframework.stereotype.Component;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import za.co.johanmynhardt.jweatherhistory.api.config.JWeatherHistoryConfig;

/**
 * @author johan
 */
@Component
public class DbBootstrapper {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DbBootstrapper.class);

    public void testDataSource(DataSource dataSource, boolean retest) throws SQLException {
        try {
            dataSource.getConnection().createStatement().executeQuery("SELECT count(*) FROM WEATHERENTRY").getMetaData();
        } catch (SQLException e) {
            if (e.getMessage().contains("does not exist") && retest) {
                // bootstrap
                try {
                    bootstrapTables(dataSource);
                    testDataSource(dataSource, false);
                } catch (URISyntaxException | IOException e1) {
                    LOG.error("Error", e1);
                }
            } else {
                throw new RuntimeException("Unexpected database error", e);
            }
        }
    }

    private void bootstrapTables(DataSource dataSource) throws SQLException, URISyntaxException, IOException {
        Connection connection = dataSource.getConnection();
        LOG.info("Got connection: " + connection);

        Statement statement = connection.createStatement();

        StringBuilder stringBuilder = new StringBuilder();
        LOG.info("Loading /sql/weatherhistory.sql");
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
                LOG.warn("Empty statement. Not executing.");
            } else {
                LOG.info("Executing statement: " + statementSting);
                statement.execute(statementSting);
            }
        }
        LOG.warn("Derby initialising complete!");
        connection.close();
    }
}

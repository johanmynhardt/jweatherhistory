package za.co.johanmynhardt.jweatherhistory.model;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import za.co.johanmynhardt.jweatherhistory.impl.config.JWeatherHistoryConfigImpl;

/**
 * Unit test for simple App.
 */
public class ModelTest {
	Logger logger = LoggerFactory.getLogger(ModelTest.class);

	@Test
	public void testModels() throws SQLException {


		Connection connection = DriverManager.getConnection(new JWeatherHistoryConfigImpl().getConnectionUrl());
		logger.info("Got Connection: " + connection);
		connection.close();
	}
}

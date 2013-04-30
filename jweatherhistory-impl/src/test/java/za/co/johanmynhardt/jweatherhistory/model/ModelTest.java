package za.co.johanmynhardt.jweatherhistory.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Logger;

import org.junit.Test;
import za.co.johanmynhardt.jweatherhistory.impl.config.JWeatherHistoryConfigImpl;

/**
 * Unit test for simple App.
 */
public class ModelTest {
	Logger logger = Logger.getLogger(ModelTest.class.getName());

	@Test
	public void testModels() throws SQLException {


		Connection connection = DriverManager.getConnection(new JWeatherHistoryConfigImpl().getConnectionUrl());
		logger.info("Got Connection: " + connection);
		connection.close();
	}
}

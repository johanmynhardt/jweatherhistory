package za.co.johanmynhardt.jweatherhistory.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Logger;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class ModelTest {
	Logger logger = Logger.getLogger(ModelTest.class.getName());

	@Test
	public void testModels() throws SQLException {


		Connection connection = DriverManager.getConnection("jdbc:derby:/tmp/weatherhistory;create=true");

		Statement statement = connection.createStatement();
		ResultSet resultset = statement.executeQuery("SELECT * fROM WINDENTRY");

		while (resultset.next()) {
			logger.info(resultset.getString(0));
		}

		connection.close();


		WeatherEntry weatherEntry = new WeatherEntry(-1, "", new Date(), new Date(), 0, 0, null, null);

		logger.info("weatherEntry = " + weatherEntry);

		RainEntry rainEntry = new RainEntry(-1, 0, "", weatherEntry);

		logger.info("rainEntry = " + rainEntry);
	}
}

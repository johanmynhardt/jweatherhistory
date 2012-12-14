package za.co.johanmynhardt.jweatherhistory.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class ModelTest {

	@Test
	public void testModels() throws SQLException {

		Connection connection = DriverManager.getConnection("jdbc:derby:/tmp/weatherhistory;create=true");

		Statement statement = connection.createStatement();
		ResultSet resultset = statement.executeQuery("SELECT * fROM WINDENTRY");

		while (resultset.next()) {
			System.out.println(resultset.getString(0));
		}

		connection.close();


		WeatherEntry weatherEntry = new WeatherEntry();

		System.out.println("weatherEntry = " + weatherEntry);

		RainEntry rainEntry = new RainEntry();

		System.out.println("rainEntry = " + rainEntry);
	}
}

package za.co.johanmynhardt.jweatherhistory.impl.config;

import java.nio.file.Path;

import org.junit.Test;
import za.co.johanmynhardt.jweatherhistory.api.config.JWeatherHistoryConfig;

/**
 * @author Johan Mynhardt
 */
public class JWeatherHistoryConfigImplTest {
	@Test
	public void testGetDbPath() throws Exception {
		JWeatherHistoryConfig jWeatherHistoryConfig = new JWeatherHistoryConfigImpl();

		Path result = jWeatherHistoryConfig.getDbPath();

		System.out.println("jWeatherHistoryConfig.getDbDir() = " + jWeatherHistoryConfig.getDbDir());
		System.out.println("jWeatherHistoryConfig.getDbPath() = " + jWeatherHistoryConfig.getDbPath());

		jWeatherHistoryConfig.bootstrap();


	}
}

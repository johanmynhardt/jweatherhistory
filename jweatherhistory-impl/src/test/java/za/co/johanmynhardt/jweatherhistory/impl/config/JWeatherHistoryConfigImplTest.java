package za.co.johanmynhardt.jweatherhistory.impl.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import za.co.johanmynhardt.jweatherhistory.api.config.JWeatherHistoryConfig;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * @author Johan Mynhardt
 */
public class JWeatherHistoryConfigImplTest {
	JWeatherHistoryConfig config = new JWeatherHistoryConfigImpl(true);

	@Test
	public void testPaths() throws Exception {
		Path configPath = config.getConfigPath();
		Path dbDir = config.getDbDir();
		Path dbPath = config.getDbPath();

		System.out.println("configPath = " + configPath);
		System.out.println("dbDir = " + dbDir);
		System.out.println("dbPath = " + dbPath);

		assertFalse(configPath.toString().contains("%s"));
		assertFalse(dbDir.toString().contains("%s"));
		assertFalse(dbPath.toString().contains("%s"));
	}

	@Test
	public void testBootstrap() {
		Path toDelete = config.getConfigPath();
		if (Files.exists(toDelete)) {
			try {
				int i = Runtime.getRuntime().exec(new String[]{"rm","-rf", toDelete.toString()}).waitFor();
				System.out.println("exit status = " + i);
				boolean deleted = !toDelete.toFile().exists();
				if (!deleted) {
					fail(String.format("%s found, but could not be deleted before testing bootstrap.", toDelete));
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			config.bootstrap();
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}

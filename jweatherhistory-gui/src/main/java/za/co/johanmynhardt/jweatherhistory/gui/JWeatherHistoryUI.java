package za.co.johanmynhardt.jweatherhistory.gui;

import java.io.IOException;
import java.util.Date;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.*;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;
import za.co.johanmynhardt.jweatherhistory.api.config.JWeatherHistoryConfig;
import za.co.johanmynhardt.jweatherhistory.impl.config.JWeatherHistoryConfigImpl;

public class JWeatherHistoryUI {
	private static Logger logger = Logger.getLogger(JWeatherHistoryUI.class.getName());
	public static void main(String[] args) {
		JWeatherHistoryConfig config = new JWeatherHistoryConfigImpl();
		config.bootstrapLog();


		try {
			PlasticLookAndFeel plasticLookAndFeel = new PlasticLookAndFeel();
			PlasticLookAndFeel.setCurrentTheme(new LightGray());
			UIManager.setLookAndFeel(plasticLookAndFeel);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}


		logger.info("----------------------------------------");
		logger.info("Starting at " + new Date());
		logger.finest("Launching JWeatherHistoryUI...");
		new MainFrame();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				logger.info("----------------------------------------");
				logger.info("Shutting down at " + new Date());
			}
		}));
	}
}

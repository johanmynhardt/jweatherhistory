package za.co.johanmynhardt.jweatherhistory.gui;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.util.Date;

@Configuration
@ComponentScan("za.co.johanmynhardt.jweatherhistory")
public class JWeatherHistoryUI {
	private static Logger LOG = LoggerFactory.getLogger(JWeatherHistoryUI.class);

	public static void main(String[] args) {


		try {
			PlasticLookAndFeel plasticLookAndFeel = new PlasticLookAndFeel();
			PlasticLookAndFeel.setCurrentTheme(new LightGray());
			UIManager.setLookAndFeel(plasticLookAndFeel);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		ApplicationContext context = new AnnotationConfigApplicationContext(JWeatherHistoryUI.class);

        LOG.info("----------------------------------------");
        LOG.info("Starting at " + new Date());
        LOG.debug("Launching JWeatherHistoryUI...");

        MainFrame mainFrame = context.getBean(MainFrame.class);
        mainFrame.setVisible(true);


		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("----------------------------------------");
            LOG.info("Shutting down at " + new Date());
        }));
	}
}

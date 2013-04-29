package za.co.johanmynhardt.jweatherhistory.gui;

import javax.swing.*;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;

public class JWeatherHistoryUI {
	public static void main(String[] args) {
		try {
			PlasticLookAndFeel plasticLookAndFeel = new PlasticLookAndFeel();
			PlasticLookAndFeel.setCurrentTheme(new LightGray());
			UIManager.setLookAndFeel(plasticLookAndFeel);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new MainFrame();
	}
}

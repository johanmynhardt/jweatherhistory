package za.co.johanmynhardt.jweatherhistory.gui;

import javax.swing.*;

import java.awt.*;

import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.TitledPanelGenerator;
import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.UIBuilderService;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author Johan Mynhardt
 */
@org.springframework.stereotype.Component
public class WeatherEntryDisplayPanel extends JPanel {
	TitledPanelGenerator uiBuilderService = new UIBuilderService();
	JTextField entryDateDisplay = new JTextField();
	JTextField captureDateDisplay = new JTextField();
	JTextField minTempDisplay = new JTextField();
	JTextField maxTempDisplay = new JTextField();
	JTextArea descriptionDisplay = new JTextArea();
	JTextField windDirectionDisplay = new JTextField();
	JTextField windSpeedDisplay = new JTextField();
	JTextArea windDescriptionDisplay = new JTextArea();
	JTextField rainVolumeDisplay = new JTextField();
	JTextArea rainDescriptionDisplay = new JTextArea();

	public WeatherEntryDisplayPanel() {
		setLayout(new BorderLayout());
		add(buildWeatherPanel(), BorderLayout.CENTER);

		JPanel bottom = new JPanel(new GridBagLayout());
		bottom.add(buildWindAndRainPanel(), new GridBagConstraints(0, 0, 1, 5, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(bottom, BorderLayout.SOUTH);
	}

	private JPanel buildWeatherPanel() {
		JPanel datePanel = new JPanel(new GridBagLayout());

		datePanel.add(displayLabel(new JLabel("Captured: "), captureDateDisplay), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		captureDateDisplay.setEditable(false);
		datePanel.add(displayLabel(new JLabel("Entry Date: "), entryDateDisplay), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		entryDateDisplay.setEditable(false);
		datePanel.add(displayLabel(new JLabel("Min Temp: "), minTempDisplay), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		minTempDisplay.setEditable(false);
		datePanel.add(displayLabel(new JLabel("Max Temp: "), maxTempDisplay), new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		maxTempDisplay.setEditable(false);
		datePanel.add(displayLabel(new JLabel(" "), new JScrollPane(descriptionDisplay)), new GridBagConstraints(3, 0, 2, 2, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		descriptionDisplay.setEditable(false);

		return uiBuilderService.newTopTitledPanel("Weather Info", datePanel);
	}

	private JPanel displayLabel(JLabel label, Component labelDisplay) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(label, BorderLayout.WEST);
		panel.add(labelDisplay, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildWindAndRainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());

		panel.add(new JLabel("Wind Direction"), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		windDirectionDisplay.setEditable(false);
		panel.add(windDirectionDisplay, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		panel.add(new JLabel("Wind Speed"), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		windSpeedDisplay.setEditable(false);
		panel.add(windSpeedDisplay, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		windDescriptionDisplay.setEditable(false);
		panel.add(new JScrollPane(windDescriptionDisplay), new GridBagConstraints(0, 2, 2, 4, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));

		panel.add(new JLabel("Rain Volume"), new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		panel.add(rainVolumeDisplay, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		rainVolumeDisplay.setEditable(false);
		panel.add(new JScrollPane(rainDescriptionDisplay), new GridBagConstraints(2, 1, 2, 5, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		rainDescriptionDisplay.setEditable(false);

		return uiBuilderService.newTopTitledPanel("Wind and Rain", panel);
	}

	public void displayWeatherEntry(WeatherEntry weatherEntry) {
		entryDateDisplay.setText(weatherEntry.getEntryDate().toString());
		captureDateDisplay.setText(weatherEntry.getCaptureDate().toString());
		minTempDisplay.setText(weatherEntry.getMinimumTemperature() + "");
		maxTempDisplay.setText(weatherEntry.getMaximumTemperature() + "");
		descriptionDisplay.setText(weatherEntry.getDescription().trim().isEmpty() ? "No description" : weatherEntry.getDescription());
		windDirectionDisplay.setText(weatherEntry.getWindEntry().getWindDirection().name());
		windSpeedDisplay.setText(weatherEntry.getWindEntry().getWindspeed() + "");
		rainVolumeDisplay.setText(weatherEntry.getRainEntry().getVolume() + "");
		rainDescriptionDisplay.setText(weatherEntry.getRainEntry().getDescription());
	}
}

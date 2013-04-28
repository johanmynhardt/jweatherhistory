package za.co.johanmynhardt.jweatherhistory.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.Border;

import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.UIBuilderService;
import za.co.johanmynhardt.jweatherhistory.impl.service.WeatherHistoryService;
import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
public class WeatherEntryEditor extends JFrame {
	private final Logger logger = Logger.getLogger(WeatherEntryEditor.class.getName());
	private final WeatherHistoryService weatherHistoryService;
	private final WeatherEntryListener entryListener;
	JTextField tfDate = new JTextField();
	JSpinner jSpinnerMin = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
	JSpinner jSpinnerMax = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
	JTextArea taDescription = new JTextArea();
	JComboBox<WindDirection> windDirectionJComboBox = new JComboBox<>(WindDirection.values());
	JSpinner jSpinnerRainVolume = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
	JTextArea taWindDescription = new JTextArea();
	JTextArea taRainDescription = new JTextArea();
	private UIBuilderService uiBuilderService = new UIBuilderService();


	public WeatherEntryEditor(WeatherHistoryService weatherHistoryService, WeatherEntryListener listener) throws HeadlessException {
		this.weatherHistoryService = weatherHistoryService;
		this.entryListener = listener;
		setTitle("Add/Edit WeatherEntry");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setSize(700, 400);

		buildGrid();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void buildGrid() {
		setLayout(new GridBagLayout());
		add(newTopTitledPanel("Date for Weather Entry", tfDate), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(newTopTitledPanel("Min Temp", jSpinnerMin), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(newTopTitledPanel("Max Temp", jSpinnerMax), new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(newTopTitledPanel("Weather Description", new JScrollPane(taDescription)), new GridBagConstraints(3, 0, 2, 2, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));

		add(newTopTitledPanel("Select Wind Direction", windDirectionJComboBox), new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(newTopTitledPanel("Set Rainfall", jSpinnerRainVolume), new GridBagConstraints(3, 2, 2, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));

		add(newTopTitledPanel("Wind Entry Description", new JScrollPane(taWindDescription)), new GridBagConstraints(0, 4, 2, 2, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		add(newTopTitledPanel("Rain Entry Description", new JScrollPane(taRainDescription)), new GridBagConstraints(3, 4, 2, 2, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));

		add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(0, 6, 6, 1, 0, 1, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 5, 5));

		JPanel buttonPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
		buttonPanel.setLayout(boxLayout);

		JPanel cancelSavePanel = new JPanel(new BorderLayout());

		buttonPanel.add(uiBuilderService.newJButton("Cancel", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				logger.info("Disposing of WeatherEntryEditor.");
				dispose();
			}
		}));
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(uiBuilderService.newJButton("OK", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				//TODO set entryDate
				RainEntry rainEntry = new RainEntry(-1, (Integer) jSpinnerRainVolume.getValue(), taRainDescription.getText(), null);
				WindEntry windEntry = new WindEntry();
				windEntry.setWindDirection((WindDirection) windDirectionJComboBox.getSelectedItem());
				windEntry.setDescription(taWindDescription.getText());
				saveNewWeatherEntry(taDescription.getText(), new Date(), rainEntry, windEntry);
				entryListener.updateItems();
				dispose();
			}
		}));
		cancelSavePanel.add(buttonPanel, BorderLayout.EAST);

		add(cancelSavePanel, new GridBagConstraints(4, 7, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
	}

	public void saveNewWeatherEntry(String description, Date entryDate, RainEntry rainEntry, WindEntry windEntry) {
		weatherHistoryService.createWeatherEntry(description, entryDate, 0, 0, rainEntry, windEntry);
	}

	private JPanel newTopTitledPanel(String title, Component component) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(topTitledBorder(title));
		panel.add(component, BorderLayout.CENTER);
		return panel;
	}

	protected Border topTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray), title);
	}
}
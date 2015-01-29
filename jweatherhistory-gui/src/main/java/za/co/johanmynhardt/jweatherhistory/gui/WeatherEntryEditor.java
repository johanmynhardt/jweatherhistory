package za.co.johanmynhardt.jweatherhistory.gui;

import com.google.common.eventbus.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Date;

import static java.lang.String.format;

import za.co.johanmynhardt.jweatherhistory.gui.events.ItemsUpdatedEvent;
import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.UIBuilderService;
import za.co.johanmynhardt.jweatherhistory.impl.service.WeatherHistoryService;
import za.co.johanmynhardt.jweatherhistory.model.RainEntry;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;
import za.co.johanmynhardt.jweatherhistory.model.WindEntry;
import za.co.johanmynhardt.jweatherhistory.model.wind.WindDirection;

/**
 * @author Johan Mynhardt
 */
@org.springframework.stereotype.Component
public class WeatherEntryEditor extends JFrame {
	private final Logger logger = LoggerFactory.getLogger(WeatherEntryEditor.class);

	@Inject
	private WeatherHistoryService weatherHistoryService;
	private WeatherEntry weatherEntry;
	JTextField tfDate = new JTextField();
	JSpinner jSpinnerMin = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
	JSpinner jSpinnerMax = new JSpinner(new SpinnerNumberModel(0, -100, 100, 1));
	JSpinner jSpinnerWindSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
	JTextArea taDescription = new JTextArea();
	JComboBox<WindDirection> windDirectionJComboBox = new JComboBox<>(WindDirection.values());
	JSpinner jSpinnerRainVolume = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
	JTextArea taWindDescription = new JTextArea();
	JTextArea taRainDescription = new JTextArea();

	@Inject
	private UIBuilderService uiBuilderService;

	@Inject
	private EventBus eventBus;

	public WeatherEntryEditor() throws HeadlessException {
		//this.entryListener = listener;
		setTitle("Add/Edit WeatherEntry");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setSize(700, 400);

		setLocationRelativeTo(null);
		setVisible(true);
	}

	@PostConstruct
	public void init() {
		buildGrid();
	}

	private void buildGrid() {
		setLayout(new GridBagLayout());
		tfDate.setToolTipText(format("Format: %s (eg: %s)", WeatherHistoryService.DATE_FORMAT, WeatherHistoryService.simpleDateFormat.format(new java.util.Date())));
		add(uiBuilderService.newTopTitledPanel("Date for Weather Entry", tfDate), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(uiBuilderService.newTopTitledPanel("Min Temp", jSpinnerMin), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(uiBuilderService.newTopTitledPanel("Max Temp", jSpinnerMax), new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(uiBuilderService.newTopTitledPanel("Weather Description", new JScrollPane(taDescription)), new GridBagConstraints(3, 0, 2, 2, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));

		add(uiBuilderService.newTopTitledPanel("Select Wind Direction", windDirectionJComboBox), new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(uiBuilderService.newTopTitledPanel("Select Wind Speed", jSpinnerWindSpeed), new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		add(uiBuilderService.newTopTitledPanel("Set Rainfall", jSpinnerRainVolume), new GridBagConstraints(3, 2, 2, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));

		add(uiBuilderService.newTopTitledPanel("Wind Entry Description", new JScrollPane(taWindDescription)), new GridBagConstraints(0, 4, 2, 2, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
		add(uiBuilderService.newTopTitledPanel("Rain Entry Description", new JScrollPane(taRainDescription)), new GridBagConstraints(3, 4, 2, 2, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));

		add(new JSeparator(JSeparator.HORIZONTAL), new GridBagConstraints(0, 6, 6, 1, 0, 1, GridBagConstraints.PAGE_END, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 5, 5));

		JPanel buttonPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
		buttonPanel.setLayout(boxLayout);

		JPanel cancelSavePanel = new JPanel(new BorderLayout());

		buttonPanel.add(uiBuilderService.newJButton("Cancel", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				logger.debug("Disposing of WeatherEntryEditor.");
				dispose();
			}
		}));
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(uiBuilderService.newJButton("OK", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				//TODO use databinding?
				try {
					if (weatherEntry != null) {
						RainEntry rainEntry = new RainEntry(weatherEntry.getRainEntry().id, (Integer) jSpinnerRainVolume.getValue(), taRainDescription.getText(), null);
						WindEntry windEntry = new WindEntry(weatherEntry.getWindEntry().id, taWindDescription.getText(), (WindDirection) windDirectionJComboBox.getSelectedItem(), (Integer) jSpinnerWindSpeed.getValue(), null);
						WeatherEntry updateWeatherEntry = new WeatherEntry(
								weatherEntry.getId(),
								taDescription.getText(),
								WeatherHistoryService.simpleDateFormat.parse(tfDate.getText()),
								new Date(),
								((Integer) jSpinnerMin.getValue()),
								((Integer) jSpinnerMax.getValue()),
								windEntry,
								rainEntry
						);
						weatherHistoryService.updateFromEdit(updateWeatherEntry);
						eventBus.post(new ItemsUpdatedEvent(){});

						dispose();
					} else {
						RainEntry rainEntry = new RainEntry(-1, (Integer) jSpinnerRainVolume.getValue(), taRainDescription.getText(), null);
						WindEntry windEntry = new WindEntry(-1, taWindDescription.getText(), (WindDirection) windDirectionJComboBox.getSelectedItem(), (Integer) jSpinnerWindSpeed.getValue(), null);
						WeatherEntry weatherEntry = new WeatherEntry(-1, taDescription.getText(), WeatherHistoryService.simpleDateFormat.parse(tfDate.getText()), new Date(), ((Integer) jSpinnerMin.getValue()), ((Integer) jSpinnerMax.getValue()), windEntry, rainEntry);
						weatherHistoryService.createWeatherEntry(weatherEntry);
						eventBus.post(new ItemsUpdatedEvent(){});
						dispose();

					}
				} catch (ParseException e) {
					logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
				}
			}
		}));
		cancelSavePanel.add(buttonPanel, BorderLayout.EAST);

		add(cancelSavePanel, new GridBagConstraints(4, 7, 1, 1, 1, 0, GridBagConstraints.BASELINE, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
	}

	public void editEntry(WeatherEntry weatherEntry) {
		this.weatherEntry = weatherEntry;
		logger.debug("Editing weatherEntry={}", weatherEntry);

		resetFields();

		if (weatherEntry != null) {
			tfDate.setText(weatherEntry.getCaptureDate().toString());
			jSpinnerMin.setValue(weatherEntry.getMinimumTemperature());
			jSpinnerMax.setValue(weatherEntry.getMaximumTemperature());
			taDescription.setText(weatherEntry.getDescription());
			windDirectionJComboBox.setSelectedItem(weatherEntry.getWindEntry().windDirection);
			jSpinnerWindSpeed.setValue(weatherEntry.getWindEntry().windspeed);
			jSpinnerRainVolume.setValue(weatherEntry.getRainEntry().volume);
			taWindDescription.setText(weatherEntry.getWindEntry().description);
			taRainDescription.setText(weatherEntry.getRainEntry().description);
		}
		setVisible(true);
	}

	private void resetFields() {
		tfDate.setText(null);
		jSpinnerMin.setValue(0);
		jSpinnerMax.setValue(0);
		taDescription.setText(null);
		windDirectionJComboBox.setSelectedItem(null);
		jSpinnerWindSpeed.setValue(0);
		jSpinnerRainVolume.setValue(0);
		taWindDescription.setText(null);
		taRainDescription.setText(null);
	}
}

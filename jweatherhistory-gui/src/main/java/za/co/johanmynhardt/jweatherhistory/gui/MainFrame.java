package za.co.johanmynhardt.jweatherhistory.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.MenuBarBuilder;
import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.UIBuilderService;
import za.co.johanmynhardt.jweatherhistory.impl.service.WeatherHistoryService;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

import static java.lang.String.format;

/**
 * @author Johan Mynhardt
 */
public class MainFrame extends JFrame implements WeatherEntryListener {

	private final WeatherHistoryService weatherHistoryService = new WeatherHistoryService();
	private final Logger logger = Logger.getLogger(MainFrame.class.getName());
	private final UIBuilderService builderService = new UIBuilderService();

	java.util.List<WeatherEntry> entries = new ArrayList<>();
	JTable jTable = new JTable();
	TableModel tableModel;

	JButton jButtonNewEntry = builderService.newJButton("New Entry", new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			new WeatherEntryEditor(weatherHistoryService, MainFrame.this);
		}
	});
	JButton jbuttonEditEntry = builderService.newJButton("Edit Entry", new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			new WeatherEntryEditor(weatherHistoryService, MainFrame.this, entries.get(jTable.getSelectedRow()));
		}
	});

	private MenuBarBuilder menuBarBuilder = builderService.newMenuBarBuilder("File");

	public MainFrame() throws HeadlessException {
		try {
			//http://findicons.com/files/icons/2130/aluminum/59/weather.png
			setIconImage(new ImageIcon(MainFrame.class.getResource("/icons/weather.png")).getImage());
		} catch (Exception e) {
			logger.warning(format("Could not set icon image. (%s: %s)", e.getClass().getSimpleName(), e.getMessage()));
		}
		setTitle("JWeatherHistory");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		setJMenuBar(menuBarBuilder.addJMenuItem("Exit", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				System.exit(0);
			}
		}).build());

		tableModel = new AbstractTableModel() {
			public int getRowCount() {
				return entries.size();
			}

			@Override
			public int getColumnCount() {
				return 8;
			}

			@Override
			public Object getValueAt(int row, int column) {
				WeatherEntry selectedEntry = entries.get(row);

				switch (column) {
					case 1:
						return selectedEntry.id;
					case 2:
						return selectedEntry.description;
					case 3:
						return selectedEntry.captureDate == null ? null : selectedEntry.captureDate;
					case 4:
						return selectedEntry.entryDate == null ? null : selectedEntry.entryDate;
					case 5:
						return selectedEntry.minimumTemperature;
					case 6:
						return selectedEntry.maximumTemperature;
					default:
						return null;
				}
			}
		};

		TableColumnModel tableColumnModel = createTableColumnModel();

		jTable = new JTable(tableModel, tableColumnModel);
		DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				if (!listSelectionEvent.getValueIsAdjusting()) {
					jbuttonEditEntry.setEnabled(true);
				}
			}
		});
		jTable.setSelectionModel(selectionModel);

		JScrollPane scrollPane = new JScrollPane(jTable);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Weather Entries"));

		JToolBar toolBar = new JToolBar("Weather Entry Actions", JToolBar.HORIZONTAL);
		toolBar.add(jButtonNewEntry);
		jbuttonEditEntry.setEnabled(false);
		toolBar.add(jbuttonEditEntry);
		toolBar.add(new JToolBar.Separator());
		add(toolBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);

		setVisible(true);
		updateItems();
	}

	@Override
	public void updateItems() {
		entries = weatherHistoryService.getAllWeatherEntries();
		((AbstractTableModel)tableModel).fireTableDataChanged();
		jbuttonEditEntry.setEnabled(false);
	}

	private TableColumnModel createTableColumnModel() {
		TableColumnModel tableColumnModel = new DefaultTableColumnModel();

		tableColumnModel.addColumn(builderService.getTableColumn(1, "ID"));
		tableColumnModel.addColumn(builderService.getTableColumn(4, "Entry Date"));
		tableColumnModel.addColumn(builderService.getTableColumn(2, "Description"));
		tableColumnModel.addColumn(builderService.getTableColumn(5, "Minimum Temperature"));
		tableColumnModel.addColumn(builderService.getTableColumn(6, "Maximum Temperature"));
		tableColumnModel.addColumn(builderService.getTableColumn(3, "Date Captured"));

		return tableColumnModel;
	}

}

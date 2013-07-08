package za.co.johanmynhardt.jweatherhistory.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ListDataListener;
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
	java.util.List<YearItem> yearList = new ArrayList<>();
	java.util.List<MonthItem> monthList = new ArrayList<>();
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
	YearItem selectedYear = new YearItem(-1, "All Time");
	MonthItem selectedMonth = new MonthItem(-1, "All Months");
	JComboBox<YearItem> yearSelector = new JComboBox<>(new ComboBoxModel<YearItem>() {
		//TODO: Clean this model up.
		Set<ListDataListener> listDataListeners = new HashSet<>();

		@Override
		public Object getSelectedItem() {
			return selectedYear;
		}

		@Override
		public void setSelectedItem(Object o) {
			selectedYear = (YearItem) o;
			updateItems();
		}

		@Override
		public int getSize() {
			return yearList.size();
		}

		@Override
		public YearItem getElementAt(int i) {
			return yearList.get(i);
		}

		@Override
		public void addListDataListener(ListDataListener listDataListener) {
			listDataListeners.add(listDataListener);
		}

		@Override
		public void removeListDataListener(ListDataListener listDataListener) {
			listDataListeners.remove(listDataListener);
		}
	});

	JComboBox<MonthItem> monthSelector = new JComboBox<>(new ComboBoxModel<MonthItem>() {
		Set<ListDataListener> listDataListeners = new HashSet<>();
		@Override
		public void setSelectedItem(Object o) {
			selectedMonth = (MonthItem) o;
			updateItems();
		}

		@Override
		public Object getSelectedItem() {
			return selectedMonth;
		}

		@Override
		public int getSize() {
			return monthList.size();
		}

		@Override
		public MonthItem getElementAt(int i) {
			return monthList.get(i);
		}

		@Override
		public void addListDataListener(ListDataListener listDataListener) {
			listDataListeners.add(listDataListener);
		}

		@Override
		public void removeListDataListener(ListDataListener listDataListener) {
			listDataListeners.remove(listDataListener);
		}
	});

	WeatherEntryDisplayPanel weatherEntryDisplayPanel = new WeatherEntryDisplayPanel();
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

				if (jTable.getSelectedRow() >= 0) {
					weatherEntryDisplayPanel.displayWeatherEntry(entries.get(jTable.getSelectedRow()));
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

		toolBar.add(monthSelector);
		toolBar.add(yearSelector);

		add(toolBar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(weatherEntryDisplayPanel, BorderLayout.SOUTH);

		setVisible(true);
		updateItems();
	}

	@Override
	public void updateItems() {
		entries = weatherHistoryService.getAllWeatherEntries();

		Set<YearItem> years = new TreeSet<>();
		years.add(new YearItem(-1, "All Time"));
		Calendar calendar = Calendar.getInstance();

		Set<MonthItem> months = new TreeSet<>();
		months.add(new MonthItem(-1, "All Months"));
		months.add(new MonthItem(Calendar.JANUARY, "January"));
		months.add(new MonthItem(Calendar.FEBRUARY, "February"));
		months.add(new MonthItem(Calendar.MARCH, "March"));
		months.add(new MonthItem(Calendar.APRIL, "April"));
		months.add(new MonthItem(Calendar.MAY, "May"));
		months.add(new MonthItem(Calendar.JUNE, "June"));
		months.add(new MonthItem(Calendar.JULY, "July"));
		months.add(new MonthItem(Calendar.AUGUST, "August"));
		months.add(new MonthItem(Calendar.SEPTEMBER, "September"));
		months.add(new MonthItem(Calendar.OCTOBER, "October"));
		months.add(new MonthItem(Calendar.NOVEMBER, "November"));
		months.add(new MonthItem(Calendar.DECEMBER, "December"));

		monthList = new ArrayList<>(months);

		java.util.List<WeatherEntry> toKeep = new ArrayList<>();
		for (WeatherEntry entry : entries) {
			calendar.setTime(entry.entryDate);
			years.add(new YearItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR)+""));
			if (selectedYear.year == calendar.get(Calendar.YEAR) || selectedYear.year == -1) {
				if (selectedMonth.month == calendar.get(Calendar.MONTH) || selectedMonth.month == -1)
					toKeep.add(entry);
			}
		}
		yearList = new ArrayList<>(years);



		Collections.sort(entries, new Comparator<WeatherEntry>() {
			@Override
			public int compare(WeatherEntry weatherEntry, WeatherEntry weatherEntry2) {
				return weatherEntry.entryDate.compareTo(weatherEntry2.entryDate);
			}
		});

		entries.retainAll(toKeep);


		((AbstractTableModel) tableModel).fireTableDataChanged();
		jbuttonEditEntry.setEnabled(false);
	}

	private class YearItem implements Comparable<YearItem> {
		final int year;
		final String display;

		private YearItem(int year, String display) {
			this.year = year;
			this.display = display;
		}

		@Override
		public String toString() {
			return display;
		}

		@Override
		public int compareTo(YearItem yearItem) {
			return Integer.compare(this.year, yearItem.year);
		}
	}

	private class MonthItem implements Comparable<MonthItem> {
		final int month;
		final String display;

		private MonthItem(int month, String display) {
			this.display = display;
			this.month = month;
		}

		@Override
		public String toString() {
			return display;
		}

		@Override
		public int compareTo(MonthItem monthItem) {
			return Integer.compare(this.month, monthItem.month);
		}
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

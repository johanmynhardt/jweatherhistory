package za.co.johanmynhardt.jweatherhistory.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import za.co.johanmynhardt.jweatherhistory.impl.service.WeatherHistoryService;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author Johan Mynhardt
 */
public class MainFrame extends JFrame {

	private final WeatherHistoryService weatherHistoryService = new WeatherHistoryService();
	private final Logger logger = Logger.getLogger(MainFrame.class.getName());

	public MainFrame() throws HeadlessException {
		setTitle("JWeatherHistory");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		setJMenuBar(createMenuBar());

		TableModel tableModel = new AbstractTableModel() {
			java.util.List<WeatherEntry> entries = weatherHistoryService.getAllWeatherEntries();

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
				logger.info("selectedEntry = " + selectedEntry);

				switch (column) {
					case 1:
						return selectedEntry.getId();
					case 2:
						return selectedEntry.getDescription();
					case 3:
						return selectedEntry.getCaptureDate() == null ? null : selectedEntry.getCaptureDate();
					case 4:
						return selectedEntry.getEntryDate() == null ? null : selectedEntry.getEntryDate();
					default:
						return null;
				}
			}
		};

		TableColumnModel tableColumnModel = new DefaultTableColumnModel();
		tableColumnModel.addColumn(new TableColumn());

		JTable jTable = new JTable(tableModel, createTableColumnModel());

		JScrollPane scrollPane = new JScrollPane(jTable);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Weather Entries"));

		JToolBar toolBar = new JToolBar("Weather Entry Actions", JToolBar.HORIZONTAL);
		toolBar.add(new JButton("New Entry"));
		toolBar.add(new JToolBar.Separator());

		add(toolBar, BorderLayout.NORTH);

		add(scrollPane, BorderLayout.CENTER);

		setVisible(true);
	}

	private TableColumnModel createTableColumnModel() {
		TableColumnModel tableColumnModel = new DefaultTableColumnModel();

		TableColumnGenerator tableColumnGenerator = new TableColumnGenerator() {
			@Override
			public TableColumn getTableColumn(int index, String title) {
				TableColumn tableColumn = new TableColumn(index);
				tableColumn.setHeaderValue(title);
				return tableColumn;
			}
		};

		tableColumnModel.addColumn(tableColumnGenerator.getTableColumn(1, "ID"));
		tableColumnModel.addColumn(tableColumnGenerator.getTableColumn(4, "Entry Date"));
		tableColumnModel.addColumn(tableColumnGenerator.getTableColumn(2, "Description"));
		tableColumnModel.addColumn(tableColumnGenerator.getTableColumn(3, "Date Captured"));


		return tableColumnModel;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");

		MenuBarBuilder menuBarBuilder = new MenuBarBuilder() {
			@Override
			public JMenuItem addMenuItem(String title, Action action) {
				JMenuItem menuItem = new JMenuItem();
				menuItem.setText(title);
				menuItem.addActionListener(action);
				return menuItem;
			}
		};

		menu.add(menuBarBuilder.addMenuItem("Dummy", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				logger.info("" + actionEvent);
			}
		}));
		menuBar.add(menu);
		return menuBar;
	}

	interface TableColumnGenerator {
		TableColumn getTableColumn(int index, String title);
	}

	private interface MenuBarBuilder {
		JMenuItem addMenuItem(String title, Action action);
	}
}

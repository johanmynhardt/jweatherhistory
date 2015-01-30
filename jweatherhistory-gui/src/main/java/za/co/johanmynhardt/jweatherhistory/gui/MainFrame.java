package za.co.johanmynhardt.jweatherhistory.gui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import za.co.johanmynhardt.jweatherhistory.api.data.IDataSerializer;
import za.co.johanmynhardt.jweatherhistory.gui.events.ItemsUpdatedEvent;
import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.MenuBarBuilder;
import za.co.johanmynhardt.jweatherhistory.gui.uibuilder.UIBuilderService;
import za.co.johanmynhardt.jweatherhistory.impl.service.WeatherHistoryService;
import za.co.johanmynhardt.jweatherhistory.model.WeatherEntry;

/**
 * @author Johan Mynhardt
 */
@org.springframework.stereotype.Component
public class MainFrame extends JFrame implements ApplicationContextAware {

    private final Logger LOG = LoggerFactory.getLogger(MainFrame.class);

    @Inject
    private WeatherHistoryService weatherHistoryService;

    @Inject
    private UIBuilderService builderService;

    @Inject
    private WeatherEntryDisplayPanel weatherEntryDisplayPanel;

    @Inject
    private WeatherEntryEditor weatherEntryEditor;

    @Inject
    private EventBus eventBus;

    @Inject
    IDataSerializer dataSerializer;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        LOG.debug("applicationContext={}", this.applicationContext);
    }

    java.util.List<WeatherEntry> entries = new ArrayList<>();
    java.util.List<YearItem> yearList = new ArrayList<>();
    java.util.List<MonthItem> monthList = new ArrayList<>();
    JTable jTable = new JTable();
    TableModel tableModel;
    JButton jButtonNewEntry;
    JButton jbuttonEditEntry;
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

    private MenuBarBuilder fileMenuBuilder;
    private MenuBarBuilder dataMenuBuilder;

    @PostConstruct
    public void init() {
        jButtonNewEntry = builderService.newJButton("New Entry", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                weatherEntryEditor.editEntry(null);
            }
        });

        jbuttonEditEntry = builderService.newJButton("Edit Entry", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                weatherEntryEditor.editEntry(entries.get(jTable.getSelectedRow()));
            }
        });

        fileMenuBuilder = builderService.newMenuBarBuilder("File");
        dataMenuBuilder = builderService.newMenuBarBuilder("Data");

        eventBus.register(this);

        initFrame();
    }

    public MainFrame() throws HeadlessException {
    }

    private void initFrame() {
        try {
            //http://findicons.com/files/icons/2130/aluminum/59/weather.png
            setIconImage(new ImageIcon(MainFrame.class.getResource("/icons/weather.png")).getImage());
        } catch (Exception e) {
            LOG.warn("Could not set icon image. ({}: {})", e.getClass().getSimpleName(), e.getMessage());
        }
        setTitle("JWeatherHistory");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        final JMenu fileMenu = fileMenuBuilder.addJMenuItem("Exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        }).build().getMenu(0);

        JMenu dataMenu = new JMenu("Data");

        dataMenu.add(newMenuItem("Export", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final File file = Files.createTempFile("jweatherhistory", dataSerializer.getExtension()).toFile();
                    dataSerializer.exportWeatherEntries(new FileOutputStream(file));
                    LOG.info("Exported data to {}", file);
                } catch (IOException e1) {
                    LOG.error("Error", e1);
                }
            }
        }));

        dataMenu.add(newMenuItem("Import", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dataSerializer.importWeatherEntries(new FileInputStream(new File("/tmp/jweatherhistory6931970176828581665.json")));
                    eventBus.post(new ItemsUpdatedEvent() {
                    });
                } catch (IOException e1) {
                    LOG.error("Error", e1);
                }
            }
        }));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(dataMenu);

        setJMenuBar(menuBar);

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
                    return selectedEntry.getId();
                case 2:
                    return selectedEntry.getDescription();
                case 3:
                    return selectedEntry.getCaptureDate() == null ? null : selectedEntry.getCaptureDate();
                case 4:
                    return selectedEntry.getEntryDate() == null ? null : selectedEntry.getEntryDate();
                case 5:
                    return selectedEntry.getMinimumTemperature();
                case 6:
                    return selectedEntry.getMaximumTemperature();
                default:
                    return null;
                }
            }
        };

        TableColumnModel tableColumnModel = createTableColumnModel();

        jTable = new JTable(tableModel, tableColumnModel);
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                jbuttonEditEntry.setEnabled(true);
            }

            if (jTable.getSelectedRow() >= 0) {
                weatherEntryDisplayPanel.displayWeatherEntry(entries.get(jTable.getSelectedRow()));
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

    }

    private JMenuItem newMenuItem(String title, Action action) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setText(title);
        menuItem.addActionListener(action);
        return menuItem;
    }

    @Subscribe
    public void receiveUpdateItems(ItemsUpdatedEvent event) {
        LOG.debug("Receive itemsUpdatedEvent={}", event);
        updateItems();
    }

    private void updateItems() {
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

        java.util.List<WeatherEntry> toKeep = entries.stream()
                .filter((weatherEntry) -> {
                    calendar.setTime(weatherEntry.getEntryDate());
                    years.add(new YearItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + ""));
                    return (selectedYear.year == calendar.get(Calendar.YEAR) || selectedYear.year == -1) && (selectedMonth.month == calendar.get(Calendar.MONTH)
                            || selectedMonth.month == -1);
                })
                .sorted((weatherEntry, weatherEntry2) -> weatherEntry.getEntryDate().compareTo(weatherEntry2.getEntryDate()))
                .collect(Collectors.toList());

        yearList = new ArrayList<>(years);

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

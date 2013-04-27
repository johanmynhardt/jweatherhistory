package za.co.johanmynhardt.jweatherhistory.gui.uibuilder;

import javax.swing.table.TableColumn;

/**
 * @author Johan Mynhardt
 */
public interface TableColumnEmitter {
	TableColumn getTableColumn(int index, String title);
}

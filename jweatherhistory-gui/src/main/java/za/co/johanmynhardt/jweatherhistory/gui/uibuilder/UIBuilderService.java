package za.co.johanmynhardt.jweatherhistory.gui.uibuilder;

import javax.swing.*;
import javax.swing.table.TableColumn;

/**
 * @author Johan Mynhardt
 */
public class UIBuilderService implements JButtonGenerator, MenuBarBuilder, TableColumnEmitter {


	@Override
	public JButton newJButton(String title, Action action) {
		JButton button = new JButton(title);
		button.addActionListener(action);
		return button;
	}

	@Override
	public MenuBarBuilder addJMenuItem(String title, Action action) {
		throw new RuntimeException("Use buildService.newMenuBarBuilder(String title) to obtain the correct reference.");
	}

	@Override
	public JMenuBar build() {
		throw new RuntimeException("Use buildService.newMenuBarBuilder(String title) to obtain the correct reference.");
	}

	@Override
	public MenuBarBuilder newMenuBarBuilder(String title) {
		return new MenuBarBuilderImpl(title);
	}

	@Override
	public TableColumn getTableColumn(int index, String title) {
		TableColumn tableColumn = new TableColumn(index);
		tableColumn.setHeaderValue(title);
		return tableColumn;
	}

	class MenuBarBuilderImpl implements MenuBarBuilder {
		final JMenu menu;
		JMenuBar menuBar = new JMenuBar();

		MenuBarBuilderImpl(String title) {
			this.menu = new JMenu(title);
		}

		@Override
		public MenuBarBuilder newMenuBarBuilder(String title) {
			throw new RuntimeException("Already have an instance of me.");
		}

		@Override
		public MenuBarBuilder addJMenuItem(String title, Action action) {
			JMenuItem menuItem = new JMenuItem();
			menuItem.setText(title);
			menuItem.addActionListener(action);
			menu.add(menuItem);
			return this;
		}

		@Override
		public JMenuBar build() {
			menuBar.add(menu);
			return menuBar;
		}
	}
}

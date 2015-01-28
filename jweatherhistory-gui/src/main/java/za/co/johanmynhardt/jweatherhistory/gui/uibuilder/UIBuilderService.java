package za.co.johanmynhardt.jweatherhistory.gui.uibuilder;

import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.table.TableColumn;

import java.awt.*;

/**
 * @author Johan Mynhardt
 */
@Service
public class UIBuilderService implements JButtonGenerator, MenuBarBuilder, TableColumnEmitter, TitledPanelGenerator {

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

	@Override
	public JPanel newTopTitledPanel(String title, Component component) {
		JPanel header = new JPanel(new BorderLayout());
		header.add(new JLabel(title), BorderLayout.WEST);
		JPanel sepPanel = new JPanel(new BorderLayout());
		sepPanel.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		sepPanel.add(Box.createHorizontalStrut(5), BorderLayout.WEST);
		sepPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
		header.add(sepPanel, BorderLayout.CENTER);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(header, BorderLayout.NORTH);
		panel.add(component, BorderLayout.CENTER);
		return panel;
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

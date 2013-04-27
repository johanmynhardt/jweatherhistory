package za.co.johanmynhardt.jweatherhistory.gui.uibuilder;

import javax.swing.*;

/**
 * @author Johan Mynhardt
 */
public interface MenuBarBuilder {
	MenuBarBuilder newMenuBarBuilder(String title);
	MenuBarBuilder addJMenuItem(String title, Action action);
	JMenuBar build();
}

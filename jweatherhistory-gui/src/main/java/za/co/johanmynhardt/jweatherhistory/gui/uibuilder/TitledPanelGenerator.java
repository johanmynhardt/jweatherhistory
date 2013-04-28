package za.co.johanmynhardt.jweatherhistory.gui.uibuilder;

import java.awt.*;

import javax.swing.*;

/**
 * @author Johan Mynhardt
 */
public interface TitledPanelGenerator {
	public JPanel newTopTitledPanel(String title, Component component);
}

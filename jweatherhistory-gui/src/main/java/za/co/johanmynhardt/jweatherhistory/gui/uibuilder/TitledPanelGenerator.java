package za.co.johanmynhardt.jweatherhistory.gui.uibuilder;

import javax.swing.*;

import java.awt.*;

/**
 * @author Johan Mynhardt
 */
public interface TitledPanelGenerator {
	public JPanel newTopTitledPanel(String title, Component component);
}

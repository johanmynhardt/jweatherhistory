package za.co.johanmynhardt.jweatherhistory.gui.uibuilder;

import javax.swing.*;

/**
 * @author Johan Mynhardt
 */
public interface JButtonGenerator {

	JButton newJButton(String title, Action action);
}

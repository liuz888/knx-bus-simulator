package fr.laas.knxsimulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Guillaume Garzone
 * 
 *         Console view to print data received from the ipu and data that will
 *         be send to it.
 *
 */
public class ConsoleView extends JPanel {

	JLabel labelText = new JLabel();

	public ConsoleView() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(labelText);
		this.setPreferredSize(new Dimension(417, 413));
	}

	public void addText(String text) {
		String oldText = labelText.getText();
		if (oldText == null) {
			oldText = "<html></html>";
		}
		oldText.replaceFirst("<html>", "");

		labelText.setText("<html>" + text + "<br>" + labelText.getText());

	}

}

package fr.laas.knxsimulator.view;

import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author Guillaume Garzone
 * 
 *         View that add input for the user to interact with the bus
 *
 */
public class UserInputView extends JPanel {

	JTextField readAdressTextField = new JTextField(12);
	JButton readButton = new JButton("Generate Read");

	JTextField writeAdressTextField = new JTextField(10);
	JTextField writeValueTextField = new JTextField(5);
	JTextField writeSourceAdressTextField = new JTextField(10);
	JButton writeButton = new JButton("Generate Write");

	public UserInputView() {

		readAdressTextField.setToolTipText("Group adress");
		writeAdressTextField.setToolTipText("Group adress");
		writeValueTextField.setToolTipText("Value");
		writeSourceAdressTextField.setToolTipText("Source/Physical address");

		Box read = Box.createHorizontalBox();
		read.add(new JLabel("read"));
		read.add(readAdressTextField);
		read.add(readButton);

		Box write = Box.createHorizontalBox();
		write.add(new JLabel("write"));
		write.add(writeSourceAdressTextField);
		write.add(writeAdressTextField);
		write.add(writeValueTextField);
		write.add(writeButton);

		Box container = Box.createVerticalBox();
		container.add(read);
		container.add(write);

		add(container);
	}

	public void addReadActionListener(ActionListener actionListener) {
		readButton.addActionListener(actionListener);
	}

	public void addWriteActionListener(ActionListener actionListener) {
		writeButton.addActionListener(actionListener);
	}

	public String getReadAdress() {
		return readAdressTextField.getText();
	}

	public String getWriteAddress() {
		return writeAdressTextField.getText();
	}

	public String getWriteValue() {
		return writeValueTextField.getText();
	}

	public String getWriteSourceAddress() {
		return writeSourceAdressTextField.getText();
	}
}

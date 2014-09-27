package fr.laas.knxsimulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.laas.knxsimulator.main.StartSimulator;

/**
 * 
 * @author Guillaume Garzone
 *
 *         Main UI class that display the window of the simulator.
 *
 */
public class Window extends JFrame implements PrintOnWindow {

	ConsoleView inputView = new ConsoleView();
	ConsoleView outputView = new ConsoleView();
	UserInputView userInputView = new UserInputView();

	private List<ButtonListener> buttonListeners = new ArrayList<ButtonListener>();

	public Window() {
		userInputView.addReadActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (StartSimulator.DEBUG)
					System.out.println("read " + userInputView.getReadAdress());
				notifyRead();
			}
		});

		userInputView.addWriteActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (StartSimulator.DEBUG)
					System.out.println("write "
							+ userInputView.getWriteAddress() + " : "
							+ userInputView.getWriteValue());
				notifyWrite();
			}
		});

		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		topPanel.add(userInputView);
		topPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Generating packet"),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		container.add(topPanel, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Input stream"),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		inputPanel.add(inputView);

		bottomPanel.add(inputPanel);

		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
		outputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Output Stream"),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		outputPanel.add(outputView);

		bottomPanel.add(outputPanel);

		container.add(bottomPanel, BorderLayout.CENTER);

		this.getContentPane().add(container);

		inputView.setPreferredSize(new Dimension(417, 413));
		outputView.setPreferredSize(new Dimension(417, 413));

		this.setTitle("KNX Interface Simulator");
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setSize(900, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void addButtonListener(ButtonListener listener) {
		if (!buttonListeners.contains(listener)) {
			buttonListeners.add(listener);
		}
	}

	private void notifyRead() {
		if (buttonListeners != null) {
			for (ButtonListener l : buttonListeners) {
				l.readCalled(userInputView.getReadAdress());
			}
		}
	}

	private void notifyWrite() {
		if (buttonListeners != null) {
			for (ButtonListener l : buttonListeners) {
				l.writeCalled(userInputView.getWriteSourceAddress(),
						userInputView.getWriteAddress(),
						userInputView.getWriteValue());
			}
		}
	}

	public interface ButtonListener {

		public void readCalled(String address);

		public void writeCalled(String sourceAdress, String targetAddress,
				String value);

	}

	public void printOutput(String message) {
		outputView.addText(message);
	}

	public void printIntput(String message) {
		inputView.addText(message);
	}

}

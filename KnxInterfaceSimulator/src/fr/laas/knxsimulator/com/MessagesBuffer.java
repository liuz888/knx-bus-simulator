package fr.laas.knxsimulator.com;

import java.util.ArrayList;

import fr.laas.knxsimulator.main.StartSimulator;

/**
 * A buffer to store messages received from the BUS (or to send)
 * 
 * @author Guillaume Garzone
 * 
 */
public class MessagesBuffer implements Observable {

	/** allows to store the message waiting for processing */
	private ArrayList<String> fifo;
	/** list of the obervers to notify when a new message is added to the fifo */
	private ArrayList<Observer> listObservers;

	/**
	 * Constructor
	 */
	public MessagesBuffer() {
		this.fifo = new ArrayList<String>();
		this.listObservers = new ArrayList<Observer>();
	}

	/**
	 * Allows to add a message to the buffer
	 * 
	 * @param message
	 */
	public void addMessageToFifo(String message) {
		if (message != null) {
			synchronized (fifo) {
				if (StartSimulator.DEBUG) {
					System.out.println("DEBUG BUFFER: added message - "
							+ message);
				}
				fifo.add(message);
			}
		}
		this.notifyObservers();
	}

	/**
	 * Allows to get the first message in the fifo and delete it from the buffer
	 * 
	 * @return null if fifo is empty
	 */
	public String getFirstMessage() {
		String message = null;
		synchronized (fifo) {
			if (this.fifo.size() != 0) {
				message = this.fifo.get(0);
				this.fifo.remove(0);
			}
		}
		return message;
	}

	public void addObserver(Observer o) {
		listObservers.add(o);
	}

	public void removeObserver(Observer o) {
		listObservers.remove(o);
	}

	public void notifyObservers() {
		for (Observer o : listObservers) {
			o.updateObserver();
		}
	}

}

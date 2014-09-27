package fr.laas.knxsimulator.com;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import fr.laas.knxsimulator.main.StartSimulator;
import fr.laas.knxsimulator.view.Window;

/**
 * Allows to send messages to the KNX IPUa
 * 
 * @author Guillaume Garzone
 * 
 */
public class Sending extends Thread implements Observer{
	/** host:port to connect to send orders */
	private String host;
	private int port;

	/** socket used to transmit data */
	private Socket socket;
	/** allows to stop the thread */
	private boolean running;
	private boolean sleeping;
	/** allows to handle connection loss */
	private static long DELAYRECONNECT = 1000;
	private static int MAXATTEMPT = 100;
	private PrintWriter pred;

	public MessagesBuffer buffer;
	private long delaySleep = 1000;
	
	private Window view ;

	/**
	 * Constructor
	 * 
	 * @param host
	 * @param port
	 */
	public Sending(String host, int port, MessagesBuffer buffer, Window window) {
		this.socket = null;
		this.running = false;
		this.sleeping = false;
		this.host = host;
		this.port = port;
		this.buffer = buffer;
		this.view = window ;
	}

	/**
	 * Stops the thread from running
	 */
	public void stopTransmission() {
		this.running = false;
	}

	/**
	 * Initialization of the connection
	 */
	public boolean initConnection() {

		boolean result = false;

		// opening the socket
		try {
			socket = new Socket(this.host, this.port);
			// initialization of the print writer to send messages using the
			// socket
			pred = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())), true);

			result = true;

		} catch (UnknownHostException e) {
			System.err.println("ERROR: host unreachable");
			// e.printStackTrace() ;
			this.closeConnection();

		} catch (IOException e) {
			// in case of initialization failure, the socket is closed
			this.closeConnection();

		} catch (IllegalArgumentException i) {
			System.err
					.println("ERROR: the sending port configured is out of range.");
			i.printStackTrace();
			this.closeConnection();
		}
		return result;

	}

	/**
	 * Closes the connection, in case of error or when the thread stops
	 */
	public void closeConnection() {

		try {
			// closes the socket
			if (this.socket != null) {
				this.socket.close();
			}
			if (this.pred != null) {
				this.pred.close();
			}
		} catch (IOException e) {
			System.err.println("ERROR while closing connection");
			e.printStackTrace();
		}
	}

	/**
	 * Inits the connection and launches the service
	 */
	public void launchConnection() {

		boolean connectionEstablished = false;
		int count = 0;

		System.out
				.println("SENDER: establishing connection for tranmission of orders...");
		while (!connectionEstablished && this.running) {
			if (StartSimulator.DEBUG) {
				System.out
						.println("DEBUG SENDER: Establishing connection for transmission, atempt: "
								+ count);
			}
			// initialization of the connection
			if (this.initConnection()) {
				connectionEstablished = true;
				System.out.println("SENDER: connection established");
			}
			if (!connectionEstablished) {
				try {
					Thread.sleep(DELAYRECONNECT + 10 * count);
				} catch (InterruptedException e) {
					// interrupted, continue
				}

				count++;
				if (count > MAXATTEMPT) {
					System.err
							.println("SENDER: Connection timeout, stopping transmission");
					this.stopTransmission();
				}
				this.closeConnection();
			}
		}
	}

	/**
	 * Allows to establish the connection and then send the informations from
	 * the bus
	 */
	@Override
	public void run() {
		String message = null;

		this.running = true;
		// lunching the connection
		this.launchConnection();

		// running : sending message each time there is a new one put in the
		// buffer
		while (this.running) {
			this.sleeping = false;
			// getting the first message put in the buffer
			message = this.buffer.getFirstMessage();

			if (message != null) {
				// sending data to the interface
				if (StartSimulator.DEBUG) {
					System.out.println("DEBUG SENDER: sending - " + message);
				}
				pred.println(message);
				view.printOutput(message);
				
			} else {
				this.sleeping = true;
				try {
					Thread.sleep(this.delaySleep);
				} catch (InterruptedException e) {
					// Interrupted: received a new message
				}
			}
		}
		// closing connection
		this.closeConnection();

	}

	/**
	 * Allows to awake the sender if sleeping because of no message to send
	 */
	public void updateObserver() {
		if (this.sleeping) {
			this.interrupt();
		}
	}

	public Window getView() {
		return view;
	}

	public void setView(Window view) {
		this.view = view;
	}

}

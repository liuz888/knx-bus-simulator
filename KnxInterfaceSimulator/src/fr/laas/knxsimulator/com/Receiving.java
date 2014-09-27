package fr.laas.knxsimulator.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import fr.laas.knxsimulator.main.StartSimulator;
import fr.laas.knxsimulator.view.Window;

/**
 * Allows to receive messages from the KNX IPU
 * 
 * @author Guillaume Garzone
 * 
 */
public class Receiving extends Thread {

	/** Port to listen to */
	private int port;
	/** allows to stop the thread if running */
	private boolean running = false;
	/** to launch the server */
	private ServerSocket socketServeur = null;
	/** socket to connect to the client */
	private Socket socketClient = null;
	/** to receive data */
	private BufferedReader in = null;
	/** to send data to the IPU */
	private MessagesBuffer buffer;
	/** link to the graphic view */
	private Window view;

	/**
	 * Constructor
	 * 
	 * @param port
	 *            to listen to
	 * @param buffer
	 *            for the sending part
	 */
	public Receiving(int port, MessagesBuffer buffer, Window window) {
		this.port = port;
		this.buffer = buffer;
		this.setView(window);
	}

	/**
	 * Initializes the connection - server side
	 * 
	 * @return true if success
	 */
	private boolean initServer() {
		boolean result = false;

		try {
			socketServeur = new ServerSocket(port);
			System.out.println("Listening part (server) launched");
			result = true;
		} catch (IOException e) {
			System.err.println("ERROR while launching the server");
			e.printStackTrace();
			this.stopListener();
		} catch (IllegalArgumentException i) {
			System.err.println("ERROR port number out of range");
			i.printStackTrace();
		}
		return result;
	}

	/**
	 * Accepts a connection
	 * 
	 * @return true if success
	 */
	private boolean acceptConnection() {

		boolean result = false;

		try {

			this.socketClient = this.socketServeur.accept();
			this.in = new BufferedReader(new InputStreamReader(
					this.socketClient.getInputStream()));

			result = true;
			System.out.println("Connected to : "
					+ this.socketClient.getInetAddress());

		} catch (IOException e) {

			System.err.println("ERROR while accepting connection");
			e.printStackTrace();
			this.closeConnectionClient();

		}
		return result;
	}

	/**
	 * Closes the connection with the client - closes the socket
	 */
	public void closeConnectionClient() {

		if (this.socketClient != null) {
			try {
				this.socketClient.close();
				System.out.println("RECEIVER : Connection closed");

			} catch (IOException e) {
				System.err.println("ERROR while closing connection");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stops the server
	 */
	public void stopListener() {

		if (this.socketServeur == null) {
			System.err.println("Socket Server was not opened");
		} else {
			try {
				this.socketServeur.close();
			} catch (IOException e) {
				System.err.println("ERROR while closing the server");
				e.printStackTrace();
			}
		}
		this.stopThread();
	}

	/**
	 * Stops the thread if the thread is running, calling stopServer stops all
	 * the service properly
	 */
	public void stopThread() {
		this.running = false;
	}

	/**
	 * Allows to open server side and listen to the appropriate socket
	 */
	@Override
	public void run() {

		String message = null;

		this.running = true;

		if (!this.initServer()) {
			this.running = false;
		} else {
			if (!this.acceptConnection()) {
				this.running = false;
			}
		}

		while (this.running) {

			try {
				message = in.readLine();
				if (message == null) {
					this.running = false;
				} else {
					if (StartSimulator.DEBUG) {
						System.out.println("DEBUG SERVER: received : "
								+ message);
					}
					// printing on screen
					view.printIntput(message);
					// simulation of the bus behavior
					String[] splitted = message.split(" ");
					if (splitted[0].matches("write")) {
						this.buffer.addMessageToFifo("write from 0.0.0 to "
								+ splitted[1] + " " + splitted[2]);
					} else if (splitted[0].matches("read")) {
						this.buffer.addMessageToFifo("read from 0.0.0 to "
								+ splitted[1]);
					}
				}
			} catch (IOException e) {
				System.err.println("ERROR while reading the message");
				e.printStackTrace();
				this.closeConnectionClient();
			}
		}

		this.stopListener();
	}

	public MessagesBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(MessagesBuffer buffer) {
		this.buffer = buffer;
	}

	public Window getView() {
		return view;
	}

	public void setView(Window view) {
		this.view = view;
	}

}

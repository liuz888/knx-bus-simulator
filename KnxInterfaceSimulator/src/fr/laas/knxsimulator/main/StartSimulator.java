package fr.laas.knxsimulator.main;

import fr.laas.knxsimulator.com.MessagesBuffer;
import fr.laas.knxsimulator.com.ProcessMessage;
import fr.laas.knxsimulator.com.Receiving;
import fr.laas.knxsimulator.com.Sending;
import fr.laas.knxsimulator.view.Window;

public class StartSimulator {

	public static final boolean DEBUG = true;

	public static void main(String[] args) {

		String host = "localhost" ;
		int sendingPort = 10081 ;
		int listeningPort = 10080 ;

		Window window = new Window() ;

		MessagesBuffer buffer = new MessagesBuffer() ;
		final Sending sending = new Sending(host, sendingPort, buffer, window) ;
		final Receiving receiving = new Receiving(listeningPort, buffer, window) ;
		
		buffer.addObserver(sending);
		@SuppressWarnings("unused")
		ProcessMessage processMessage = new ProcessMessage(window, buffer) ;
		
		receiving.start();
		sending.start();
		
		

		// closes the connections when program is terminated or shut down
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shut down of services");
				sending.stopTransmission();
				receiving.stopListener();
			}
		});

	}

}

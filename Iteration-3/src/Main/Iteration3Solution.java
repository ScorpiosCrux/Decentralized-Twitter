package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

import java.io.IOException;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;
import MainHandlers.PrintHandler;
import MainHandlers.RegistryHandler;
import Settings.UserSettings;

public class Iteration3Solution {

	// Handlers
	private RegistryHandler registry_handler;
	private NetworkHandler network_handler;
	private PrintHandler print_handler;
	private PeerCommHandler peer_comm_handler;

	private UserSettings settings;


	public Iteration3Solution(UserSettings settings) {
		this.settings = settings;
		try {
			this.network_handler = new NetworkHandler(settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.print_handler = new PrintHandler();
		this.peer_comm_handler = new PeerCommHandler(settings, network_handler);

		// Should be last
		this.registry_handler = new RegistryHandler(settings, this, peer_comm_handler);


		// initial connection to the registry
		try {
			registry_handler.start(settings.client_port);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}

		// communication with the peer
		peer_comm_handler.start();

/* 		// final communicatino with registry
		try {
			client.stop();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} */
	}

	public UserSettings getSettings() {
		return this.settings;
	}

	public NetworkHandler getNetworkHandler() {
		return this.network_handler;
	}

	public PrintHandler getPrintHandler() {
		return this.print_handler;
	}

	



	

	

	



}

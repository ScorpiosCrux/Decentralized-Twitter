package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

import java.io.IOException;

import Main.HelperDataClasses.SourceList;
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

	private final SourceList all_sources = new SourceList();

	public Iteration3Solution(UserSettings settings) {
		startRegistryCommunication(settings);
		startPeerCommunication();
		finishRegistryCommunication();
	}

	public void startRegistryCommunication(UserSettings settings) {
		this.settings = settings;
		this.print_handler = new PrintHandler();
		try {
			this.network_handler = new NetworkHandler(settings);
			this.peer_comm_handler = new PeerCommHandler(settings, network_handler, all_sources);
			this.registry_handler = new RegistryHandler(settings, this, peer_comm_handler);
			registry_handler.start(settings.client_port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void startPeerCommunication() {
		// communication with the peer
		peer_comm_handler.start();
	}

	public void finishRegistryCommunication() {
		/*
		 * // final communicatino with registry
		 * try {
		 * client.stop();
		 * } catch (IOException ioe) {
		 * ioe.printStackTrace();
		 * }
		 */
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

	public PeerCommHandler getPeerCommHandler(){
		return this.peer_comm_handler;
	}

}

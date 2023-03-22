/*
* Author: Tyler Chen
*/

package Main;

/* Imports */
import java.io.IOException;
import Main.HelperDataClasses.SourceList;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;
import MainHandlers.PrintHandler;
import MainHandlers.ProcessHandler;
import MainHandlers.RegistryHandler;
import Settings.UserSettings;

public class Main {

	private final boolean DEBUG = false;

	/* Peer Settings */
	private UserSettings settings;
	/* System Handlers */
	private RegistryHandler registry_handler;
	private NetworkHandler network_handler;
	private PrintHandler print_handler;
	private PeerCommHandler peer_comm_handler;
	/* Data */
	private final SourceList all_sources = new SourceList();

	public static void main(String[] args) {
		UserSettings settings = new UserSettings();
		new Main(settings);
	}

	/*
	 * Function that initiates all threads
	 */
	public Main(UserSettings settings) {
		this.settings = settings;

		/* Initialize Handlers */
		initializeHandlers();
		ProcessHandler.pause(1);

		/* Initialize Registry Communication */
		initializeRegistryCommunication();
		ProcessHandler.pause(1);

		startPeerCommunication();
		finishRegistryCommunication();
	}

	/* Initializes Handlers for this peer */
	private void initializeHandlers() {
		System.out.println("SYSTEM: INITIALIZING HANDLERS");
		try {
			this.print_handler = new PrintHandler();
			this.network_handler = new NetworkHandler(settings);
			this.peer_comm_handler = new PeerCommHandler(settings, network_handler, all_sources);
			this.registry_handler = new RegistryHandler(settings, this, peer_comm_handler);
			System.out.println("SYSTEM: FINISHED INITIALIZING HANDLERS");

		} catch (IOException e) {
			if (DEBUG)
				e.printStackTrace();
			else
				System.out.println("SYSTEM: FAILED TO INITIALIZE");
			System.exit(0);
		}
	}

	/*
	 * Registers this peer to the registry
	 */
	private void initializeRegistryCommunication() {
		try {
			this.registry_handler.start(this.settings.client_port);
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
			else
				System.out.println("SYSTEM: FAILED TO CONNECT TO REGISTRY");
			System.exit(0);
		}

	}

	public void finishRegistryCommunication() {
		/*
		 * // final communication with registry
		 * try {
		 * client.stop();
		 * } catch (IOException ioe) {
		 * ioe.printStackTrace();
		 * }
		 */
	}

	public void startPeerCommunication() {
		// communication with the peer
		peer_comm_handler.start();
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

	public PeerCommHandler getPeerCommHandler() {
		return this.peer_comm_handler;
	}

}

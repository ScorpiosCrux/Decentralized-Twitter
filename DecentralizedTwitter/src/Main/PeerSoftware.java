package Main;

/* Imports */
import java.io.IOException;
import Main.HelperDataClasses.SourceList;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;
import MainHandlers.PrintHandler;
import MainHandlers.ProcessHandler;
import MainHandlers.RegistryHandler;

public class PeerSoftware {

	private final boolean DEBUG = false;

	/* System Handlers */
	private RegistryHandler registry_handler;
	private NetworkHandler network_handler;
	private PrintHandler print_handler;
	private PeerCommHandler peer_comm_handler;
	/* Data */
	private final SourceList all_sources = new SourceList();

	/*
	 * Function that initiates all threads
	 */
	public PeerSoftware() {
		// this.settings = settings;

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
			this.network_handler = new NetworkHandler(this);
			this.peer_comm_handler = new PeerCommHandler(network_handler, all_sources);
			this.registry_handler = new RegistryHandler(this, peer_comm_handler);
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
			this.registry_handler.start(Settings.CLIENT_PORT);
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

	// public Settings getSettings() {
	// return this.settings;
	// }

	public NetworkHandler getNetworkHandler() {
		return this.network_handler;
	}

	public PrintHandler getPrintHandler() {
		return this.print_handler;
	}

	public PeerCommHandler getPeerCommHandler() {
		return this.peer_comm_handler;
	}

	/* Defines the settings of the app. Allows usage globally */
	public static class Settings {
		public final static String REGISTRY_IP = "127.0.0.1";
		public final static int REGISTRY_PORT = 55921;

		public final static String TEAM_NAME = "TylerChen";
		public final static boolean RUNNING_ON_LAN = false;
		public final static int CLIENT_PORT = 30001;

		private Settings() {
		}
	}
}

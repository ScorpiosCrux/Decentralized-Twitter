package Main;

/* Imports */
import java.io.IOException;
import java.util.Vector;

import Handlers.NetworkHandler;
import Handlers.PeerCommHandler;
import Handlers.PrintHandler;
import Handlers.ProcessHandler;
import Handlers.Registry.RegistryHandler;
import Host.HostMap;
import Main.HelperDataClasses.MessageLogs;
import Main.HelperDataClasses.SnippetLog;
import Main.Threads.GroupManager;
import Main.Threads.MessageReceiver;
import Main.Threads.MessageSender;

public class PeerSoftware {

	private GroupManager groupManager;
	private MessageReceiver messageReceiver;
	private MessageSender messageSender;

	/* System Handlers */
	private RegistryHandler registry_handler;
	public NetworkHandler network_handler;
	private PrintHandler print_handler;
	private PeerCommHandler peer_comm_handler;

	/* Data */
	// public final SourceList sourceList = new SourceList(); //
	public final HostMap hostMap = new HostMap();
	private final Vector<SnippetLog> all_snippets = new Vector<SnippetLog>(); // Used in SnippetHandler.java
	private final MessageLogs sent_logs;
	private final MessageLogs received_logs;

	/* Shared Data */
	public String externalIP;

	/*
	 * Function that initiates all threads
	 */
	public PeerSoftware() {

		/* Initialize Handlers */
		initializeHandlers();
		ProcessHandler.pause(1);

		
		this.sent_logs = new MessageLogs(network_handler.getExternalIP(), Settings.CLIENT_PORT);
		this.received_logs = new MessageLogs(network_handler.getExternalIP(), Settings.CLIENT_PORT);

		/* Initialize Registry Communication */
		initializeRegistryCommunication();
		ProcessHandler.pause(1);

		initializePeerCommunication();
		startPeerCommunication();

		finishRegistryCommunication();
	}

	/* Initializes Handlers for this peer */
	private void initializeHandlers() {
		System.out.println("SYSTEM: INITIALIZING HANDLERS");
		try {
			this.print_handler = new PrintHandler();
			this.network_handler = new NetworkHandler(this);
			// this.peer_comm_handler = new PeerCommHandler(network_handler, sourceList);
			this.registry_handler = new RegistryHandler(this);
			System.out.println("SYSTEM: FINISHED INITIALIZING HANDLERS");

		} catch (IOException e) {
			if (Settings.DEBUG)
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
			if (Settings.DEBUG)
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

	private void initializePeerCommunication() {
		this.groupManager = new GroupManager(this);
		this.messageReceiver = new MessageReceiver(this);
		this.messageSender = new MessageSender(this);
	}

	public void startPeerCommunication() {
		// communication with the peer
		// peer_comm_handler.start();
		this.groupManager.start();
		this.messageReceiver.start();
		this.messageSender.start();
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

		public final static boolean DEBUG = true;

		public final static String REGISTRY_IP = "127.0.0.1";
		public final static int REGISTRY_PORT = 55921;

		public final static String TEAM_NAME = "TylerChen";
		public final static boolean RUNNING_ON_LAN = true;
		public final static int CLIENT_PORT = 30001;

		/*
		 * These two times should be multiples of each other.
		 * Broadcast should also be shorter than inactivity.
		 */
		public final static int MAX_INACTIVITY_SECONDS = 60;
		public final static int BROADCAST_INTERVALS_SECONDS = 5;
		public final static int INPUT_CHECK_FREQUENCY_MILLISECONDS = 500;

		private Settings() {
		}
	}
}

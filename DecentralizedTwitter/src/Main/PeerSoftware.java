package Main;

/* Imports */
import java.io.IOException;
import Handlers.NetworkHandler;
import Handlers.ProcessHandler;
import Handlers.Registry.RegistryHandler;
import Host.HostMap;
import Main.Threads.GroupManager;
import Main.Threads.MessageReceiver;
import Main.Threads.MessageSender;

public class PeerSoftware {

	private GroupManager groupManager;
	private MessageReceiver messageReceiver;
	private MessageSender messageSender;

	/* System Handlers */
	private RegistryHandler registryHandler;
	public NetworkHandler networkHandler;

	/* Data */
	// public final SourceList sourceList = new SourceList(); //
	public final HostMap hostMap = new HostMap();

	/* Shared Data */
	public String externalIP;

	/*
	 * Function that initiates all threads
	 */
	public PeerSoftware() {

		initializeHandlers();
		initializeRegistryCommunication();
		initializePeerCommunication();

		startPeerCommunication();
	}

	/*
	 * Initializes handlers for this client
	 * 
	 */
	private void initializeHandlers() {
		System.out.println("SYSTEM: INITIALIZING HANDLERS");
		try {
			this.networkHandler = new NetworkHandler(this);
			// this.peer_comm_handler = new PeerCommHandler(network_handler, sourceList);
			this.registryHandler = new RegistryHandler(this);
			System.out.println("SYSTEM: FINISHED INITIALIZING HANDLERS");
			ProcessHandler.pause(1);

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
	 * 
	 */
	private void initializeRegistryCommunication() {
		try {
			this.registryHandler.start(Settings.CLIENT_PORT);
			ProcessHandler.pause(1);
		} catch (Exception e) {
			if (Settings.DEBUG)
				e.printStackTrace();
			else
				System.out.println("SYSTEM: FAILED TO CONNECT TO REGISTRY");
			System.exit(0);
		}
	}

	/*
	 * Initializes peer communication
	 * 
	 */
	private void initializePeerCommunication() {
		this.groupManager = new GroupManager(this);
		this.messageReceiver = new MessageReceiver(this);
		this.messageSender = new MessageSender(this);
	}

	/*
	 * Begins peer communication threads
	 * 
	 */
	private void startPeerCommunication() {
		this.groupManager.start();
		this.messageReceiver.start();
		this.messageSender.start();
	}

	public void stopPeerCommunication() {
		this.groupManager.setThreadStop();
		this.messageReceiver.setThreadStop();
		this.messageSender.setThreadStop();
	}

	/* Defines the settings of the app. Allows usage globally */
	public static class Settings {

		public final static boolean DEBUG = false;

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

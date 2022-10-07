package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import MainHandlers.MessageHandler;
import MainHandlers.NetworkHandler;
import Settings.UserSettings;
import Testing.PrintHandler;

public class Iteration3Solution {

	// Handlers
	private NetworkHandler network_handler;
	private PrintHandler print_handler;
	private MessageHandler message_handler;

	private UserSettings settings;
	private boolean registry_connected;

	private Source registry;

	private Hashtable<Source, Vector<Peer>> listOfSources = new Hashtable<Source, Vector<Peer>>();
	private Vector<UDPMessageLog> peersSent = new Vector<UDPMessageLog>();
	private Vector<UDPMessageLog> peersReceived = new Vector<UDPMessageLog>();
	private Vector<SnippetLog> allSnippets = new Vector<SnippetLog>();

	private DatagramSocket peer_socket;
	private String externalIP;
	private int port;

	public Iteration3Solution(UserSettings settings) {
		this.settings = settings;
		this.network_handler = new NetworkHandler();
		this.print_handler = new PrintHandler();
		this.message_handler = new MessageHandler();
	}


	
	public UserSettings getSettings() {
		return this.settings;
	}

	public NetworkHandler getNetworkHandler() {
		return this.network_handler;
	}

	public Vector<UDPMessageLog> getPeersSent() {
		return this.peersSent;
	}

	public Vector<UDPMessageLog> getPeersReceived() {
		return this.peersReceived;
	}

	public Vector<SnippetLog> getAllSnippets() {
		return this.allSnippets;
	}

	public DatagramSocket getOutgoingUDP() {
		return this.peer_socket;
	}

	public Hashtable<Source, Vector<Peer>> getAllSources() {
		return this.listOfSources;
	}

	void peerCommunication() {
		// create objects for group management and snippetHandler, these are in their
		// own threads
		GroupManagement gm = new GroupManagement("Group Management", this.peer_socket, this.externalIP,
				this.port, this.listOfSources, this.peersSent);
		gm.start();

		SnippetHandler sh = new SnippetHandler("SnippetHandler", this.listOfSources, this.peer_socket, this.externalIP,
				this.port, this.allSnippets);
		sh.start();

		// infinite loop until a stop has been received. this is the thread for
		// receiving UDP messages
		boolean stop = false;
		while (true) {
			UDPMessage message = receiveUDPMsg();
			String msgType = "";
			// System.out.println("Received message: " + message.message);

			try {
				msgType = message.message.substring(0, 4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			switch (msgType) {
				case "peer":
					HandlePeerUpdate pu = new HandlePeerUpdate("PeerUpdate", this.listOfSources, message, this.registry,
							this.peersReceived);
					pu.start();
					break;
				case "snip":
					// System.out.println("\n\n" + message.message + "\n\n");
					sh.handleIncomingSnip(message.message, message.sourcePeer);
					break;
				case "stop":
					sendUDPStopMessage(message.sourcePeer);
					stop = true;
					gm.setStop();
					sh.setStop();
					System.out.println("\n\nstop");
					break;
			}

			if (stop == true)
				break;

			// for (Map.Entry<Source, Vector<Peer>> s : this.listOfSources.entrySet())
			// System.out.println("Looped. Number of peers: " + s.getValue().size());
		}
	}

	//////////////////////////// SOCKET FUNCTIONS
	//////////////////////////// //////////////////////////////////////

	///////////////////////// RESPONSE/RECEIVE FUNCTIONS
	///////////////////////// //////////////////////////////

	// Read the message in the socket, if the message is receive peers, we want to
	// keep reading
	// Returns a string that contains the message
	private String readSocket(BufferedReader reader, Socket socket) throws IOException {
		String message = reader.readLine();
		if (message == null) {
			System.out.println("SOCKET RECEIVED: NULL");
			return "";
		} else if (message.equals("receive peers")) {
			receivePeers(socket, reader);
		}
		return message;
	}

	// Adds the source and peers to listOfSources reading from the reader.
	private void receivePeers(Socket socket, BufferedReader reader) throws IOException {

		// Check to see if the additional source is already in the list.
		// Source source = new Source(new Peer(getIP(socket), socket.getPort(), null));
		if (!listOfSources.contains(registry)) {
			this.listOfSources.put(registry, new Vector<Peer>());
		}

		String numOfPeersString = reader.readLine();
		try {
			int numOfPeers = Integer.parseInt(numOfPeersString);
			Vector<Peer> currentList = this.listOfSources.get(registry);
			for (int i = 0; i < numOfPeers; i++) {
				String peer = reader.readLine();
				String[] parts = peer.split(":");
				Peer newPeer = new Peer(parts[0], Integer.parseInt(parts[1]), registry.getPeer().toString());
				currentList.add(newPeer);
			}
			this.listOfSources.put(registry, currentList);
		} catch (Exception e) {
			System.out.println("Error in receiving peers!");
		}
	}

	// Received UDP messages and returns a UDPMessage with information about the
	// source and the content itself
	private UDPMessage receiveUDPMsg() {
		// System.out.println("Waiting for UDP message");
		try {
			DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
			peer_socket.receive(packet);
			Peer sourcePeer = new Peer(packet.getAddress().getHostAddress(), packet.getPort(), null);
			String message = new String(packet.getData(), 0, packet.getLength());
			return new UDPMessage(message, sourcePeer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	///////////////////////// RESPONSE/RECEIVE FUNCTIONS
	///////////////////////// //////////////////////////////

	public void start(int port) throws IOException {
		Socket registry_socket = network_handler.createSocket(this.registry.getPeer().getIP(),
				this.registry.getPeer().getPort());
		BufferedReader reader = new BufferedReader(new InputStreamReader(registry_socket.getInputStream()));

		this.peer_socket = network_handler.createUDPSocket(this.port);
		this.externalIP = network_handler.getExternalIP();

		if (registry_socket.isConnected())
			this.registry_connected = true;

		while (registry_socket.isConnected()) {
			String request = readSocket(reader, registry_socket); // Read request
			String returnMessage = message_handler.handleRequest(this, registry_socket, request); // Handle request
			if (returnMessage.equals("-1")) {
				print_handler.printError("Error 0 returned from handleRequest");
				break;
			} else if (returnMessage.equals("1"))
				break;
			network_handler.writeSocket(registry_socket, returnMessage); // Send response
			print_handler.printResponse(returnMessage, request);
		}
		registry_socket.close();
	}

	public void stop() throws IOException {
		Socket registry_socket = network_handler.createSocket(this.registry.getPeer().getIP(),
				this.registry.getPeer().getPort());
		BufferedReader reader = new BufferedReader(new InputStreamReader(registry_socket.getInputStream()));

		while (registry_socket.isConnected()) {
			String request = readSocket(reader, registry_socket); // Read request
			String returnMessage = message_handler.handleRequest(this, registry_socket, request); // Handle request
			if (returnMessage.equals("-1")) {
				print_handler.printError("Error 0 returned from handleRequest");
				break;
			} else if (returnMessage.equals("1"))
				break;
			network_handler.writeSocket(registry_socket, returnMessage); // Send response
			print_handler.printResponse(returnMessage, request);
		}
		registry_socket.close();
	}

	private void sendUDPStopMessage(Peer peer) {
		try {
			String ip = peer.getIP();
			int port = peer.getPort();

			byte[] buffer = new byte[1024];
			InetAddress address = InetAddress.getByName(ip);
			System.out.println("\n\n\n\n\nAddress that sent stop: " + address.toString());

			String data = "ack" + settings.team_name;
			buffer = data.getBytes();
			DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);

			peersSent.add(new UDPMessageLog(peer, new Peer(this.externalIP, this.port, null), null));
			peer_socket.send(response);
			// System.out.println("Broadcast to: " + peer.toString());

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to sendUDPMessage (GroupManagment): " + peer.toString());
		}

	}

	public boolean isRegistryConnected() {
		return registry_connected;
	}

	public void setRegistry(Source registry) {
		this.registry = registry;
	}

	public Source getRegistry() {
		return this.registry;
	}

}

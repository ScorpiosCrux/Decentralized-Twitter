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
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import Settings.UserSettings;

public class Iteration3Solution {

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

	public void setRegistry(Source registry) {
		this.registry = registry;
	}

	public Source getRegistry() {
		return this.registry;
	}

	//////////////////////////// SOCKET FUNCTIONS
	//////////////////////////// //////////////////////////////////////

	// Creates a socket with ip and port.
	// Returns the socket
	private Socket createSocket(String ip, int port) throws UnknownHostException, IOException {
		Socket socket = new Socket(ip, port);
		return socket;
	}

	// Closes the socket
	private void closeSocket(Socket socket) throws IOException {
		socket.close();
	}

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

	// Gets the IP address from the socket
	private String getIP(Socket socket) {
		return socket.getInetAddress().getHostAddress();
	}

	// -1 to let the OS choose a port
	private DatagramSocket createUDPSocket(int port) throws SocketException {
		if (port == -1)
			return new DatagramSocket();
		else
			return new DatagramSocket(port);
	}

	//////////////////////////// SOCKET FUNCTIONS
	//////////////////////////// //////////////////////////////////////

	///////////////////////// RESPONSE/RECEIVE FUNCTIONS
	///////////////////////// //////////////////////////////

	// Stolen from https://www.w3schools.com/java/java_files_read.asp
	private String readCode(String path) {
		String code = "";

		try {
			File myObj = new File(path);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				code += data + "\n";
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return code;
	}

	// Assuming that src code is less than 2 million chars.
	private String genSrcCodeRes() {
		String sourceCode = "";

		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Iteration2Solution.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Peer.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Source.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/GroupManagement.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/HandlePeerUpdate.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/ReturnSearch.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/SnippetHandler.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/SnippetLog.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/UDPMessage.java");
		sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/UDPMessageLog.java");

		return sourceCode;
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
				Peer newPeer = new Peer(parts[0], Integer.parseInt(parts[1]), registry.peer.toString());
				currentList.add(newPeer);
			}
			this.listOfSources.put(registry, currentList);
		} catch (Exception e) {
			System.out.println("Error in receiving peers!");
		}
	}

	// Generates report based on assignment specs
	private String generateReport() {
		int numOfSources = listOfSources.size();
		int totalNumOfPeers = 0;
		String peer_list_sources = "";
		String peer_list = "";
		String peers_recd = "";
		String peers_sent = "";
		String snip_list = "";

		for (Map.Entry<Source, Vector<Peer>> e : listOfSources.entrySet()) {
			Source source = e.getKey();
			String sourceLocation = source.peer.toString() + "\n";
			Vector<Peer> listOfPeers = e.getValue();
			String peers = "";
			for (Peer p : listOfPeers) {
				String peer_string = p.toString() + "\n";
				peers += peer_string;
				peer_list += peer_string;
				totalNumOfPeers++;
			}
			// adds one source
			peer_list_sources += sourceLocation + source.time + "\n" + listOfPeers.size() + "\n" + peers;
		}

		peers_recd += this.peersReceived.size() + "\n";
		for (UDPMessageLog m : this.peersReceived) {
			try {
				peers_recd += m.msgOrigin.toString() + " " + m.transmittedPeer.toString() + " " + m.timeStamp + "\n";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		peers_sent += this.peersSent.size() + "\n";
		for (UDPMessageLog m : this.peersSent) {
			try {
				peers_sent += m.msgOrigin.toString() + " " + m.transmittedPeer.toString() + " " + m.timeStamp + "\n";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		snip_list += this.allSnippets.size() + "\n";
		for (SnippetLog sl : this.allSnippets) {
			snip_list += sl.timeStamp + " " + sl.content + " " + sl.sourcePeer.toString() + "\n";
		}

		return totalNumOfPeers + "\n" + peer_list + numOfSources + "\n" +
				peer_list_sources + peers_recd + peers_sent + snip_list;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	// Gets the externalIP for broadcasting
	private String getExternalIP() throws IOException {
		// https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} catch (MalformedURLException e) {
			System.out.println("Error getting External IP!");
			e.printStackTrace();
			return "Error!";
		}
	}

	// Writes to socket with message then flushes the message to the stream.
	private void writeSocket(Socket socket, String message) throws IOException {
		// TODO: Convert message to Unicode characters
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		writer.write(message);
		writer.flush();
	}

	// This function handles the requests from the registry and returns a response.
	// Other code: -1=error; 0=no response; 1=connection closed, exit main loop
	private String handleRequest(Socket socket, String request) throws IOException {
		switch (request) {
			case "get team name":
				return settings.team_name + "\n";
			case "get code":
				String language = "Java";
				String newline = "\n";
				String code = genSrcCodeRes();
				String end_of_code = "...";

				return language + newline + code + newline + end_of_code + newline;
			case "receive peers":
				return "0";
			case "get report":
				return generateReport();
			case "get location":
				if (settings.running_on_lan == true) {
					return "127.0.0.1" + ":" + this.peer_socket.getLocalPort() + "\n";
				} else {
					// https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
					URL whatismyip = new URL("http://checkip.amazonaws.com");
					BufferedReader in = new BufferedReader(new InputStreamReader(
							whatismyip.openStream()));

					String ip = in.readLine();
					return ip + ":" + this.peer_socket.getLocalPort() + "\n";
				}
			case "close":
				closeSocket(socket);
				return "1";
		}
		return "-1";
	}

	///////////////////////// RESPONSE/RECEIVE FUNCTIONS
	///////////////////////// //////////////////////////////

	///////////////////////////// QOS FUNCTIONS
	///////////////////////////// ///////////////////////////////////////
	private void printResponse(String message, String request) {
		System.out.println("\nRequest: " + request);
		System.out.println("\t------BEGIN------");
		System.out.print(message);
		System.out.println("\t-------END-------\n");
	}

	private void printError(String message) {
		System.out.println("\n\t------ERROR------");
		System.out.print(message);
		System.out.println("\n\t------ERROR------");
	}
	///////////////////////////// QOS FUNCTIONS
	///////////////////////////// ///////////////////////////////////////

	public void start(int port) throws IOException {
		Socket registry_socket = createSocket(this.registry.peer.getIP(), this.registry.peer.getPort());
		BufferedReader reader = new BufferedReader(new InputStreamReader(registry_socket.getInputStream()));

		this.peer_socket = createUDPSocket(this.port);
		this.externalIP = getExternalIP();
		this.port = peer_socket.getLocalPort();

		if (registry_socket.isConnected())
			this.registry_connected = true;

		while (registry_socket.isConnected()) {
			String request = readSocket(reader, registry_socket); // Read request
			String returnMessage = handleRequest(registry_socket, request); // Handle request
			if (returnMessage.equals("-1")) {
				printError("Error 0 returned from handleRequest");
				break;
			} else if (returnMessage.equals("1"))
				break;
			writeSocket(registry_socket, returnMessage); // Send response
			printResponse(returnMessage, request);
		}

		registry_socket.close();

	}

	public void stop() throws IOException {
		Socket registry_socket = createSocket(this.registry.peer.getIP(), this.registry.peer.getPort());
		BufferedReader reader = new BufferedReader(new InputStreamReader(registry_socket.getInputStream()));

		while (registry_socket.isConnected()) {
			String request = readSocket(reader, registry_socket); // Read request
			String returnMessage = handleRequest(registry_socket, request); // Handle request
			if (returnMessage.equals("-1")) {
				printError("Error 0 returned from handleRequest");
				break;
			} else if (returnMessage.equals("1"))
				break;
			writeSocket(registry_socket, returnMessage); // Send response
			printResponse(returnMessage, request);
		}

		registry_socket.close();

	}

	public boolean isRegistryConnected(){
		return registry_connected;
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

}

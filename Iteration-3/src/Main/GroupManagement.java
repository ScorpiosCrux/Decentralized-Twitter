package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.UDPMessageLog;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;
import Settings.UserSettings;

//This class is for group management:
//	-broadcasting
//	-sets inactive peers
//Inspired by: https://www.tutorialspoint.com/java/java_multithreading.htm for threading

public class GroupManagement extends Thread {

	UserSettings settings;
	NetworkHandler network_handler;
	PeerCommHandler parent;

	private Thread t;
	private String threadName;

	private DatagramSocket outgoingSocket;
	private String ip;
	private int port;
	private Hashtable<Source, Vector<Peer>> listOfSources = new Hashtable<Source, Vector<Peer>>();
	private boolean stop;
	private Vector<UDPMessageLog> peersSent = new Vector<UDPMessageLog>();

	public GroupManagement(UserSettings settings, NetworkHandler network_handler, PeerCommHandler parent) {
		this.settings = settings;
		this.network_handler = network_handler;
		this.parent = parent;

		this.threadName = "Group Management";
		this.outgoingSocket = network_handler.getOutGoingUDP();
		this.ip = network_handler.getExternalIP();
		this.port = settings.client_port;
		this.listOfSources = parent.getAllSources();
		this.stop = false;
		System.out.println("Group Management Thread Created!");
	}

	public void run() {
		// inactivity max should be double of broadcast_intervals in SECONDS
		int inactivity_max = 60;
		int broadcast_intervals = 5;

		boolean checkInactivePeers = false;
		while (stop != true) {

			try {
				broadcast();
				if (checkInactivePeers == true) {
					checkActivity(inactivity_max);
					checkInactivePeers = false;
				} else
					checkInactivePeers = !checkInactivePeers;
				Thread.sleep(broadcast_intervals * 1000);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in GroupManagment!");
			}

		}
		System.out.println("Thread " + threadName + " exiting");
	}

	public void start() {
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}

	public void setStop() {
		this.stop = true;
	}

	// This function checks the activity and updates the peer to active/inactive
	// Returns:
	// true - active
	// false - inactive
	private void checkActivity(int inactivity_max) {

		for (Map.Entry<Source, Vector<Peer>> s : listOfSources.entrySet()) {
			Vector<Peer> listOfPeers = s.getValue();
			for (Peer peer : s.getValue()) {

				// Inspired by:
				// https://stackoverflow.com/questions/4927856/how-can-i-calculate-a-time-difference-in-java
				Instant start = peer.getInstant();
				Instant end = Instant.now();
				Duration timeElapsed = Duration.between(start, end);

				// if peer is active and timeElapsed > x
				if (timeElapsed.toSeconds() > inactivity_max && peer.isActive()) {
					peer.setActivity(false);
					// System.out.println("Peer has been set to false! Info: " + peer.toString());

				} else if (timeElapsed.toSeconds() < inactivity_max && !peer.isActive()) { // peer comes back alive
					peer.setActivity(true);
					// System.out.println("Peer has been set to true! Info: " + peer.toString());
				}
			}
		}

	}

	// Function for sending a UDPMessage via the socket we created in main and sends
	// a "peer" message to the peer param
	private void sendUDPMessage(Peer peer) {
		try {
			String ip = peer.getIP();
			int port = peer.getPort();

			byte[] buffer = new byte[1024];
			InetAddress address = InetAddress.getByName(ip);

			String data = "peer" + this.ip + ":" + this.port;
			buffer = data.getBytes();
			DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);

			peersSent.add(new UDPMessageLog(peer, new Peer(this.ip, this.port, null), null));
			outgoingSocket.send(response);
			// System.out.println("Broadcast to: " + peer.toString());

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to sendUDPMessage (GroupManagment): " + peer.toString());
		}

	}

	// Function that broadcasts to all active peers that we know about
	private void broadcast() {
		Peer ourselves = new Peer(ip, port, null);

		for (Map.Entry<Source, Vector<Peer>> s : listOfSources.entrySet()) {
			Vector<Peer> listOfPeers = s.getValue();
			for (int i = 0; i < listOfPeers.size(); i++) {
				Peer peer = listOfPeers.get(i);
				if (peer.isActive() && !peer.equals(ourselves))
					sendUDPMessage(peer);
			}
		}
	}

	public Vector<UDPMessageLog> getSendLogs(){
		return this.peersSent;
	}

}

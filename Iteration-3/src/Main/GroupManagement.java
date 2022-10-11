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

import Main.HelperDataClasses.MessageLogs;
import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.PeerOld;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.SourceList;
import Main.HelperDataClasses.SourceOld;
import Main.HelperDataClasses.UDPMessageLog;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;
import Settings.UserSettings;

//This class is for group management:
//	-broadcasting
//	-sets inactive peers
//Inspired by: https://www.tutorialspoint.com/java/java_multithreading.htm for threading

public class GroupManagement extends Thread {

	private UserSettings settings;
	private NetworkHandler network_handler;
	private PeerCommHandler parent;

	private SourceList all_sources;
    private Vector<UDPMessageLog> peers_sent;
	private MessageLogs sent_logs;
	
	private Thread t;
	private String threadName;

	private DatagramSocket outgoingSocket;
	private String ip;
	private int port;
	private boolean stop;

	public GroupManagement(UserSettings settings, NetworkHandler network_handler, PeerCommHandler parent) {
		this.settings = settings;
		this.network_handler = network_handler;
		this.parent = parent;

		this.all_sources = parent.getAllSources();
		this.peers_sent = parent.getAllPeersSent();
		this.sent_logs = parent.getSentLogs();

		this.threadName = "Group Management";
		this.outgoingSocket = network_handler.getOutGoingUDP();
		this.ip = network_handler.getExternalIP();
		this.port = settings.client_port;
		//this.all_sources = parent.getAllSources();
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
				System.out.println("Error in Group Managment!");
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

		for (Map.Entry<SourceOld, Vector<PeerOld>> s : all_sources.entrySet()) {
			Vector<PeerOld> listOfPeers = s.getValue();
			for (PeerOld peer : s.getValue()) {

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
	private void sendUDPMessage(String dest_ip, int dest_port) {
		try {
			byte[] buffer = new byte[1024];
			InetAddress address = InetAddress.getByName(dest_ip);

			String data = "peer" + this.ip + ":" + this.port;
			buffer = data.getBytes();
			DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);

			peers_sent.add(new UDPMessageLog(peer, new PeerOld(this.ip, this.port, null), null));

			outgoingSocket.send(response);
			// System.out.println("Broadcast to: " + peer.toString());

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to sendUDPMessage (GroupManagment): " + peer.toString());
		}

	}

	// Function that broadcasts to all active peers that we know about
	private void broadcast() {
		Vector<Source> source_list = all_sources.getSources();
		for (Source s : source_list){
			Vector<Peer> active_peers = s.getActivePeers();
			for (Peer p : active_peers){
				sendUDPMessage(p.getIP(), p.getPort());
			}
		}
	}

	public Vector<UDPMessageLog> getSendLogs(){
		return this.peers_sent;
	}

}

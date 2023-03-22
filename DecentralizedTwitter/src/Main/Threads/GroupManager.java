package Main.Threads;
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
import java.util.Vector;

import Main.PeerSoftware;
import Main.HelperDataClasses.MessageLogs;
import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.SourceList;
import Main.PeerSoftware.Settings;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;

//This class is for group management:
//	-broadcasting
//	-sets inactive peers

/*
* This threaded class manages checking inactivity and sending updates to peers
* that we're still active.
* Source: https://www.tutorialspoint.com/java/java_multithreading.htm
*/
public class GroupManager extends Thread {

	private PeerSoftware ps;

	/* Thread Setup */
	private Thread t;
	private String threadName;

	private SourceList all_sources;
	private MessageLogs sent_logs;

	private DatagramSocket outgoingSocket;
	private String ip;
	private int port;
	private boolean stop;

	/* Constructor */
	public GroupManager(PeerSoftware ps, NetworkHandler network_handler, PeerCommHandler parent) {
		this.threadName = "-- Group Manager";
		this.ps = ps;

		this.all_sources = parent.getAllSources();
		this.sent_logs = parent.getSentLogs();

		this.outgoingSocket = network_handler.getOutgoingUDPSocket();
		this.ip = network_handler.getExternalIP();
		this.port = Settings.CLIENT_PORT;
		this.stop = false;

		System.out.println("SYSTEM: Creating " + threadName + " thread");
	}

	public void start() {
		System.out.println("SYSTEM: Starting " + threadName + " thread");
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}

	@Override
	public void run() {
		System.out.println("SYSTEM: Running " + threadName + " thread");

		// inactivity max should be double of broadcast_intervals in SECONDS
		int inactivity_max = 60;
		int broadcast_intervals = 5;

		boolean checkInactivePeers = false;
		while (stop != true) {

			try {
				broadcast();
				if (checkInactivePeers == true) {
					all_sources.checkActivity(inactivity_max);
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

	public void setStop() {
		this.stop = true;
	}

	// Function for sending a UDPMessage via the socket we created in main and sends
	// a "peer" message to the peer param
	private void sendUDPMessage(String dest_ip, int dest_port) {
		try {
			String data = "peer" + this.ip + ":" + this.port;

			byte[] buffer = new byte[1024];
			buffer = data.getBytes();
			InetAddress address = InetAddress.getByName(dest_ip);
			DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);

			sent_logs.addLog(dest_ip, dest_port);
			outgoingSocket.send(response);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to sendUDPMessage (GroupManagment): ");
		}

	}

	// Function that broadcasts to all active peers that we know about
	private void broadcast() {
		Vector<Source> source_list = all_sources.getSources();
		for (Source s : source_list) {
			Vector<Peer> active_peers = s.getActivePeers();
			for (Peer p : active_peers) {
				sendUDPMessage(p.getIP(), p.getPort());
			}
		}
	}

}

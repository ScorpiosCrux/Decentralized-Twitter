package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

//This class handles incoming UDP Message with "peer" at the start
//

import java.time.Instant;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import Main.HelperDataClasses.DataValidator;
import Main.HelperDataClasses.PeerOld;
import Main.HelperDataClasses.ReturnSearch;
import Main.HelperDataClasses.SourceList;
import Main.HelperDataClasses.SourceOld;
import Main.HelperDataClasses.UDPMessage;
import Main.HelperDataClasses.UDPMessageLog;
import MainHandlers.PeerCommHandler;
import Settings.UserSettings;

public class HandlePeerUpdate extends Thread {
	private UDPMessage message_raw;
	private SourceList all_sources;
	private Vector<UDPMessageLog> peers_received;

	private Thread t;
	private String threadName;

	public HandlePeerUpdate(UserSettings settings, UDPMessage message_raw, PeerCommHandler parent) {
		this.message_raw = message_raw;

		this.all_sources = parent.getAllSources();
		this.peers_received = parent.getAllPeersRec();

		this.threadName = "Peer Update Handler";
	}

	// run the thread
	public void run() {
		// TODO: Create Peer
		String peer_ip = parsePeerIP(message_raw);
		int peer_port = parsePeerPort(message_raw);
		all_sources.addPeer(message_raw.getSourcePeer().getIP(), message_raw.getSourcePeer().getPort(), peer_ip,
				peer_port);

		this.peers_received.add(new UDPMessageLog(message_raw.getSourcePeer(), peer, null));

	}

	public String parsePeerIP(UDPMessage message_raw) {
		String message = message_raw.getMessage();
		String ip_port = message.substring(4, message.length());
		String[] ip_port_lst = ip_port.split(":");
		String ip_raw = ip_port_lst[0].trim();
		boolean valid_ip = DataValidator.isValidIP(ip_raw);

		if (valid_ip)
			return ip_raw;
		else
			return null;
	}

	public Integer parsePeerPort(UDPMessage message_raw) {
		String message = message_raw.getMessage();
		String ip_port = message.substring(4, message.length());
		String[] ip_port_lst = ip_port.split(":");
		String port_raw = ip_port_lst[1].trim();
		boolean valid_port = DataValidator.isValidPort(port_raw);

		if (valid_port)
			return Integer.parseInt(port_raw);
		else
			return null;

	}

	// Start the thread
	public void start() {
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}

}

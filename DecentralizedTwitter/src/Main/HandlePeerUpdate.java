package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

//This class handles incoming UDP Message with "peer" at the start
//

import Main.HelperDataClasses.DataValidator;
import Main.HelperDataClasses.MessageLogs;
import Main.HelperDataClasses.SourceList;
import Main.HelperDataClasses.UDPMessagePack;
import Main.PeerSoftware.Settings;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;

public class HandlePeerUpdate extends Thread {

	private NetworkHandler network_handler;

	private UDPMessagePack message_raw;
	private SourceList all_sources;
	private MessageLogs received_logs;

	private Thread t;
	private String threadName;

	public HandlePeerUpdate(PeerCommHandler parent, UDPMessagePack message_raw) {

		this.network_handler = parent.getNetworkHandler();

		this.message_raw = message_raw;

		this.all_sources = parent.getAllSources();
		this.received_logs = parent.getReceivedLogs();

		this.threadName = "Peer Update Handler";
	}

	// run the thread
	public void run() {
		String peer_ip = parsePeerIP(message_raw);
		int peer_port = parsePeerPort(message_raw);
		if (peer_ip != network_handler.getExternalIP() && peer_port != Settings.CLIENT_PORT) {
			all_sources.addPeer(message_raw.getSourcePeer().getIP(), message_raw.getSourcePeer().getPort(), peer_ip,
					peer_port);
			this.received_logs.addLog(message_raw.getSourcePeer().getIP(), message_raw.getSourcePeer().getPort(),
					peer_ip,
					peer_port);
		}

	}

	public String parsePeerIP(UDPMessagePack message_raw) {
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

	public Integer parsePeerPort(UDPMessagePack message_raw) {
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

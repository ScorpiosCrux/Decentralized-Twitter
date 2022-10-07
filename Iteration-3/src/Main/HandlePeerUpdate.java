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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Main.HelperDataClasses.PeerOld;
import Main.HelperDataClasses.ReturnSearch;
import Main.HelperDataClasses.SourceOld;
import Main.HelperDataClasses.UDPMessage;
import Main.HelperDataClasses.UDPMessageLog;
import MainHandlers.PeerCommHandler;
import Settings.UserSettings;

public class HandlePeerUpdate extends Thread {
	private PeerCommHandler parent;
	private UDPMessage message_pck;
	private Hashtable<SourceOld, Vector<PeerOld>> all_sources;
	private Vector<UDPMessageLog> peers_received;


	private Thread t;
	private String threadName;
	

	private SourceOld source;


	public HandlePeerUpdate(UserSettings settings, UDPMessage message, PeerCommHandler parent) {
		this.parent = parent;
		//this.all_sources = parent.getAllSources();
		this.peers_received = parent.getAllPeersRec();
	
		this.threadName = "Peer Update Handler";
		this.message_pck = message;
		this.source = new SourceOld(new PeerOld(settings.registry_ip, settings.registry_port, null));

	}

	// run the thread
	public void run() {
		PeerOld peer = createPeer();
		this.peers_received.add(new UDPMessageLog(message_pck.getSourcePeer(), peer, null));
		if (peer != null) {
			//this.all_sources = parent.getAllSources();
			updateAddPeer(peer, source);
		}

	}

	// Start the thread
	public void start() {
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}

	// Checks to see if the ip is a valid ip.
	// Inspired from:
	// https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
	private boolean isValidIP(String ip) {
		String IPV4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
		Pattern pattern = Pattern.compile(IPV4_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	// Check to see if the port is a valid port
	private boolean isValidPort(String port_in) {
		int port;
		try {
			port = Integer.parseInt(port_in);
			int length = port_in.length();
			if (port > 0 && length <= 5)
				return true;
			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Find a peer in the data structure for storing all peers and sources
	private ReturnSearch findPeer(PeerOld peer) {
		for (Map.Entry<SourceOld, Vector<PeerOld>> s : all_sources.entrySet()) {
			Vector<PeerOld> listOfPeers = s.getValue();
			for (int i = 0; i < listOfPeers.size(); i++)
				if (listOfPeers.get(i).equals(peer))
					return new ReturnSearch(s.getKey(), i);
		}
		return new ReturnSearch(null, -1);
	}

	// Either updates the time stamp for the peer or adds a new peer
	private void updateAddPeer(PeerOld peer, SourceOld sourcePeer) {
		ReturnSearch res = findPeer(peer); // check if the peer exists in the data structure
		// if peer exist in data structure:
		if (res.getSource() != null && res.getIteration() != -1) {
			Vector<PeerOld> listOfPeers = all_sources.get(res.getSource());
			listOfPeers.get(res.getIteration()).setInstant(Instant.now());
		} else if (res.getSource() == null && res.getIteration() == -1) {
			Vector<PeerOld> listOfPeers = all_sources.get(sourcePeer); // all sources is nested. Create new vector and append.
			// Vector<Peer> listOfPeers = new Vector<Peer>(); // all sources is nested. Create new vector and append.
			listOfPeers.add(peer);
			this.all_sources.put(sourcePeer, listOfPeers);
			System.out.println("New Peer! Source and list of peers has been updated!");
		}
	}

	// Gets the ip and port then splits.
	// Trims the ip and port then checks if they're valid.
	// if: created successfully, return a peer.
	// else: return null
	private PeerOld createPeer() {
		PeerOld peer = null;
		try {
			String message = message_pck.getMessage();
			String ip_port = message.substring(4, message.length());
			String[] ip_port_lst = ip_port.split(":");
			String ip_raw = ip_port_lst[0].trim();
			String port_raw = ip_port_lst[1].trim();

			boolean validIp = isValidIP(ip_raw);
			boolean validPort = isValidPort(port_raw);

			if (validIp && validPort) {
				int port = Integer.parseInt(port_raw);
				peer = new PeerOld(ip_raw, port, message_pck.getSourcePeer().toString());
			}
		} catch (Exception e) {
			System.err.println("Unable to createPeer in HandlePeerUpdate.");
		}
		return peer;
	}

}

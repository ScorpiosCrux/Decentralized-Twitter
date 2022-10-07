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

import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.ReturnSearch;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.UDPMessage;
import Main.HelperDataClasses.UDPMessageLog;

public class HandlePeerUpdate extends Thread{
	private Thread t;
	private String threadName;
	private Hashtable<Source, Vector<Peer>> listOfSources = new Hashtable<Source, Vector<Peer>>();
	private Vector<UDPMessageLog> peersReceived = new Vector<UDPMessageLog>();
	private UDPMessage message_pck;
	private Source source;
	
	HandlePeerUpdate(String tName, Hashtable<Source, Vector<Peer>> listOfSources, UDPMessage message, Source source,
			Vector<UDPMessageLog> peersReceived){
		this.threadName = tName;
		this.listOfSources = listOfSources;
		this.message_pck = message;
		this.source = source;
		this.peersReceived = peersReceived;
	}
	
	//run the thread
	public void run() {
		
		Peer peer = createPeer();
		this.peersReceived.add(new UDPMessageLog(message_pck.sourcePeer, peer, null));
		if (peer != null)
			updateAddPeer(peer, source);
		
	}
	
	//Start the thread
	public void start() {
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}
	
	//Checks to see if the ip is a valid ip.
	//Inspired from: https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
	private boolean isValidIP(String ip) {
		String IPV4_PATTERN =
	            "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
		Pattern pattern = Pattern.compile(IPV4_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}
	
	//Check to see if the port is a valid port
	private boolean isValidPort(String port_in) {
		int port;
		try {
			port = Integer.parseInt(port_in);
			int length = port_in.length();
			if (port > 0 && length <= 5)
				return true;
			else
				return false;
			
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	//Find a peer in the data structure for storing all peers and sources
	private ReturnSearch findPeer(Peer peer){
		for (Map.Entry<Source, Vector<Peer>> s : listOfSources.entrySet()) {
			Vector<Peer> listOfPeers = s.getValue();
			for (int i = 0; i < listOfPeers.size(); i++) 
				if (listOfPeers.get(i).equals(peer)) 
					return new ReturnSearch(s.getKey(), i);
		}
		return new ReturnSearch(null, -1);
	}
	
	//Either updates the time stamp for the peer or adds a new peer
	private void updateAddPeer(Peer peer, Source sourcePeer) {
		ReturnSearch res = findPeer(peer);				//check if the peer exists in the data structure
		//if peer exist in data structure:
		if (res.source != null && res.iteration != -1) {
			Vector<Peer> listOfPeers = listOfSources.get(res.source);
			listOfPeers.get(res.iteration).setInstant(Instant.now());
		} else if (res.source == null && res.iteration == -1) {
			Vector<Peer> listOfPeers = listOfSources.get(sourcePeer);
			listOfPeers.add(peer);
			this.listOfSources.put(sourcePeer, listOfPeers);
		}
	}
	
	
	//Gets the ip and port then splits.
	//Trims the ip and port then checks if they're valid.
	//		if: created successfully, return a peer.
	//		else: return null
	private Peer createPeer() {
		Peer peer = null;
		try {
			String message = message_pck.message;
			String ip_port = message.substring(4, message.length());
			String[] ip_port_lst = ip_port.split(":");
			String ip_raw = ip_port_lst[0].trim();
			String port_raw = ip_port_lst[1].trim();
			
			
			boolean validIp = isValidIP(ip_raw);
			boolean validPort = isValidPort(port_raw);
			

			if (validIp && validPort) {
				int port = Integer.parseInt(port_raw);
				peer = new Peer(ip_raw, port, message_pck.sourcePeer.toString());
			}
		} catch (Exception e) {
			System.err.println("Unable to createPeer in HandlePeerUpdate.");
		}
		return peer;
	}

	
	
}

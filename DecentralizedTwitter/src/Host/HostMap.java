package Host;

import java.util.HashMap;
import java.util.Vector;

import Main.PeerSoftware.Settings;

public class HostMap {
	private HashMap<Host, Vector<Host>> map = new HashMap<Host, Vector<Host>>();

	// ============ BASIC FUNCTIONS ============

	/*
	 * Adds a new source value
	 * 
	 */
	public void addSource(String ip, int port) {
		Host host = new Host(ip, port);
		Vector<Host> list = new Vector<Host>();
		this.map.put(host, list);
	}

	/*
	 * Gets the hosts from a specified ip and port
	 * Returns: A list of hosts that were given by the specified params.
	 */
	public Vector<Host> getHosts(String ip, int port) {
		Host host = new Host(ip, port);
		return this.map.get(host);
	}

	/*
	 * Adds a new host to the source's list .
	 * 
	 */
	public void addHost(String srcIP, int srcPort, String newIP, int newPort) {
		Host srcHost = new Host(srcIP, srcPort);
		Host newHost = new Host(newIP, newPort);
		Vector<Host> hosts = this.getHosts(srcIP, srcPort);
		hosts.add(newHost);
		this.map.put(srcHost, hosts);
	}

	// ============ (end) BASIC FUNCTIONS ============

	// ============ PROJECT SPECIFIC FUNCTIONS ============
	
	/* 
	 * Gets all active hosts.
	 * 
	 */
	public Vector<Host> getActiveHosts() {
		int inactivity_max = Settings.MAX_INACTIVITY_SECONDS;
		Vector<Host> activeHosts = new Vector<Host>();

		for (Host source : this.map.keySet()) {
			Vector<Host> hosts = this.map.get(source);
			for (Host host : hosts) {
				host.checkActivity(inactivity_max);
				
				/* If host is active */
				if (host.getActivity()) {
					activeHosts.add(host);
				}
			}
		}

		return activeHosts;
	}
	// ============ (end) PROJECT SPECIFIC FUNCTIONS ============

}

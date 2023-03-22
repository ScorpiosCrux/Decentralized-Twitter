package Main.HelperDataClasses;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Main.PeerSoftware;

public class Peer {

	// This peer
	private String peer_ip;
	private int peer_port;

	private boolean active;
	private Instant lastCommunication;
	private String creationTime;
	private int timeStamp;

	public Peer(String ip, int port) {
		this.peer_ip = ip;
		this.peer_port = port;

		this.active = true;
		this.lastCommunication = Instant.now();
		this.timeStamp = 0;

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String creationTime = dtf.format(LocalDateTime.now());
		this.creationTime = creationTime;
	}

	public String toString() {
		return peer_ip + ":" + peer_port;
	}

	public String getIP() {
		return peer_ip;
	}

	public int getPort() {
		return peer_port;
	}

	public boolean isActive() {
		return active;
	}

	public void setActivity(boolean value) {
		this.active = value;
	}

	public void setInstant(Instant time) {
		this.lastCommunication = time;
	}

	public Instant getInstant() {
		return lastCommunication;
	}

	public synchronized int getTimeStamp() {
		return this.timeStamp;
	}

	public synchronized void setTimeStamp(int ts) {
		this.timeStamp = ts;
	}

	public synchronized void setMaxTimeStamp(int received_timestamp) {
		this.timeStamp = Math.max(this.timeStamp, received_timestamp);
	}

	public void incrementTimeStamp(){
		this.timeStamp += 1;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void checkActivity(int inactivity_max) {
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(lastCommunication, end);

		if (timeElapsed.toSeconds() > inactivity_max && active) {
			active = false;
			// System.out.println("Peer has been set to false! Info: " + peer.toString());

		} else if (timeElapsed.toSeconds() < inactivity_max && !active) { // peer comes back alive
			active = true;
			// System.out.println("Peer has been set to true! Info: " + peer.toString());
		}
	}

	public void updateActivity() {
		this.lastCommunication = Instant.now();
	}

	// Allows Comparable to work
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Peer other = (Peer) obj;
		if (!(peer_ip.equals(other.peer_ip) && peer_port == other.peer_port))
			return false;
		return true;
	}
}

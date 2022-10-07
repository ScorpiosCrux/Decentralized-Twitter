package Main.HelperDataClasses;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Peer {
    
    // This peer
    private String ip;
	private int port;

	private boolean active;
	private Instant lastCommunication;
	private String creationTime;
	private int timeStamp;

	
	public Peer(String ip, int port) {
		this.ip = ip;
		this.port = port;
		
		this.active = true;
		this.lastCommunication = Instant.now();
		this.timeStamp = 0;

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
		String creationTime = dtf.format(LocalDateTime.now());
		this.creationTime = creationTime;
	}


	public String toString() {
		return ip + ":" + port;
	}
	
	public String getIP() {
		return ip;
	}
	
	public int getPort() {
		return port;
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
	
	public String getCreationTime() {
		return creationTime;
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
        if (!(ip.equals(other.ip) && port == other.port))
            return false;
        return true;
    }   
}

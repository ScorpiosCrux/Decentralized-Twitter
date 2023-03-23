package Host;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Main.PeerSoftware.Settings;

public class Host {
	private String ip;
	private int port;
	private boolean isActive;
	private Instant lastCommunication;
	private String creationTime;
	private int timeStamp;

	public Host(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.isActive = true;
		this.lastCommunication = Instant.now();
		this.timeStamp = 0;

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String creationTime = dtf.format(LocalDateTime.now());
		this.creationTime = creationTime;
	}

	public String toString() {
		return this.ip + ":" + this.port;
	}

	public String getIPAddress() {
		return this.ip;
	}

	public int getPort() {
		return this.port;
	}

	public boolean getActivity() {
		return this.isActive;
	}

	public void setActivity(boolean value) {
		this.isActive = value;
	}

	public void setInstant(Instant time) {
		this.lastCommunication = time;
	}

	public Instant getInstant() {
		return lastCommunication;
	}

	public int getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(int ts) {
		this.timeStamp = ts;
	}

	public void setMaxTimeStamp(int received_timestamp) {
		this.timeStamp = Math.max(this.timeStamp, received_timestamp);
	}

	public void incrementTimeStamp() {
		this.timeStamp += 1;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void checkActivity(int inactivity_max) {
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(lastCommunication, end);

		/* If the host stops communicating, set peer as inactive again. */
		if (timeElapsed.toSeconds() > inactivity_max && isActive) {
			isActive = false;
			if (Settings.DEBUG) {
				System.out.println("DEBUG: Host (" + this.toString() + ") Activity: Info: " + this.isActive);
			}
		}
		/* If host resumes communication, set peer as active again. */
		else if (timeElapsed.toSeconds() < inactivity_max && !isActive) {
			isActive = true;
			if (Settings.DEBUG) {
				System.out.println("DEBUG: Host (" + this.toString() + ") Activity: Info: " + this.isActive);
			}
		}
	}

	public void updateActivity() {
		this.lastCommunication = Instant.now();
	}

	/* Allows comparable to work */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Host other = (Host) obj;
		if (!(ip.equals(other.ip) && port == other.port))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + this.ip.hashCode();
		result = 31 * result + port;
		return result;
	}
}

package Main.HelperDataClasses;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */
//This class create a log for UDPMessages

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UDPMessageLog {
	PeerOld msgOrigin;
	PeerOld transmittedPeer;
	String timeStamp;
	
	public UDPMessageLog(PeerOld msgOrigin, PeerOld transmittedPeer, String timeStamp){
		this.msgOrigin = msgOrigin;
		this.transmittedPeer = transmittedPeer;
		if (timeStamp == null) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
			String creationTime = dtf.format(LocalDateTime.now());
			this.timeStamp = creationTime;
		} else {
			this.timeStamp = timeStamp;
		}
	}

	public PeerOld getMsgOrigin(){
		return this.msgOrigin;
	}

	public PeerOld getTransmittedPeer(){
		return this.transmittedPeer;
	}

	public String getTimeStamp(){
		return this.timeStamp;
	}
	
}

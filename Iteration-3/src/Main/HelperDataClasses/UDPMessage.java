package Main.HelperDataClasses;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

public class UDPMessage {
	private String message;
	private Peer sourcePeer;

	public UDPMessage(String message, Peer sourcePeer) {
		this.message = message;
		this.sourcePeer = sourcePeer;
	}

	public String getMessage() {
		return this.message;
	}

	public Peer getSourcePeer() {
		return sourcePeer;
	}

}

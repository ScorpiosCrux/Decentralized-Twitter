package Main.HelperDataClasses;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

import Host.Host;

public class UDPMessagePack {
	private String message;
	private Host source;

	public UDPMessagePack(String message, Host source) {
		this.message = message;
		this.source = source;
	}

	public String getMessage() {
		return this.message;
	}

	public Host getSourcePeer() {
		return source;
	}

}

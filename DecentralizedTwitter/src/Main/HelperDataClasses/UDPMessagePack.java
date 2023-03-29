package Main.HelperDataClasses;

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

	/* 
	 * Returns the Host of the message origin
	 */
	public Host getSource() {
		return source;
	}

}

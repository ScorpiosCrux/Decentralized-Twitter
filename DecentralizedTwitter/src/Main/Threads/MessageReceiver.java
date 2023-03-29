package Main.Threads;

import Main.PeerSoftware;
import Main.HelperDataClasses.UDPMessagePack;
import Main.PeerSoftware.Settings;

/* This class handles all message sending */
public class MessageReceiver extends Thread {

	private PeerSoftware ps;

	/* Thread Setup */
	private Thread t;
	private String threadName;
	private boolean threadStop;

	public MessageReceiver(PeerSoftware ps) {
		this.threadName = "-- Message Receiver";
		this.ps = ps;
	}

	public void start() {
		System.out.println("SYSTEM: Starting " + threadName + " thread");
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}

	public void setThreadStop() {
		this.threadStop = true;
	}

	@Override
	public void run() {
		System.out.println("SYSTEM: Running " + threadName + " thread");

		while (true) {
			UDPMessagePack pack = ps.networkHandler.receiveUDPMessage();
			if (Settings.DEBUG) {
				System.out.println(
						"DEBUG: RECEIVED MESSAGE FROM " + pack.getSource().toString() + " CONTENT: " + pack.getMessage());
			}

			try {
				String messageType = pack.getMessage().substring(0, 4);

				switch (messageType) {
					case "peer":
						ps.hostMap.refreshHost(pack.getSource());
						break;
					case "snip":
						System.out.println("SYSTEM: (Incoming Message) " + pack.getMessage());
						break;
					case "stop":
						System.out.println("SYSTEM: RECEIVED STOP REQUEST.\nSTARTING SHUT DOWN...\n");
						ps.stopPeerCommunication();
						break;
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Invalid Message Type! Message: " + pack.getMessage());
			}

			if (threadStop)
				break;

		}

		System.out.println("SYSTEM: " + threadName + " exiting!");
	}
}

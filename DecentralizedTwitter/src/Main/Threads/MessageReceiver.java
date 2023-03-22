package Main.Threads;

import Main.PeerSoftware;

/* This class handles all message sending */
public class MessageReceiver extends Thread {

	private PeerSoftware ps;

	/* Thread Setup */
	private Thread t;
	private String threadName;

	protected MessageReceiver(PeerSoftware ps) {
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

	@Override
	public void run() {
		System.out.println("SYSTEM: Running " + threadName + " thread");

		System.out.println("SYSTEM: " + threadName + " thread exiting!");
	}
}


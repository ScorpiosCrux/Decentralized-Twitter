package Main.Threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import Host.Host;
import Main.PeerSoftware;
import Main.PeerSoftware.Settings;

/* This class handles all message the user wishes to send */
public class MessageSender extends Thread {

	private PeerSoftware ps;

	/* Thread Setup */
	private Thread t;
	private String threadName;
	private boolean threadStop;

	public MessageSender(PeerSoftware ps) {
		this.threadName = "-- Message Sender";
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

		int INPUT_CHECK_FREQUENCY_MILLISECONDS = Settings.INPUT_CHECK_FREQUENCY_MILLISECONDS;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("\nTweet your thoughts: ");

		while (true) {
			try {
				if (br.ready()) {
					String content = br.readLine();
					// ourselves.incrementTimeStamp();
					broadcastMessage(content);
					System.out.println("SYSTEM: Tweet has been sent!\n\nTweet your thoughts: ");
				} else {
					Thread.sleep(INPUT_CHECK_FREQUENCY_MILLISECONDS);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (threadStop)
				break;
		}

		System.out.println("SYSTEM: " + threadName + " exiting!");
	}

	/*
	 * Sends the inputted message to all active peers
	 * 
	 */
	public void broadcastMessage(String message) {
		// String data = "snip" + ourselves.getTimeStamp() + " " + message;
		String updatedMessage = "snip1" + " " + message;

		Vector<Host> activeHosts = ps.hostMap.getActiveHosts();
		for (Host host : activeHosts) {
			ps.networkHandler.send(host.getIPAddress(), host.getPort(), updatedMessage);
			// sent_logs.addLog(dest_ip, dest_port););
		}

	}
}

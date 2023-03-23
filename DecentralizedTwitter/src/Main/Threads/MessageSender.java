package Main.Threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import Main.PeerSoftware;
import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.Source;
import Main.PeerSoftware.Settings;

/* This class handles all message the user wishes to send */
public class MessageSender extends Thread {

	private PeerSoftware ps;

	/* Thread Setup */
	private Thread t;
	private String threadName;
	private boolean threadStop;

	private String ip;
	private int port;

	public MessageSender(PeerSoftware ps) {
		this.threadName = "-- Message Sender";
		this.ps = ps;

		this.ip = ps.network_handler.getExternalIP();
		this.port = Settings.CLIENT_PORT;
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

		int INPUT_CHECK_FREQUENCY_MILLISECONDS = Settings.INPUT_CHECK_FREQUENCY_MILLISECONDS;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Tweet your thoughts: ");

		while (true) {
			try {
				if (br.ready()) {
					String content = br.readLine();
					// ourselves.incrementTimeStamp();
					sendPeerMessage(content);
					System.out.println("Tweet has been tweeted! \nTweet your thoughts: ");
				} else {
					Thread.sleep(INPUT_CHECK_FREQUENCY_MILLISECONDS);
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (threadStop)
				break;
		}

		System.out.println("SYSTEM: " + threadName + " thread exiting!");
	}

	/* Sends the inputted message to all active peers */
	public void sendPeerMessage(String message) {
		// String data = "snip" + ourselves.getTimeStamp() + " " + message;
		String updatedMessage = "snip1" + " " + message;

		Vector<Source> source_list = ps.sourceList.getSources();
		for (Source s : source_list) {
			Vector<Peer> active_peers = s.getActivePeers();
			for (Peer p : active_peers) {
				ps.network_handler.send(p.getIP(), p.getPort(), updatedMessage);
				// sent_logs.addLog(dest_ip, dest_port););
			}
		}

	}
}

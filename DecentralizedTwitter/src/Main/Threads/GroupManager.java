package Main.Threads;

import java.util.Vector;
import Main.PeerSoftware;
import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.Source;
import Main.PeerSoftware.Settings;

/*
* This threaded class manages checking inactivity and sending updates to peers
* that we're still active.
* Source: https://www.tutorialspoint.com/java/java_multithreading.htm
*/
public class GroupManager extends Thread {

	private PeerSoftware ps;

	/* Thread Setup */
	private Thread t;
	private String threadName;
	private String ip;
	private int port;
	private boolean stop;

	/* Constructor */
	public GroupManager(PeerSoftware ps) {
		this.threadName = "-- Group Manager";
		this.ps = ps;

		// this.all_sources = parent.getAllSources();
		// this.sent_logs = parent.getSentLogs();

		this.ip = ps.network_handler.getExternalIP();
		this.port = Settings.CLIENT_PORT;

		this.stop = false;

		System.out.println("SYSTEM: Creating " + threadName + " thread");
	}

	/* Starts the thread */
	public void start() {
		System.out.println("SYSTEM: Starting " + threadName + " thread");
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}

	/* Runs the thread */
	@Override
	public void run() {
		System.out.println("SYSTEM: Running " + threadName + " thread");

		int MAX_INACTIVITY_SECONDS = Settings.MAX_INACTIVITY_SECONDS;
		int BROADCAST_INTERVALS_SECONDS = Settings.BROADCAST_INTERVALS_SECONDS;
		int timeSlept = 0;

		while (stop != true) {
			try {
				sendPeerUpdates();
				if (timeSlept >= BROADCAST_INTERVALS_SECONDS) {
					ps.sourceList.checkActivity(MAX_INACTIVITY_SECONDS);
					timeSlept = 0;
				}
				timeSlept += BROADCAST_INTERVALS_SECONDS;
				Thread.sleep(BROADCAST_INTERVALS_SECONDS * 1000);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error in Group Management!");
			}

		}
		System.out.println("Thread " + threadName + " exiting");
	}

	public void setStop() {
		this.stop = true;
	}

	private void sendPeerUpdates() {
		String message = "peer" + this.ip + ":" + this.port;
		
		Vector<Source> source_list = ps.sourceList.getSources();
		for (Source s : source_list) {
			Vector<Peer> active_peers = s.getActivePeers();
			for (Peer p : active_peers) {
				ps.network_handler.send(p.getIP(), p.getPort(), message);
				// sent_logs.addLog(dest_ip, dest_port););
			}
		}
	}
}

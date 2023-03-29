package Main.Threads;

import java.util.Vector;
import Host.Host;
import Main.PeerSoftware;
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

		int BROADCAST_INTERVALS_SECONDS = Settings.BROADCAST_INTERVALS_SECONDS;
		int timeSlept = 0;

		ps.hostMap.checkActiveHosts();
		Vector<Host> activeHosts = ps.hostMap.getActiveHosts();

		while (stop != true) {
			try {
				sendPeerUpdates(activeHosts);
				if (timeSlept >= BROADCAST_INTERVALS_SECONDS) {
					ps.hostMap.checkActiveHosts();
					activeHosts = ps.hostMap.getActiveHosts();
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

	private void sendPeerUpdates(Vector<Host> activeHosts) {
		String message = "peer" + this.ip + ":" + this.port;

		for (Host host : activeHosts) {
			ps.network_handler.send(host.getIPAddress(), host.getPort(), message);
			// sent_logs.addLog(dest_ip, dest_port););
		}
	}
}

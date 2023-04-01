package Main.Threads;

import java.util.Vector;
import Host.Host;
import Main.PeerSoftware;
import Main.PeerSoftware.Settings;


public class GroupManager extends ThreadedRunner {

	private String ip;
	private int port;

	/* Constructor */
	public GroupManager(PeerSoftware ps) {
		super(ps, "-- Group Manager");

		this.ip = ps.networkHandler.getExternalIP();
		this.port = Settings.CLIENT_PORT;
	}


	/* Runs the thread */
	@Override
	public void run() {
		System.out.println("SYSTEM: Running " + threadName + " thread");

		int BROADCAST_INTERVALS_SECONDS = Settings.BROADCAST_INTERVALS_SECONDS;
		int timeSlept = 0;

		ps.hostMap.checkActiveHosts();
		Vector<Host> activeHosts = ps.hostMap.getActiveHosts();

		while (true) {
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

			if (threadStop)
				break;
		}

		System.out.println("SYSTEM: " + threadName + " exiting!");
	}

	private void sendPeerUpdates(Vector<Host> activeHosts) {
		String message = "peer" + this.ip + ":" + this.port;

		for (Host host : activeHosts) {
			ps.networkHandler.send(host.getIPAddress(), host.getPort(), message);
			// sent_logs.addLog(dest_ip, dest_port););
		}
	}
}

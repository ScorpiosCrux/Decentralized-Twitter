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

	@Override
	public void run() {
		System.out.println("SYSTEM: Running " + threadName + " thread");

		while (true) {
			UDPMessagePack pack = ps.network_handler.receiveUDPMessage();
			if (Settings.DEBUG) {
				System.out.println(
						"DEBUG: RECEIVED MESSAGE FROM " + pack.getSource().toString() + " CONTENT: " + pack.getMessage());
			}

			try {
				String messageType = pack.getMessage().substring(0, 4);

				switch (messageType) {
					case "peer":
						System.out.println("Peer");
						ps.hostMap.refreshHost(pack.getSource());
						// HandlePeerUpdate pu = new HandlePeerUpdate(this, message);
						// pu.start();
						break;
					case "snip":
						System.out.println("SYSTEM: (Incoming Message) " + pack.getMessage());
						// snippet_handler.handleIncomingSnip(message);
						break;
					case "stop":
						System.out.println("Stop");
						// sendUDPStopMessage(message.getSourcePeer());
						// stop = true;
						// group_management.setStop();
						// snippet_handler.setStop();
						// System.out.println("\n\nstop");
						break;
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Invalid Message Type! Message: " + pack.getMessage());
			}

			if (threadStop)
				break;

		}

		System.out.println("SYSTEM: " + threadName + " thread exiting!");
	}
}

/*
 * // Sends a stop message to registry request to stop
 * private void sendUDPStopMessage(Peer peer) {
 * try {
 * String ip = peer.getIP();
 * int port = peer.getPort();
 * 
 * byte[] buffer = new byte[1024];
 * InetAddress address = InetAddress.getByName(ip);
 * System.out.println("\n\n\n\n\nAddress that sent stop: " +
 * address.toString());
 * 
 * String data = "ack" + Settings.TEAM_NAME;
 * buffer = data.getBytes();
 * DatagramPacket response = new DatagramPacket(buffer, buffer.length, address,
 * port);
 * 
 * this.sent_logs.addLog(Settings.REGISTRY_IP, Settings.REGISTRY_PORT);
 * network_handler.getOutgoingUDPSocket().send(response);
 * // System.out.println("Broadcast to: " + peer.toString());
 * 
 * } catch (IOException e) {
 * e.printStackTrace();
 * System.out.println("Unable to sendUDPMessage (GroupManagment): " +
 * peer.toString());
 * }
 * 
 * }
 */

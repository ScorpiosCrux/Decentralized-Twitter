package MainHandlers;

import Main.GroupManagement;
import Main.HandlePeerUpdate;
import Main.SnippetHandler;
import Main.UDPMessage;

public class PeerCommHandler {
    

    /* void peerCommunication() {
		// create objects for group management and snippetHandler, these are in their
		// own threads
		GroupManagement gm = new GroupManagement("Group Management", this.peer_socket, this.externalIP,
				this.settings.client_port, this.listOfSources, this.peersSent);
		gm.start();

		SnippetHandler sh = new SnippetHandler(this, "SnippetHandler", this.listOfSources, this.peer_socket,
				this.externalIP,
				this.settings.client_port, this.allSnippets);
		sh.start();

		// infinite loop until a stop has been received. this is the thread for
		// receiving UDP messages
		boolean stop = false;
		while (true) {
			UDPMessage message = receiveUDPMsg();
			String msgType = "";
			// System.out.println("Received message: " + message.message);

			try {
				msgType = message.message.substring(0, 4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			switch (msgType) {
				case "peer":
					HandlePeerUpdate pu = new HandlePeerUpdate("PeerUpdate", this.listOfSources, message, this.registry,
							this.peersReceived);
					pu.start();
					break;
				case "snip":
					// System.out.println("\n\n" + message.message + "\n\n");
					sh.handleIncomingSnip(message.message, message.sourcePeer);
					break;
				case "stop":
					sendUDPStopMessage(message.sourcePeer);
					stop = true;
					gm.setStop();
					sh.setStop();
					System.out.println("\n\nstop");
					break;
			}

			if (stop == true)
				break;

			// for (Map.Entry<Source, Vector<Peer>> s : this.listOfSources.entrySet())
			// System.out.println("Looped. Number of peers: " + s.getValue().size());
		}
	}

     */
}

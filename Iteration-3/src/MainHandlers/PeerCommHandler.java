package MainHandlers;

import java.util.Hashtable;
import java.util.Vector;


import Main.GroupManagement;
import Main.HandlePeerUpdate;
import Main.Peer;
import Main.SnippetHandler;
import Main.Source;
import Main.UDPMessage;
import Settings.UserSettings;

public class PeerCommHandler {
    UserSettings settings;
    NetworkHandler network_handler;

    GroupManagement group_management;
    SnippetHandler snippet_handler;

	private Hashtable<Source, Vector<Peer>> all_sources = new Hashtable<Source, Vector<Peer>>();

    // Constructor
    public PeerCommHandler(UserSettings settings, NetworkHandler network_handler) {
        this.settings = settings;
        this.network_handler = network_handler;

        // create objects for group management and snippetHandler, these are in their
        // own threads
        this.group_management = new GroupManagement("Group Management", this.peer_socket, this.externalIP,
                this.settings.client_port, this.listOfSources, this.peersSent);
        group_management.start();

        snippet_handler = new SnippetHandler(settings, network_handler, this);
        snippet_handler.start();
    }

    public void start() {

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
                    group_management.setStop();
                    snippet_handler.setStop();
                    System.out.println("\n\nstop");
                    break;
            }

            if (stop == true)
                break;

            // for (Map.Entry<Source, Vector<Peer>> s : this.listOfSources.entrySet())
            // System.out.println("Looped. Number of peers: " + s.getValue().size());
        }
    }

    public Hashtable<Source, Vector<Peer>> getAllSources(){
        return all_sources;
    }

}

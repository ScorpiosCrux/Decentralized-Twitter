package MainHandlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

import Main.GroupManagement;
import Main.HandlePeerUpdate;
import Main.HelperDataClasses.PeerOld;
import Main.HelperDataClasses.SnippetLog;
import Main.HelperDataClasses.SourceOld;
import Main.HelperDataClasses.UDPMessage;
import Main.HelperDataClasses.UDPMessageLog;
import Settings.UserSettings;

public class PeerCommHandler {
    UserSettings settings;
    NetworkHandler network_handler;

    GroupManagement group_management;
    SnippetHandler snippet_handler;

    private Hashtable<SourceOld, Vector<PeerOld>> all_sources = new Hashtable<SourceOld, Vector<PeerOld>>();// Used in all below
    private Vector<UDPMessageLog> peers_received = new Vector<UDPMessageLog>(); // Used in HandlePeerUpdate
    private Vector<SnippetLog> all_snippets = new Vector<SnippetLog>(); // Used in SnippetHandler.java
    private Vector<UDPMessageLog> peers_sent = new Vector<UDPMessageLog>(); // Used in GroupManagement.java

    // Constructor
    public PeerCommHandler(UserSettings settings, NetworkHandler network_handler) {
        this.settings = settings;
        this.network_handler = network_handler;

        // create objects for group management and snippetHandler, these are in their
        // own threads
        this.group_management = new GroupManagement(settings, network_handler, this);
        group_management.start();

        snippet_handler = new SnippetHandler(settings, network_handler, this);
        snippet_handler.start();
    }

    public void start() {
        // infinite loop until a stop has been received. this is the thread for
        // receiving UDP messages
        boolean stop = false;
        while (true) {
            UDPMessage message = network_handler.receiveUDPMsg();
            String msgType = "";
            // System.out.println("Received message: " + message.message);

            try {
                msgType = message.getMessage().substring(0, 4);
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (msgType) {
                case "peer":
                    HandlePeerUpdate pu = new HandlePeerUpdate(this.settings, message, this);
                    pu.start();
                    break;
                case "snip":
                    snippet_handler.handleIncomingSnip(message.getMessage(), message.getSourcePeer());
                    break;
                case "stop":
                    sendUDPStopMessage(message.getSourcePeer());
                    stop = true;
                    group_management.setStop();
                    snippet_handler.setStop();
                    System.out.println("\n\nstop");
                    break;
            }

            if (stop == true)
                break;

        }
    }

    // Sends a stop message to registry request to stop
    private void sendUDPStopMessage(PeerOld peer) {
        try {
            String ip = peer.getIP();
            int port = peer.getPort();

            byte[] buffer = new byte[1024];
            InetAddress address = InetAddress.getByName(ip);
            System.out.println("\n\n\n\n\nAddress that sent stop: " + address.toString());

            String data = "ack" + settings.team_name;
            buffer = data.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);

            this.group_management.getSendLogs().add(new UDPMessageLog(peer,
                    new PeerOld(network_handler.getExternalIP(), settings.client_port, null), null));
            network_handler.getOutGoingUDP().send(response);
            // System.out.println("Broadcast to: " + peer.toString());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to sendUDPMessage (GroupManagment): " + peer.toString());
        }

    }

    public Hashtable<SourceOld, Vector<PeerOld>> getAllSources() {
        return all_sources;
    }


    public Vector<UDPMessageLog> getAllPeersRec(){
        return peers_received;
    }

    public Vector<SnippetLog> getAllSnippets(){
        return all_snippets;
    }

    public Vector<UDPMessageLog> getAllPeersSent(){
        return peers_sent;
    }

}

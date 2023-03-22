package MainHandlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Vector;
import Main.GroupManagement;
import Main.HandlePeerUpdate;
import Main.HelperDataClasses.MessageLogs;
import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.SnippetLog;
import Main.HelperDataClasses.SourceList;
import Main.HelperDataClasses.UDPMessage;
import Settings.UserSettings;

public class PeerCommHandler {
    UserSettings settings;
    NetworkHandler network_handler;

    GroupManagement group_management;
    SnippetHandler snippet_handler;

    private SourceList all_sources;
    private final Vector<SnippetLog> all_snippets = new Vector<SnippetLog>(); // Used in SnippetHandler.java
    private final MessageLogs sent_logs;
    private final MessageLogs received_logs;
    
    // Constructor
    public PeerCommHandler(UserSettings settings, NetworkHandler network_handler, SourceList all_sources) {
        this.settings = settings;
        this.network_handler = network_handler;
        this.sent_logs = new MessageLogs(network_handler.getExternalIP(), settings.client_port);
        this.received_logs = new MessageLogs(network_handler.getExternalIP(), settings.client_port);
        this.all_sources = all_sources;
    }

    public void start() {
        // create objects for group management and snippetHandler, these are in their
        // own threads
        this.group_management = new GroupManagement(settings, network_handler, this);
        group_management.start();

        snippet_handler = new SnippetHandler(settings, network_handler, this);
        snippet_handler.start();

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
                    HandlePeerUpdate pu = new HandlePeerUpdate(this, message);
                    pu.start();
                    break;
                case "snip":
                    snippet_handler.handleIncomingSnip(message);
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
    private void sendUDPStopMessage(Peer peer) {
        try {
            String ip = peer.getIP();
            int port = peer.getPort();

            byte[] buffer = new byte[1024];
            InetAddress address = InetAddress.getByName(ip);
            System.out.println("\n\n\n\n\nAddress that sent stop: " + address.toString());

            String data = "ack" + settings.team_name;
            buffer = data.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);

            
            this.sent_logs.addLog(settings.registry_ip, settings.registry_port);
            network_handler.getOutGoingUDP().send(response);
            // System.out.println("Broadcast to: " + peer.toString());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to sendUDPMessage (GroupManagment): " + peer.toString());
        }

    }

    public SourceList getAllSources() {
        return all_sources;
    }

    public Vector<SnippetLog> getAllSnippets() {
        return all_snippets;
    }

    public MessageLogs getSentLogs(){
        return this.sent_logs;
    }

    public MessageLogs getReceivedLogs(){
        return this.received_logs;
    }

    public NetworkHandler getNetworkHandler(){
        return this.network_handler;
    }

    public UserSettings getSettings(){
        return this.settings;
    }

}

package Handlers.Registry;

/* Imports */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import Handlers.PrintHandler;
import Main.PeerSoftware;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.SourceList;
import Main.PeerSoftware.Settings;

/* This class handles communication with the registry */
public class RegistryHandler {

    private PeerSoftware ps;
    private RequestHandler requestHandler;

    private Source registry;
    private boolean registry_connected;

    /*
     * Registry Constructor
     * 
     */
    public RegistryHandler(PeerSoftware ps) {
        this.ps = ps;
        this.requestHandler = new RequestHandler(ps.externalIP, Settings.CLIENT_PORT);
    }

    /*
     * Registry Runner
     * 
     */
    public void start(int port) throws IOException {

        /* Adds the registry as a source for peers */
        this.ps.hostMap.addSource(Settings.REGISTRY_IP, Settings.REGISTRY_PORT);

        PrintHandler print_handler = ps.getPrintHandler();

        Socket registry_socket = ps.network_handler.createSocket(Settings.REGISTRY_IP, Settings.REGISTRY_PORT);

        BufferedReader reader = new BufferedReader(new InputStreamReader(registry_socket.getInputStream()));

        if (registry_socket.isConnected())
            this.registry_connected = true;

        while (registry_socket.isConnected()) {
            String request = readSocket(reader, registry_socket); // Read request
            String returnMessage = request_handler.handleRequest(registry_socket, request); // Handle request
            if (returnMessage.equals("-1")) {
                print_handler.printError("Error 0 returned from handleRequest");
                break;
            } else if (returnMessage.equals("0")) {
                continue;
            } else if (returnMessage.equals("1"))
                break;
            ps.network_handler.send(registry_socket, returnMessage); // Send response
            print_handler.printResponse(returnMessage, request);
        }
        registry_socket.close();
    }

    // Read the message in the socket, if the message is receive peers, we want to
    // keep reading
    // Returns a string that contains the message
    private String readSocket(BufferedReader reader, Socket socket) throws IOException {
        String message = reader.readLine();
        if (message == null) {
            System.out.println("SOCKET RECEIVED: NULL");
            return "";
        } else if (message.equals("receive peers")) {
            receivePeers(socket, reader);
        }
        return message;
    }

    /*
     * Receive peers from registry. Adds the peers to a SourceList.
     */
    private void receivePeers(Socket socket, BufferedReader reader) throws IOException {
        // SourceList all_sources = peer_comm_handler.getAllSources();

        String numOfPeersString = reader.readLine();
        try {
            int numOfPeers = Integer.parseInt(numOfPeersString);
            for (int i = 0; i < numOfPeers; i++) {
                String peer = reader.readLine();
                String[] parts = peer.split(":");
                String ip = parts[0];
                int port = Integer.parseInt(parts[1]);
                /*
                 * if (ip.equals(peer_comm_handler.getNetworkHandler().getExternalIP()) && port
                 * == settings.client_port)
                 * continue;
                 */
                ps.sourceList.addPeer(Settings.REGISTRY_IP, Settings.REGISTRY_PORT, ip, port);
            }
        } catch (Exception e) {
            System.out.println("Error in receiving peers!");
        }
    }

    /* ===================== GETTERS ===================== */

    public boolean getRegistryStatus() {
        return registry_connected;
    }

    public Source getRegistry() {
        return this.registry;
    }

    /* ===================== (END) GETTERS ===================== */

}

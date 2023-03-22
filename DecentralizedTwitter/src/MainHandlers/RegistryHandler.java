package MainHandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import Main.Main;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.SourceList;
import Settings.UserSettings;

public class RegistryHandler {

    private UserSettings settings;
    private Main main;
    private RequestHandler request_handler;
    private PeerCommHandler peer_comm_handler;

    private Source registry;
    private boolean registry_connected;

    // Constructor
    public RegistryHandler(UserSettings settings, Main main, PeerCommHandler peer_comm_handler) {
        this.settings = settings;
        this.main = main;
        this.request_handler = new RequestHandler(settings, main, peer_comm_handler);
        this.peer_comm_handler = peer_comm_handler;
    
        this.registry = new Source(settings.registry_ip, settings.registry_port);
    }

    // Getters and Setters

    public boolean isRegistryConnected() {
        return registry_connected;
    }

    public Source getRegistry() {
        return this.registry;
    }

    public void start(int port) throws IOException {
        NetworkHandler network_handler = main.getNetworkHandler();
        PrintHandler print_handler = main.getPrintHandler();

        Socket registry_socket = network_handler.createSocket(settings.registry_ip, settings.registry_port);

        BufferedReader reader = new BufferedReader(new InputStreamReader(registry_socket.getInputStream()));

        if (registry_socket.isConnected())
            this.registry_connected = true;

        while (registry_socket.isConnected()) {
            String request = readSocket(reader, registry_socket); // Read request
            String returnMessage = request_handler.handleRequest(registry_socket, request); // Handle request
            if (returnMessage.equals("-1")) {
                print_handler.printError("Error 0 returned from handleRequest");
                break;
            } else if (returnMessage.equals("0")){
                continue;
            }else if (returnMessage.equals("1"))
                break;
            network_handler.send(registry_socket, returnMessage); // Send response
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
        SourceList all_sources = peer_comm_handler.getAllSources();

        String numOfPeersString = reader.readLine();
        try {
            int numOfPeers = Integer.parseInt(numOfPeersString);
            for (int i = 0; i < numOfPeers; i++) {
                String peer = reader.readLine();
                String[] parts = peer.split(":");
                String ip = parts[0];
                int port = Integer.parseInt(parts[1]);
                /* if (ip.equals(peer_comm_handler.getNetworkHandler().getExternalIP()) && port == settings.client_port)
                    continue; */
                all_sources.addPeer(settings.registry_ip, settings.registry_port, ip, port);
            }
        } catch (Exception e) {
            System.out.println("Error in receiving peers!");
        }
    }

}

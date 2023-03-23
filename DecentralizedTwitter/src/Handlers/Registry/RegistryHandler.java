package Handlers.Registry;

/* Imports */
import java.io.IOException;
import Handlers.PrintHandler;
import Main.PeerSoftware;
import Main.PeerSoftware.Settings;

/* This class handles communication with the registry */
public class RegistryHandler {

    private PeerSoftware ps;
    private RequestHandler requestHandler;
    private PrintHandler printHandler;

    /*
     * Registry Constructor
     * 
     */
    public RegistryHandler(PeerSoftware ps) {
        this.ps = ps;
        this.requestHandler = new RequestHandler(ps.externalIP, Settings.CLIENT_PORT);
        this.printHandler = new PrintHandler();

    }

    /*
     * Registry Runner
     * 
     */
    public void start(int port) throws IOException {

        /* Adds the registry as a source for peers */
        this.ps.hostMap.addSource(Settings.REGISTRY_IP, Settings.REGISTRY_PORT);

        ps.network_handler.createSocket(Settings.REGISTRY_IP, Settings.REGISTRY_PORT);

        while (ps.network_handler.checkConnection()) {
            /* Read Request */
            String request = ps.network_handler.readSocket();

            if (request.equals("receive peers")) {
                receivePeers();
                continue;
            }

            String response = requestHandler.handleRequest(request);
            if (response != null) {
                ps.network_handler.send(response);
                this.printHandler.printResponse(response, request);
                continue;
            }

            break;
        }
    }

    /*
     * Receive peers from registry. Adds the peers to a SourceList.
     * 
     */
    private void receivePeers() throws IOException {
        try {
            String message = ps.network_handler.readSocket();
            int numberOfPeers = Integer.parseInt(message);

            for (int i = 0; i < numberOfPeers; i++) {
                message = ps.network_handler.readSocket();

                String[] messageSplit = message.split(":");
                String ip = messageSplit[0];
                int port = Integer.parseInt(messageSplit[1]);

                if (ip.equals(ps.externalIP) && port == Settings.CLIENT_PORT)
                    continue;

                ps.hostMap.addHost(Settings.REGISTRY_IP, Settings.REGISTRY_PORT, ip, port);
            }
        } catch (Exception e) {
            System.out.println("SYSTEM: ERROR RECEIVING PEERS FROM REGISTRY");
        }
    }


}

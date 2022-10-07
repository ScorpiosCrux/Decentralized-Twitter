package MainHandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import Main.Iteration3Solution;
import Main.Peer;
import Main.Source;
import Settings.UserSettings;
import Testing.PrintHandler;

public class RegistryHandler {

    private UserSettings settings;
    private Iteration3Solution main;
    private RequestHandler request_handler;

    private Source registry;
	private boolean registry_connected;

    // Constructor
    public RegistryHandler(UserSettings settings, Iteration3Solution main) {
        this.settings = settings;
        this.main = main;
        this.request_handler = new RequestHandler(settings, main);

        this.registry = new Source(new Peer(settings.registry_ip, settings.registry_port, null));
    }

    // Getters and Setters

    public boolean isRegistryConnected() {
		return registry_connected;
	}

	public void setRegistry(Source registry) {
		this.registry = registry;
	}

	public Source getRegistry() {
		return this.registry;
	}

    public void start(int port) throws IOException {
        NetworkHandler network_handler = main.getNetworkHandler();
        PrintHandler print_handler = main.getPrintHandler();

		Socket registry_socket = network_handler.createSocket(this.registry.getPeer().getIP(),
				this.registry.getPeer().getPort());

		BufferedReader reader = new BufferedReader(new InputStreamReader(registry_socket.getInputStream()));

		

		if (registry_socket.isConnected())
			this.registry_connected = true;

		while (registry_socket.isConnected()) {
			String request = readSocket(reader, registry_socket); // Read request
			String returnMessage = request_handler.handleRequest(registry_socket, request); // Handle request
			if (returnMessage.equals("-1")) {
				print_handler.printError("Error 0 returned from handleRequest");
				break;
			} else if (returnMessage.equals("1"))
				break;
			network_handler.writeSocket(registry_socket, returnMessage); // Send response
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

    // Adds the source and peers to listOfSources reading from the reader.
    private void receivePeers(Socket socket, BufferedReader reader) throws IOException {
        Hashtable<Source, Vector<Peer>> listOfSources = main.getAllSources();

        // Check to see if the additional source is already in the list.
        // Source source = new Source(new Peer(getIP(socket), socket.getPort(), null));
        if (!listOfSources.contains(registry)) {
            listOfSources.put(registry, new Vector<Peer>());
        }

        String numOfPeersString = reader.readLine();
        try {
            int numOfPeers = Integer.parseInt(numOfPeersString);
            Vector<Peer> currentList = listOfSources.get(registry);
            for (int i = 0; i < numOfPeers; i++) {
                String peer = reader.readLine();
                String[] parts = peer.split(":");
                Peer newPeer = new Peer(parts[0], Integer.parseInt(parts[1]), registry.getPeer().toString());
                currentList.add(newPeer);
            }
            listOfSources.put(registry, currentList);
        } catch (Exception e) {
            System.out.println("Error in receiving peers!");
        }
    }




    
}

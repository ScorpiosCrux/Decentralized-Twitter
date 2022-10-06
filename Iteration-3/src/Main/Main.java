package Main;

import java.io.IOException;

import Settings.UserSettings;

public class Main {
    public static void main(String[] args) {
		UserSettings settings = new UserSettings();
		Iteration3Solution client = new Iteration3Solution(settings);

		Source registry = new Source(new Peer(settings.registry_ip, settings.registry_port, null)); // own
		client.setRegistry(registry);

		// initial connection to the registry
		try {
			client.start(settings.client_port);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}

		// communication with the peer
		client.peerCommunication();

		// final communicatino with registry
		try {
			client.stop();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
}

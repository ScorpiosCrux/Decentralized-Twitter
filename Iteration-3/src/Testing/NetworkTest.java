package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Main.Iteration3Solution;
import Main.Peer;
import Main.Source;
import MainHandlers.NetworkHandler;
import Settings.UserSettings;

public class NetworkTest {

    private static boolean setup = false;
    private Iteration3Solution client;
    private UserSettings settings;

    @BeforeEach
    public void setup() {
        if (setup)
            return;
        else {
            System.out.println("Setup Complete");

            this.settings = new UserSettings();
            this.client = new Iteration3Solution(settings);

            Source registry = new Source(new Peer(settings.registry_ip, settings.registry_port, null)); // own
            client.setRegistry(registry);
        }
    }

    @Test
    void testRegistryFailedConnection() {
        // Registry must be off for this to pass
        Exception e = assertThrows(IOException.class, () -> client.start(settings.client_port));
        assertTrue(e.getMessage().contains("Connection refused"));
    }

    @Test
    void testRegistrySuccessfulConnection() throws IOException {
        // Registry must be on for this to pass
        client.start(settings.client_port);
        assertTrue(client.isRegistryConnected());
    }

    @Test
    void createdUDPSocket() throws SocketException {
        NetworkHandler nh = new NetworkHandler();
        DatagramSocket socket;
        int port = 31824;

        socket = nh.createUDPSocket(port);
        assertEquals("Port Number should be the same", port, socket.getLocalPort());
    }


}

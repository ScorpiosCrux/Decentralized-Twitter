package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Main.Iteration3Solution;

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

            this.client = new Iteration3Solution(settings);
        }
    }

    @Test
    void testRegistryFailedConnection() {
        // Registry must be off for this to pass
        Exception e = assertThrows(IOException.class, () -> client.initRegistryCommunication(settings));
        assertTrue(e.getMessage().contains("Connection refused"));
    }

    // @Test
    // void testRegistrySuccessfulConnection() throws IOException {
    // // Registry must be on for this to pass
    // client.start(settings.client_port);
    // assertTrue(client.isRegistryConnected());
    // }

    @Test
    void createdUDPSocket() throws SocketException {

        DatagramSocket socket = null;
        int port = 31824;
        NetworkHandler nh;
        try {
            nh = new NetworkHandler(settings);
            socket = nh.createUDPSocket(port);
            assertEquals("Port Number should be the same", port, socket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertFalse("Unable to create UDP Socket", socket == null);

    }

}

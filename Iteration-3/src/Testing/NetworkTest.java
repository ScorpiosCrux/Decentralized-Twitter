package Testing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Main.Iteration3Solution;
import Main.Peer;
import Main.Source;
import Settings.UserSettings;

public class NetworkTest {

    private static boolean setup = false;
    private Iteration3Solution client;

    @BeforeEach
    public void setup() {
        if (setup)
            return;
        else {
            System.out.println("Setup Complete");

            UserSettings settings = new UserSettings();
            this.client = new Iteration3Solution(settings);

            Source registry = new Source(new Peer(settings.registry_ip, settings.registry_port, null)); // own
            client.setRegistry(registry);
        }
    }

    @Test
    void testRegistryConnection() {
        // initial connection to the registry
        System.out.println("Testing Registry");
        try {
            client.start(30000);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }
    }

    @Test
    void testMethod1() {
        System.out.println("**--- Test method1 executed ---**");
    }
}

package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Main.HelperDataClasses.SourceList;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;
import MainHandlers.SnippetHandler;
import Settings.UserSettings;


public class SnippetHandlerTest {
    private static boolean setup = false;

	private final SourceList all_sources = new SourceList();
    private UserSettings settings;
    private NetworkHandler network_handler;
    private PeerCommHandler peer_comm_handler;


    @BeforeEach
    public void setup() {
        if (setup)
            return;
        else {
            System.out.println("Setup Complete");

            this.settings = new UserSettings();
            try {
                this.network_handler = new NetworkHandler(settings);
                this.peer_comm_handler = new PeerCommHandler(settings, network_handler, all_sources);
                all_sources.addPeer("1.1.1.1", 1, "2.2.2.2", 2);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    @Test
    public void SnippetCreatedConstructor(){
        SnippetHandler snippet_handler = new SnippetHandler(settings, network_handler, peer_comm_handler);
        assertEquals(snippet_handler.getClass(), SnippetHandler.class);
    }

    @Test 
    public void SnippetCreationNullValues(){
        this.settings = null;
        this.network_handler = null;
        this.peer_comm_handler = null;
        
        // So that we can setup again
        SnippetHandlerTest.setup = false;

        Exception e = assertThrows(NullPointerException.class, () -> new SnippetHandler(settings, network_handler, peer_comm_handler));
        assertTrue(e.getMessage().contains("null"));
    }

    


}

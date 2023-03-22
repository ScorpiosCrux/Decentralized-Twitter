package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.SourceList;

public class SourceListTest {

    private SourceList sources;

    @BeforeEach
    public void setup() {
        this.sources = new SourceList();
    }

    @Test
    void testSourceList() {
        SourceList sources = new SourceList();
        assertTrue("Object should be properly created and empty", sources.isEmpty());
    }

    /*
     * Test finding an added Source obj
     */
    @Test
    void testFindSource() {
        String source_ip = "1.1.1.1";
        int source_port = 80;

        String peer_ip = "2.2.2.2";
        int peer_port = 30000;

        Source source = new Source(source_ip, source_port);

        this.sources.addPeer(source_ip, source_port, peer_ip, peer_port);
        Source target = this.sources.findSource(source_ip, source_port);
        assertEquals("Source IP and Port should be the same as target", source, target);
    }

    /*
     * Test finding a non existant Source obj
     */
    @Test
    void testFindNullSource() {
        String source_ip = "1.1.1.1";
        int source_port = 80;

        String peer_ip = "2.2.2.2";
        int peer_port = 30000;

        this.sources.addPeer(source_ip, source_port, peer_ip, peer_port);
        Source target = this.sources.findSource("3.3.3.3", source_port);
        assertEquals("Target should be null as this Source does not exist!", null, target);
    }

    /*
     * Test adding a peer and the source and then checking if the peer added is
     * assigned
     * Under the correct source.
     */
    @Test
    void testSourceListAddPeer() {
        String source_ip = "1.1.1.1";
        int source_port = 80;

        String peer_ip = "2.2.2.2";
        int peer_port = 30000;

        this.sources.addPeer(source_ip, source_port, peer_ip, peer_port);
        assertEquals("SourceList should not be empty and only contain one element", this.sources.getSize(), 1);
        Source target_source = this.sources.findSource(source_ip, source_port);
        Peer peer = new Peer(peer_ip, peer_port);
        Peer target = target_source.findPeer(peer_ip, peer_port);

        assertEquals("Peers should have same IP and Port as defined in Peer.java", peer, target);
    }

}

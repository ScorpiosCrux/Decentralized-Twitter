package Main.HelperDataClasses;

import java.util.Vector;

public class Source {

    private String source_ip;
    private int source_port;

    private Vector<Peer> peers;

    public Source(String ip, int port) {
        this.source_ip = ip;
        this.source_port = port;

        this.peers = new Vector<Peer>();
    }

    public Vector<Peer> getPeers() {
        return this.peers;
    }

    public Peer findPeer(String target_ip, int target_port) {
        Peer target = new Peer(target_ip, target_port);
        for (Peer peer : this.peers) {
            if (peer.equals(target))
                return peer;
        }
        return null;
    }

    public void addPeer(Peer peer) {
        this.peers.add(peer);
    }

    public String getIP() {
        return this.source_ip;
    }

    public int getPort() {
        return this.source_port;
    }

    public Vector<Peer> getActivePeers() {
        Vector<Peer> active_peers = new Vector<Peer>();

        for (Peer p : this.peers)
            if (p.isActive())
                active_peers.add(p);

        return active_peers;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Source other = (Source) obj;
        if (!(this.source_ip.equals(other.getIP()) && this.source_port == other.getPort()))
            return false;
        return true;
    }
}

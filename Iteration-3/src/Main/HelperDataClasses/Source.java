package Main.HelperDataClasses;

import java.util.Vector;

public class Source {

    private String source_ip;
    private int source_port;

    private Vector<Peer> peers;

    public Source(String ip, int port){
        this.source_ip = ip;
        this.source_port = port;
        
        this.peers = new Vector<Peer>();
    }

    public Vector<Peer> getPeers(){
        return this.peers;
    }

    public void addPeer(Peer peer){
        this.peers.add(peer);
    }

    public String getIP(){
        return this.source_ip;
    }

    public int port(){
        return this.source_port;
    }
    
}

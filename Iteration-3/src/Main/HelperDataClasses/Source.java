package Main.HelperDataClasses;

import java.util.Vector;

public class Source {

    private String ip;
    private int port;

    private Vector<Peer> peers;

    public Source(String ip, int port){
        this.ip = ip;
        this.port = port;
        
        this.peers = new Vector<Peer>();
    }

    public Vector<Peer> getPeers(){
        return this.peers;
    }

    public void addPeer(Peer peer){
        this.peers.add(peer);
    }

    public String getIP(){
        return this.ip;
    }

    public int port(){
        return this.port;
    }
    
}

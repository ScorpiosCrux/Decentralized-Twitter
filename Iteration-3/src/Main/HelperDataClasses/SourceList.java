package Main.HelperDataClasses;

import java.util.Vector;

public class SourceList {

    private Vector<Source> sources;

    public SourceList() {
        this.sources = new Vector<Source>();
    }

    public void addPeer(String source_ip, int source_port, String peer_ip, int peer_port) {
        Source target = findSource(source_ip, source_port);
        if (target != null)
            target.addPeer(new Peer(peer_ip, peer_port));
        else {
            Source new_source = new Source(source_ip, source_port);
            new_source.addPeer(new Peer(peer_ip, peer_port));
            this.sources.add(new_source);
        }
    }

    public void 

    public Source findSource(String ip, int port) {
        Source target = new Source(ip, port);
        for (Source source : this.sources) {
            if (source.equals(target))
                return source;
        }
        return null;
    }

    public void updateActivity(String source_ip, int source_port, String target_ip, int target_port){
        Source target_source = findSource(source_ip, source_port);
        target_source.updateActivity(target_ip, target_port);
    }

    public void checkActivity(int inactivity_max){
        for (Source s : sources)
            s.checkActivity(inactivity_max);
    }

    public boolean isEmpty(){
        return sources.isEmpty();
    }

    public int getSize(){
        return sources.size();
    }

    public Vector<Source> getSources(){
        return this.sources;
    }

}

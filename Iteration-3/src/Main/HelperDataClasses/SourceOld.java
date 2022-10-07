package Main.HelperDataClasses;

/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class SourceOld {
    private PeerOld peer;
    private String time;

    public SourceOld(PeerOld peer) {
        this.peer = peer;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        this.time = formatter.format(date);
    }

    public PeerOld getPeer() {
        return this.peer;
    }

    public String getTime() {
        return this.time;
    }

    // Allows Comparable to work
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SourceOld other = (SourceOld) obj;
        if (!(peer.equals(other.peer)))
            return false;
        return true;
    }

}

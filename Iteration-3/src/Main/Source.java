package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class Source {
	Peer peer;
	String time;
	
	
	public Source(Peer peer) {
		this.peer = peer;
		
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    Date date = new Date();  
	    this.time = formatter.format(date);  
	}
	
	//Allows Comparable to work
	public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Source other = (Source) obj;
        if (!(peer.equals(other.peer)))
            return false;
        return true;
    }   
	
}

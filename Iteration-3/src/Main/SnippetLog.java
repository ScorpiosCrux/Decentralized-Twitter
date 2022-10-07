package Main;

/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */
//This class is a log for Snippets
public class SnippetLog {
	private int timeStamp;
	private String content;
	private Peer sourcePeer;

	SnippetLog(int timeStamp, String content, Peer sourcePeer) {
		this.timeStamp = timeStamp;
		this.content = content;
		this.sourcePeer = sourcePeer;
	}

	public int getTimeStamp() {
		return this.timeStamp;
	}

	public String getContent() {
		return this.content;
	}

	public Peer getSourcePeer() {
		return this.sourcePeer;
	}

}

package Main.HelperDataClasses;

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
	private PeerOld sourcePeer;

	public SnippetLog(int timeStamp, String content, PeerOld sourcePeer) {
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

	public PeerOld getSourcePeer() {
		return this.sourcePeer;
	}

}

/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */
//This class is a log for Snippets
public class SnippetLog {
	int timeStamp;
	String content;
	Peer sourcePeer;
	
	SnippetLog(int timeStamp, String content, Peer sourcePeer){
		this.timeStamp = timeStamp;
		this.content = content;
		this.sourcePeer = sourcePeer;
	}
}

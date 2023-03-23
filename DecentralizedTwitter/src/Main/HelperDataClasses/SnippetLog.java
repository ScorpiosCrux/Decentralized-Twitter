package Main.HelperDataClasses;

import Host.Host;

//This class is a log for Snippets
public class SnippetLog {
	private int timeStamp;
	private String content;
	private Host source;

	public SnippetLog(int timeStamp, String content, Host source) {
		this.timeStamp = timeStamp;
		this.content = content;
		this.source = source;
	}

	public int getTimeStamp() {
		return this.timeStamp;
	}

	public String getContent() {
		return this.content;
	}

	public Host getSource() {
		return this.source;
	}

}

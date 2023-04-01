package Main.Threads;

import Main.PeerSoftware;

/*
* This threaded class manages checking inactivity and sending updates to peers
* that we're still active.
* Source: https://www.tutorialspoint.com/java/java_multithreading.htm
*/
public abstract class ThreadedRunner extends Thread {

	protected PeerSoftware ps;

	protected Thread t;
	protected String threadName;
	protected boolean threadStop;

	public ThreadedRunner(PeerSoftware ps, String threadName) {
		this.threadName = threadName;
		this.ps = ps;

		this.threadStop = false;

		System.out.println("SYSTEM: Creating " + threadName + " thread");
	}

	/* Starts the thread */
	public void start() {
		System.out.println("SYSTEM: Starting " + threadName + " thread");
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}

	public void setThreadStop() {
		this.threadStop = true;
	}

	@Override
	public abstract void run();

}

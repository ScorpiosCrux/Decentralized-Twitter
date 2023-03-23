package Handlers;

import java.util.concurrent.TimeUnit;

public class ProcessHandler {

	/* Sleeps for a bit */
	public static void pause(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

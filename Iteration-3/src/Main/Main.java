package Main;

import Settings.UserSettings;

public class Main {
	public static void main(String[] args) {
		UserSettings settings = new UserSettings();
		Iteration3Solution client = new Iteration3Solution(settings);
	}
}

package Main.HelperDataClasses;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataValidator {
    // Checks to see if the ip is a valid ip.
	// Inspired from:
	// https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
	public static boolean isValidIP(String ip) {
		String IPV4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
		Pattern pattern = Pattern.compile(IPV4_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	// Check to see if the port is a valid port
	public static boolean isValidPort(String port_in) {
		int port;
		try {
			port = Integer.parseInt(port_in);
			int length = port_in.length();
			if (port > 0 && length <= 5)
				return true;
			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}

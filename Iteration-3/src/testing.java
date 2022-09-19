/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testing {

	public static void main(String[] args) {
		String ip = "0.0.0.0";
		
		boolean test = isValidIP(ip);
		System.out.println(test);
		
		
		Peer test1 = new Peer("1.1.1.1", 55532, "test");
		System.out.println("test");

	}
	
	private static boolean isValidIP(String ip) {
		String IPV4_PATTERN =
	            "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
		
		
		Pattern pattern = Pattern.compile(IPV4_PATTERN);

		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	 
		
	}

}

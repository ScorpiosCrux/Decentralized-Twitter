package Testing;

public class PrintHandler {
    public void printResponse(String message, String request) {
        System.out.println("\nRequest: " + request);
        System.out.println("\t------BEGIN------");
        System.out.print(message);
        System.out.println("\t-------END-------\n");
    }

    public void printError(String message) {
        System.out.println("\n\t------ERROR------");
        System.out.print(message);
        System.out.println("\n\t------ERROR------");
    }
}

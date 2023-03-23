package Handlers.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import Main.PeerSoftware.Settings;

public class RequestHandler {

    /* Our information */
    String externalIP;
    int port;

    public RequestHandler(String externalIP, int port) {
        this.externalIP = externalIP;
        this.port = port;
    }

    // This function handles the requests from the registry and returns a response.
    // Other code: -1=error; 0=no response; 1=connection closed, exit main loop
    public String handleRequest(String request) throws IOException {
        switch (request) {
            case "get team name":
                return Settings.TEAM_NAME + "\n";
            case "get code":
                String language = "Java";
                String newline = "\n";
                String code = genSrcCodeRes();
                String end_of_code = "...";

                return language + newline + code + newline + end_of_code + newline;
            case "receive peers":
                return "0";
            case "get report":
                // return generateReport();
            case "get location":
                return this.externalIP + ":" + this.port + "\n";
            // case "close":
            // network_handler.closeSocket(socket);
            // return "1";
        }
        return "-1";
    }

    // Assuming that src code is less than 2 million chars.
    private String genSrcCodeRes() {
        String sourceCode = "";

        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Iteration2Solution.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Peer.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Source.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/GroupManagement.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/HandlePeerUpdate.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/ReturnSearch.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/SnippetHandler.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/SnippetLog.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/UDPMessage.java");
        // sourceCode +=
        // readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/UDPMessageLog.java");

        return sourceCode;
    }

    // Stolen from https://www.w3schools.com/java/java_files_read.asp
    private String readCode(String path) {
        String code = "";

        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                code += data + "\n";
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return code;
    }

    // Generates report based on assignment specs
    /*
     * private String generateReport() {
     * Hashtable<SourceOld, Vector<PeerOld>> all_sources = null;
     * Vector<SnippetLog> all_snippets = peer_comm_handler.getAllSnippets();
     * Vector<UDPMessageLog> peers_received = null;
     * //Vector<UDPMessageLog> peers_sent = peer_comm_handler.getAllPeersSent();
     * 
     * int numOfSources = all_sources.size();
     * int totalNumOfPeers = 0;
     * String peer_list_sources = "";
     * String peer_list = "";
     * String peers_recd = "";
     * String peers_sent_str = "";
     * String snip_list = "";
     * 
     * for (Map.Entry<SourceOld, Vector<PeerOld>> e : all_sources.entrySet()) {
     * SourceOld source = e.getKey();
     * String sourceLocation = source.getPeer().toString() + "\n";
     * Vector<PeerOld> listOfPeers = e.getValue();
     * String peers = "";
     * for (PeerOld p : listOfPeers) {
     * String peer_string = p.toString() + "\n";
     * peers += peer_string;
     * peer_list += peer_string;
     * totalNumOfPeers++;
     * }
     * // adds one source
     * peer_list_sources += sourceLocation + source.getTime() + "\n" +
     * listOfPeers.size() + "\n" + peers;
     * }
     * 
     * peers_recd += peers_received.size() + "\n";
     * for (UDPMessageLog m : peers_received) {
     * try {
     * peers_recd += m.getMsgOrigin().toString() + " " +
     * m.getTransmittedPeer().toString() + " "
     * + m.getTimeStamp() + "\n";
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     */
    /*
     * peers_sent_str += peers_sent.size() + "\n";
     * for (UDPMessageLog m : peers_sent) {
     * try {
     * peers_sent_str += m.getMsgOrigin().toString() + " " +
     * m.getTransmittedPeer().toString() + " "
     * + m.getTimeStamp() + "\n";
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     */

    /*
     * snip_list += all_snippets.size() + "\n";
     * for (SnippetLog sl : all_snippets) {
     * snip_list += sl.getTimeStamp() + " " + sl.getContent() + " " +
     * sl.getSourcePeer().toString() + "\n";
     * }
     * 
     * return totalNumOfPeers + "\n" + peer_list + numOfSources + "\n" +
     * peer_list_sources + peers_recd + peers_sent_str + snip_list;
     * }
     */

}

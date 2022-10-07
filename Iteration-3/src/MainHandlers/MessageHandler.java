package MainHandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import Main.Iteration3Solution;
import Main.Peer;
import Main.SnippetLog;
import Main.Source;
import Main.UDPMessageLog;
import Settings.UserSettings;

public class MessageHandler {
    // This function handles the requests from the registry and returns a response.
    // Other code: -1=error; 0=no response; 1=connection closed, exit main loop
    public String handleRequest(Iteration3Solution data, Socket socket, String request) throws IOException {
        UserSettings settings = data.getSettings();
        NetworkHandler network_handler = data.getNetworkHandler();
        switch (request) {
            case "get team name":
                return settings.team_name + "\n";
            case "get code":
                String language = "Java";
                String newline = "\n";
                String code = genSrcCodeRes();
                String end_of_code = "...";

                return language + newline + code + newline + end_of_code + newline;
            case "receive peers":
                return "0";
            case "get report":
                return generateReport(data);
            case "get location":
                if (settings.running_on_lan == true) {
                    return "127.0.0.1" + ":" + data.getOutgoingUDP().getLocalPort() + "\n";
                } else {
                    return network_handler.getExternalIP() + ":" + data.getOutgoingUDP().getLocalPort() + "\n";
                }
            case "close":
                network_handler.closeSocket(socket);
                return "1";
        }
        return "-1";
    }

    // Assuming that src code is less than 2 million chars.
    private String genSrcCodeRes() {
        String sourceCode = "";

        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Iteration2Solution.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Peer.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/Source.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/GroupManagement.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/HandlePeerUpdate.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/ReturnSearch.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/SnippetHandler.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/SnippetLog.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/UDPMessage.java");
        sourceCode += readCode("/Users/vivid/Dev/Java/CPSC-559/Iteration-2/src/UDPMessageLog.java");

        return sourceCode;
    }

    // Generates report based on assignment specs
    private String generateReport(Iteration3Solution data) {
        Hashtable<Source, Vector<Peer>> listOfSources = data.getAllSources();
        Vector<UDPMessageLog> peers_received = data.getPeersReceived();
        Vector<UDPMessageLog> peers_sent = data.getPeersSent();
        Vector<SnippetLog> all_snippets = data.getAllSnippets();

        int numOfSources = listOfSources.size();
        int totalNumOfPeers = 0;
        String peer_list_sources = "";
        String peer_list = "";
        String peers_recd = "";
        String peers_sent_str = "";
        String snip_list = "";

        for (Map.Entry<Source, Vector<Peer>> e : listOfSources.entrySet()) {
            Source source = e.getKey();
            String sourceLocation = source.getPeer().toString() + "\n";
            Vector<Peer> listOfPeers = e.getValue();
            String peers = "";
            for (Peer p : listOfPeers) {
                String peer_string = p.toString() + "\n";
                peers += peer_string;
                peer_list += peer_string;
                totalNumOfPeers++;
            }
            // adds one source
            peer_list_sources += sourceLocation + source.getTime() + "\n" + listOfPeers.size() + "\n" + peers;
        }

        peers_recd += peers_received.size() + "\n";
        for (UDPMessageLog m : peers_received) {
            try {
                peers_recd += m.getMsgOrigin().toString() + " " + m.getTransmittedPeer().toString() + " "
                        + m.getTimeStamp() + "\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        peers_sent_str += peers_sent.size() + "\n";
        for (UDPMessageLog m : peers_sent) {
            try {
                peers_sent_str += m.getMsgOrigin().toString() + " " + m.getTransmittedPeer().toString() + " "
                        + m.getTimeStamp() + "\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        snip_list += all_snippets.size() + "\n";
        for (SnippetLog sl : all_snippets) {
            snip_list += sl.getTimeStamp() + " " + sl.getContent() + " " + sl.getSourcePeer().toString() + "\n";
        }

        return totalNumOfPeers + "\n" + peer_list + numOfSources + "\n" +
                peer_list_sources + peers_recd + peers_sent_str + snip_list;
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

    
}

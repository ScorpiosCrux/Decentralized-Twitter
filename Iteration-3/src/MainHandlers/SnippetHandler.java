package MainHandlers;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */
//This class handles the incoming and outgoing snippets


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.SnippetLog;
import Main.HelperDataClasses.Source;
import Main.HelperDataClasses.SourceList;
import Main.HelperDataClasses.UDPMessage;
import Settings.UserSettings;

public class SnippetHandler extends Thread{
	private UserSettings settings;
	private NetworkHandler network_handler;
	private PeerCommHandler parent; 

	private Thread t;
	private String threadName;
	private DatagramSocket outgoingSocket;
	private SourceList all_sources;
    private Vector<SnippetLog> all_snippets;

	private Peer ourselves;
	private boolean stop = false;

	
	public SnippetHandler(UserSettings settings, NetworkHandler network_handler, PeerCommHandler parent){
		this.settings = settings;
		this.network_handler = network_handler;
		this.parent = parent;

		this.all_snippets = parent.getAllSnippets();

		this.threadName = "Snippet Handler";
		this.all_sources = parent.getAllSources();
		this.outgoingSocket = network_handler.getOutGoingUDP();

		this.ourselves = new Peer(network_handler.getExternalIP(), settings.client_port);
		
		System.out.println("Snippet Handler Thread Created!");
	}
	
	public void run() {
		System.out.println("Tweet your thoughts: ");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String content;
        while (stop != true) {
            try {
                
                if (!br.ready()) {
                	Thread.sleep(500);
                	continue;
                }else
                	content = br.readLine();
                
				ourselves.incrementTimeStamp();
				broadcast(content);
				System.out.println("Tweet has been tweeted! \nTweet your thoughts: ");
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
       
		System.out.println("Thread " + threadName + " exiting");
	}
	
	public void start() {
		System.out.println("Starting " + threadName);
		if (t == null) {
			this.t = new Thread(this, threadName);
			this.t.start();
		}
	}
	
	public void setStop() {
		this.stop = true;
	}

	// Function that broadcasts to all active peers that we know about
	private void broadcast(String content) {
		Vector<Source> source_list = all_sources.getSources();
		for (Source s : source_list){
			Vector<Peer> active_peers = s.getActivePeers();
			for (Peer p : active_peers){
				sendUDPMessage(p.getIP(), p.getPort(), content);
			}
		}
	}
	
	//send the snippet to peer
	private void sendUDPMessage(String peer_ip, int peer_port, String content) {
		try {
			byte[] buffer = new byte[1024];
			InetAddress address = InetAddress.getByName(peer_ip);
			
			String data = "snip" + ourselves.getTimeStamp() + " " + content;
			buffer = data.getBytes();
			DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, peer_port);
			
			outgoingSocket.send(response);
			all_snippets.add(new SnippetLog(ourselves.getTimeStamp(), content, ourselves));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to sendUDPMessage (SnippetHandler): " + peer_ip + ":" + peer_port);
		}

	}
	
	//handle the incoming snippet, updating our timestamp and also adding it to our logs
	public void handleIncomingSnip(UDPMessage message_pack) {
		try {
			String message = message_pack.getMessage();

			
			message = message.substring(4, message.length());
			String[] message_split = message.split(" ", 2);
			int received_timestamp = Integer.parseInt(message_split[0]);
			String content = message_split[1];
			       
	        ourselves.setMaxTimeStamp(received_timestamp);
			
			System.out.println("Message has been sent from: " + message_pack.getSourcePeer().toString() + ". They sent: " + content);
			//this.all_snippets.add(new SnippetLog(p.getTimeStamp(), content, sourcePeer));
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to parse incoming snip: " + message_pack.getMessage());
		}
	}
	
	


}

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
import Main.HelperDataClasses.ReturnSearch;
import Main.HelperDataClasses.SnippetLog;
import Main.HelperDataClasses.Source;
import Settings.UserSettings;

public class SnippetHandler extends Thread{
	private UserSettings settings;
	private NetworkHandler network_handler;
	private PeerCommHandler parent; 

	private Thread t;
	private String threadName;
	private DatagramSocket outgoingSocket;
	private Hashtable<Source, Vector<Peer>> listOfSources = new Hashtable<Source, Vector<Peer>>();


	private Vector<SnippetLog> allSnippets = new Vector<SnippetLog>();
	private String public_ip;
	private int port;
	private boolean stop = false;

	
	public SnippetHandler(UserSettings settings, NetworkHandler network_handler, PeerCommHandler parent){
		this.settings = settings;
		this.network_handler = network_handler;
		this.parent = parent;

		this.threadName = "Snippet Handler";
		this.listOfSources = parent.getAllSources();
		this.outgoingSocket = network_handler.getOutGoingUDP();
		this.public_ip = network_handler.getExternalIP();
		this.port = settings.client_port;
		
		System.out.println("Snippet Handler Thread Created!");
	}
	
	public void run() {
		// TODO: Duplicate Code
		System.out.println("Tweet your thoughts: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String content;
		//Inspired from a discord message in the 559 server
        while (stop != true) {
            try {
                
                if (!br.ready()) {
                	Thread.sleep(500);
                	continue;
                }else
                	content = br.readLine();
                
                
                Peer ourselves = new Peer(public_ip, port, null);
                ReturnSearch location = findPeer(ourselves);
                
                Peer p = listOfSources.get(location.getSource()).get(location.getIteration());
                p.setTimeStamp(p.getTimeStamp()+1);
            
				broadcast(content, p);
				System.out.println("Tweet has been tweeted! \nTweet your throughts: ");
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

	//broadcast the message to all peers while logging it
	private void broadcast(String content, Peer ourselves) {
		
		for (Map.Entry<Source, Vector<Peer>> s : listOfSources.entrySet()) {
			Vector<Peer> listOfPeers = s.getValue();
			for (Peer p : listOfPeers) {
				if (p.isActive() && !p.equals(ourselves)) {
					
					sendUDPMessage(p, content, ourselves);
				}
			}
		}
	}
	
	//send the snippet to peer
	private void sendUDPMessage(Peer peer, String content, Peer ourselves) {
		try {
			String ip = peer.getIP();
			int port = peer.getPort();
			
			byte[] buffer = new byte[1024];
			InetAddress address = InetAddress.getByName(ip);
			
			String data = "snip" + ourselves.getTimeStamp() + " " + content;
			buffer = data.getBytes();
			DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);
			
			outgoingSocket.send(response);
			allSnippets.add(new SnippetLog(ourselves.getTimeStamp(), content, ourselves));
			
			//System.out.println("Broadcast to: " + peer.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Unable to sendUDPMessage (SnippetHandler): " + peer.toString());
		}

	}
	
	//handle the incoming snippet, updating our timestamp and also adding it to our logs
	public void handleIncomingSnip(String message, Peer sourcePeer) {
		try {
			
			message = message.substring(4, message.length());
			String[] message_split = message.split(" ", 2);
			int received_timestamp = Integer.parseInt(message_split[0]);
			String content = message_split[1];
			
			Peer ourselves = new Peer(public_ip, port, null);	        
	        ReturnSearch location = findPeer(ourselves);
	        Peer p = listOfSources.get(location.getSource()).get(location.getIteration());
	        int max = Math.max(p.getTimeStamp(), received_timestamp) + 1;
			p.setTimeStamp(max);
			
			System.out.println("Message has been sent from: " + sourcePeer.toString() + ". They sent: " + content);
			this.allSnippets.add(new SnippetLog(p.getTimeStamp(), content, sourcePeer));
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to parse incoming snip: " + message);
		}
	}
	
	//this finds the peer in our datastructure
	private ReturnSearch findPeer(Peer peer){
		for (Map.Entry<Source, Vector<Peer>> s : listOfSources.entrySet()) {
			Vector<Peer> listOfPeers = s.getValue();
			for (int i = 0; i < listOfPeers.size(); i++) 
				if (listOfPeers.get(i).equals(peer)) 
					return new ReturnSearch(s.getKey(), i);
		}
		return new ReturnSearch(null, -1);
	}

}

package Main;
/*
 * Author: Tyler Chen
 * UCID: 30066806
 * Iteration 3
 * CPSC 559
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import MainHandlers.RequestHandler;
import MainHandlers.NetworkHandler;
import MainHandlers.PeerCommHandler;
import MainHandlers.RegistryHandler;
import Settings.UserSettings;
import Testing.PrintHandler;

public class Iteration3Solution {

	// Handlers
	private RegistryHandler registry_handler;
	private NetworkHandler network_handler;
	private PrintHandler print_handler;
	private RequestHandler request_handler;

	private UserSettings settings;


	private Hashtable<Source, Vector<Peer>> listOfSources = new Hashtable<Source, Vector<Peer>>();
	private Vector<UDPMessageLog> peersSent = new Vector<UDPMessageLog>();
	private Vector<UDPMessageLog> peersReceived = new Vector<UDPMessageLog>();
	private Vector<SnippetLog> allSnippets = new Vector<SnippetLog>();


	public Iteration3Solution(UserSettings settings) {
		this.settings = settings;
		try {
			this.network_handler = new NetworkHandler(settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.print_handler = new PrintHandler();
		this.request_handler = new RequestHandler(settings, this);

		// Should be last
		this.registry_handler = new RegistryHandler(settings, this);


		// initial connection to the registry
		try {
			registry_handler.start(settings.client_port);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}

		// communication with the peer
		PeerCommHandler peer_comm_handler = new PeerCommHandler(settings, network_handler);
		peer_comm_handler.start();

/* 		// final communicatino with registry
		try {
			client.stop();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} */
	}

	public UserSettings getSettings() {
		return this.settings;
	}

	public NetworkHandler getNetworkHandler() {
		return this.network_handler;
	}

	public PrintHandler getPrintHandler() {
		return this.print_handler;
	}

	public Vector<UDPMessageLog> getPeersSent() {
		return this.peersSent;
	}

	public Vector<UDPMessageLog> getPeersReceived() {
		return this.peersReceived;
	}

	public Vector<SnippetLog> getAllSnippets() {
		return this.allSnippets;
	}


	public Hashtable<Source, Vector<Peer>> getAllSources() {
		return this.listOfSources;
	}

	



	

	

	



}

package MainHandlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.UDPMessage;
import Settings.UserSettings;

		// this.peer_socket = network_handler.createUDPSocket(this.settings.client_port);
		// this.externalIP = network_handler.getExternalIP();

public class NetworkHandler {

    private DatagramSocket outgoing_udp;

    public NetworkHandler(UserSettings settings) throws IOException{
        this.outgoing_udp = createUDPSocket(settings.client_port);
    }

    public DatagramSocket getOutGoingUDP(){
        return outgoing_udp;
    }

    // Gets the IP address from the socket
    public String getIP(Socket socket) {
        return socket.getInetAddress().getHostAddress();
    }

    // -1 to let the OS choose a port
    public DatagramSocket createUDPSocket(int port) throws SocketException {
        if (port == -1)
            return new DatagramSocket();
        else
            return new DatagramSocket(port);
    }

    // Gets the externalIP for broadcasting
    public String getExternalIP() {
        // https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } catch (IOException e) {
            System.out.println("Error getting External IP!");
            e.printStackTrace();
            return "Error!";
        }
    }


    // Creates a socket with ip and port.
	// Returns the socket
	public Socket createSocket(String ip, int port) throws UnknownHostException, IOException {
		Socket socket = new Socket(ip, port);
		return socket;
	}

	// Closes the socket
	public void closeSocket(Socket socket) throws IOException {
		socket.close();
	}

    // Writes to socket with message then flushes the message to the stream.
	public void writeSocket(Socket socket, String message) throws IOException {
		// TODO: Convert message to Unicode characters
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		writer.write(message);
		writer.flush();
	}

    // Received UDP messages and returns a UDPMessage with information about the
	// source and the content itself
	public UDPMessage receiveUDPMsg() {
		// System.out.println("Waiting for UDP message");
		try {
			DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
			outgoing_udp.receive(packet);
			Peer sourcePeer = new Peer(packet.getAddress().getHostAddress(), packet.getPort());
			String message = new String(packet.getData(), 0, packet.getLength());
			return new UDPMessage(message, sourcePeer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    
}

package MainHandlers;

/* Imports */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

import Main.PeerSoftware;
import Main.HelperDataClasses.Peer;
import Main.HelperDataClasses.UDPMessagePack;
import Main.PeerSoftware.Settings;

/* 
 * This class contains all functions related to networks.
 */
public class NetworkHandler {

    private PeerSoftware ps;
    private DatagramSocket outgoing_udp_socket; // UDP Socket for sending data to peers
    private String external_ip;

    /* Constructor */
    public NetworkHandler(PeerSoftware ps) throws IOException {
        this.ps = ps;
        this.outgoing_udp_socket = createUDPSocket(Settings.CLIENT_PORT);
        this.external_ip = findExternalIP();

        System.out.println("SYSTEM: Network Handler Initialized! External IP: " + external_ip);
    }

    /*
     * Creates a UDP Socket
     * Params:
     * port - the port number. Specify -1 to let the OS choose a value.
     * 
     */
    public DatagramSocket createUDPSocket(int port) throws SocketException {
        if (port == -1)
            return new DatagramSocket();
        else
            return new DatagramSocket(port);
    }

    /*
     * Finds the external IP address of the system
     * 
     * Source :
     * https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-
     * in-java
     * 
     */
    public String findExternalIP() {
        try {
            URL ip_stream = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(ip_stream.openStream()));
            String ip = in.readLine();
            return ip;
        } catch (IOException e) {
            System.out.println("Error getting External IP!");
            e.printStackTrace();
            return "Error!";
        }
    }

    /* ===================== TCP SOCKETS ===================== */

    /* Creates a socket compatible with TCP/IP */
    public Socket createSocket(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            return socket;
        } catch (Exception e) {
            System.out.println("SYSTEM: Unable to create socket!");
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    /* Closes the socket */
    public void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("SYSTEM: Unable to create socket!");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /* Sends message to TCP/IP Socket. Flushes stream after. */
    public void send(Socket socket, String message) throws IOException {
        // TODO: Convert message to Unicode characters
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.flush();
    }

    /*
     * Sends a UDP Message
     * Params:
     * ip - destination ip
     * port - destination port
     * message - payload
     */
    public void send(String ip, int port, String message) {
        try {
            byte[] buffer = new byte[1024];
            buffer = message.getBytes();
            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, address, port);
            outgoing_udp_socket.send(response);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to send UDP message!");
        }

    }

    /* ===================== (end) TCP SOCKETS ===================== */

    // Received UDP messages and returns a UDPMessage with information about the
    // source and the content itself
    // TODO: MOVE THIS
    public UDPMessagePack receiveUDPMessage() {
        // System.out.println("Waiting for UDP message");
        try {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            outgoing_udp_socket.receive(packet);
            Peer sourcePeer = new Peer(packet.getAddress().getHostAddress(), packet.getPort());
            String message = new String(packet.getData(), 0, packet.getLength());
            return new UDPMessagePack(message, sourcePeer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ===================== GETTERS ===================== */

    /* Returns this systems UDP socket */
    public DatagramSocket getOutgoingUDPSocket() {
        return outgoing_udp_socket;
    }

    public String getExternalIP() {
        return this.external_ip;
    }

    /* Gets the IP address from the incoming socket */
    public String getSocketIP(Socket socket) {
        return socket.getInetAddress().getHostAddress();
    }

    /* ===================== (END) GETTERS ===================== */

}

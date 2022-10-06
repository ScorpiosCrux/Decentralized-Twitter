package Network;

import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

public class NetworkHandler {
    
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
}

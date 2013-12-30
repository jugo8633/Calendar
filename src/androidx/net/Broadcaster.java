//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Broadcaster {
	private DatagramSocket socket;
	private DatagramPacket packet;

	public Broadcaster(InetAddress addr, int port) throws IOException {
		socket = new DatagramSocket(port, addr);
	}

  public void setBroadcast(boolean on) throws SocketException {
    socket.setBroadcast(on);
  }

  public void connect(InetAddress host, int port) {
    socket.connect(host, port);
  }

  public void sendBytes(InetAddress host, int port, byte[] data, int offset, int length) throws IOException {
		packet = new DatagramPacket(data, offset, length, host, port);
		socket.send(packet);
	}

	public void sendString(InetAddress host, int port, String msg) throws IOException {
		byte[] data = msg.getBytes();
		sendBytes(host, port, data, 0, data.length);
	}
}


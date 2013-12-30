//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import java.io.IOException;
import java.net.InetAddress;

public class HostBroadcaster {
	private Broadcaster broadcaster;
	private InetAddress host;
	private int port;

	public HostBroadcaster(Broadcaster broadcaster, InetAddress host, int port) {
		this.broadcaster = broadcaster;
		this.host = host;
		this.port = port;
	}

	public void sendBytes(byte[] data, int offset, int length) throws IOException {
		broadcaster.sendBytes(host, port, data, offset, length);
	}

	public void sendString(String msg) throws IOException {
		byte[] data = msg.getBytes();
		sendBytes(data, 0, data.length);
	}

}


//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import android.os.Handler;
import java.io.IOException;
import java.net.*;
import androidx.LogX;

public class Multicaster {
	private MulticastSocket socket;
  private DatagramPacket in_packet;
  private byte[] in_buffer;
  private InetAddress group;

  public static interface Listener {
     public void onPacketReceived(DatagramPacket packet);
  };

  private Listener listener;
  private Handler handler;
  private Thread thread;

  /**
   * @param localInterface the local interface to bind the socket to (may be null)
   * @param localPort the local port to bind to
   *
   * @throws IOException
   */
  public Multicaster(InetAddress localInterface, int localPort, Listener listener) throws IOException {
    if (localInterface == null) {
      socket = new MulticastSocket(localPort);      
    } else {
      socket = new MulticastSocket(new InetSocketAddress(localInterface, localPort));
    }
//    socket = new MulticastSocket();
//    socket.setBroadcast(true);
    this.listener = listener;
    handler = new Handler();
    in_buffer = new byte[512];
    in_packet = new DatagramPacket(in_buffer, in_buffer.length);
  }

  /**
   * Join a particular multicast group
   * @param group group address in the range 224.0.0.1 to 239.255.255.255 for example
   * InetAddress group = InetAddress.getByName("230.0.0.1");
   * @throws IOException
   */
  public void joinGroup(InetAddress group) throws IOException {
    if (this.group != null) {
      socket.leaveGroup(this.group);
    }
    socket.joinGroup(group);
    this.group = group;
  }

  public synchronized void start() {
    if (isRunning()) {
      throw new IllegalStateException("Already running!");
    }
    thread = new Thread(receiver, "Multicast receiver");
    thread.setDaemon(true);
    thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
  }

  public synchronized void stop() {
    thread = null;
  }

  public synchronized void close() {
    if (socket == null) {
      throw new IllegalStateException("already closed!");
    }
    stop();
    socket.close();
    socket = null;
  }

  public boolean isClosed() {
    return (socket != null);
  }

  private Runnable receiver = new Runnable() {
    public void run() {
      while (isRunning()) {
        receivePackets();
      }
    }
  };

  public boolean isRunning() {
    return (thread != null);
  }

  private void receivePackets() {
    try {
      receivePacket();
    } catch (IOException e) {
      LogX.e("Problem receiving packets on port " + socket.getLocalPort(), e);
      stop();
    }
  }

  private Runnable packetSender = new Runnable() {
    public void run() {
      listener.onPacketReceived(in_packet);
    }
  };

  private void receivePacket() throws IOException {
    // receive our packet
//    LogX.i("waiting for a multicast packet");
    socket.receive(in_packet);
//    LogX.i("multicast packet has arrived!");
    // send it to the listener on the main thread
    handler.post(packetSender);
  }

}
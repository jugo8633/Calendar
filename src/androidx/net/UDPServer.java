//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import androidx.LogX;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.io.IOException;
import android.os.Handler;

public class UDPServer {
  private DatagramSocket socket;
  private DatagramPacket packet;
  private Thread thread;
  private byte[] buffer;
  private Handler handler;

  public static interface Listener {
     public void onPacketReceived(DatagramPacket packet);
  };

  private Listener listener;

  /**
   * Create a UDPServer
   *
   * @param this_host if null, we can listen for broadcasts, otherwise this can
   * only listen for packets directed at the given address.
   * 
   * @param port
   * @param listener
   * @throws SocketException
   */
  public UDPServer(InetAddress this_host, int port, Listener listener) throws SocketException {
    this.listener = listener;
    buffer = new byte[512];
    if (this_host == null) {
      socket = new DatagramSocket(port);
    } else {
      socket = new DatagramSocket(port, this_host);
    }
    packet = new DatagramPacket(buffer, buffer.length);
    handler = new Handler();
  }

  public synchronized void start() {
    if (isRunning()) {
      throw new IllegalStateException("Already running!");
    }
    thread = new Thread(receiver, "UDP receiver");
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
      listener.onPacketReceived(packet);
    }
  };

  private void receivePacket() throws IOException {
    // receive our packet
    LogX.i("waiting for a UDP packet");
    socket.receive(packet);
    LogX.i("UDP packet has arrived!");
    // send it to the listener on the main thread
    handler.post(packetSender);
  }
}

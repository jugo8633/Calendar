//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import java.net.InetAddress;
import java.net.UnknownHostException;
import androidx.LogX;

public class NetUtil {
  private NetUtil() {
  }

  /**
   * Get the local ip address of the WIFI we are connected to
   * 
   * @param context
   * @return
   */
  public static InetAddress getLocalWifiAddress(Context context) {
    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo connectionInfo = wifiManager.getConnectionInfo();

    if (!wifiManager.isWifiEnabled()) {
      LogX.i("wifi not enabled");
      return null;
    }
    int ip = connectionInfo.getIpAddress();
    if (ip == 0) {
      LogX.e("invalid ip address");
      return null;
    }

    byte[] byteaddr = new byte[] { (byte)(ip & 0xff), (byte)(ip >> 8 & 0xff), (byte)(ip >> 16 & 0xff), (byte)(ip >> 24 & 0xff) };
    try {
      return InetAddress.getByAddress(byteaddr);
    } catch (UnknownHostException thisShouldNeverHappen) {
      throw new RuntimeException("local ip address not recognized!", thisShouldNeverHappen);
    }
  }

  /**
   * Given an input address like 192.168.1.65 returns a broadcast form of that
   * address like 192.168.1.255
   *
   * @param address
   * @return
   */
  public static InetAddress getBroadcastAddress(InetAddress address) {
    byte[] addr = address.getAddress();
    byte[] bcaddr = new byte[] {addr[0], addr[1], addr[2], (byte)255};
    try {
      return InetAddress.getByAddress(bcaddr);
    } catch (UnknownHostException thisShouldNeverHappen) {
      throw new RuntimeException("broadcast ip address not recognized!", thisShouldNeverHappen);
    }
  }
}

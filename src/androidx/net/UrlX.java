//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlX {
	private UrlX() {
	}
	
  /**
   * Do a GET request and retrieve up to maxBytes bytes
   * @param url
   * @param maxBytes
   * @return
   * @throws IOException
   */
  public static byte[] doGetAndReturnBytes(URL url, int maxBytes) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    InputStream istr = null;
    try {
      int rc = conn.getResponseCode();
      if (rc != 200) {
        throw new IOException("code " + rc + " '" + conn.getResponseMessage() + "'");
      }
      istr = new BufferedInputStream(conn.getInputStream(), 512);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      copy(istr, baos, maxBytes);
      return baos.toByteArray();      
    } finally {
      if (istr != null) {
        istr.close();
      }
    }
  }
  
  private static int copy(InputStream in, OutputStream out, int maxBytes) throws IOException {
    byte[] buf = new byte[512];
    int bytesRead = 1;
    int totalBytes = 0;
    while (bytesRead > 0) {
      bytesRead = in.read(buf, 0, Math.min(512, maxBytes - totalBytes));
      if (bytesRead > 0) {
        out.write(buf, 0, bytesRead);
        totalBytes += bytesRead;
      }
    }
    return totalBytes;
  }
  
	
}

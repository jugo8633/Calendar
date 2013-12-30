//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;

public class URLGetter {
  private URL url;
  private HostnameVerifier verifier;
  private boolean followRedirects;
  private boolean closeConnection;
  private Map<String, List<String>> request_headers;

  public URLGetter(URL url, HostnameVerifier verifier) {
    this.url = url;
    this.verifier = verifier;
    followRedirects = true;
    closeConnection = true;
  }

  public boolean getCloseConnection() {
    return closeConnection;
  }

  public void setCloseConnection(boolean closeConnection) {
    this.closeConnection = closeConnection;
  }

  public boolean getFollowRedirects() {
    return followRedirects;
  }

  public void addHeader(String header, String value) {
    if (request_headers == null) {
      request_headers = new HashMap<String, List<String>>();
    }
    List<String> values = request_headers.get(header);
    if (values == null) {
      values = new ArrayList<String>(2);
      request_headers.put(header, values);
    }
    values.add(value);
  }

  public void clearRequestHeaders() {
    if (request_headers != null) {
      request_headers.clear();
    }
  }

  public void addSecureCookie(String name, String value, String path) {
    addHeader("Cookie", name + "=" + value + "; path=" + path + "; secure");
  }

  public void setFollowRedirects(boolean followRedirects) {
    this.followRedirects = followRedirects;
  }

  public void doGetAndDiscardResponse() throws IOException {
    URLResponse response = doGet();
    InputStream istr = response.getInputStream();
    if (istr != null) {
      istr.close();
    }
  }

  /**
   * Do a GET with no query params
   * @return
   * @throws IOException
   */
  public URLResponse doGet() throws IOException {
    return doGet(url);
  }

  /**
   * Do a GET with the supplied params as query parameters
   * @param params
   * @return
   * @throws IOException
   */
  public URLResponse doGet(Map<String, String> params) throws IOException {
    if (params == null || params.isEmpty()) {
      return doGet(url);
    }
    String qry = UrlUtil.buildQuery(params);
    return doGet(new URL(url.toString() + "?" + qry));
  }

  private URLResponse doGet(URL url) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    if (conn instanceof HttpsURLConnection) {
      HttpsURLConnection secureConn = (HttpsURLConnection) conn;
      if (verifier != null) {
        secureConn.setHostnameVerifier(verifier);
      }
    }
    conn.setRequestMethod("GET");
    conn.setInstanceFollowRedirects(followRedirects);
    boolean ignoreConnection = false;
    if (request_headers != null) {
      for (String header : request_headers.keySet()) {
        List<String> hvalues = request_headers.get(header);
        for (String hvalue : hvalues) {
          conn.addRequestProperty(header, hvalue);
          if (header.equalsIgnoreCase("Connection")) {
            ignoreConnection = true;
          }
        }
      }
    }
    if (!ignoreConnection && closeConnection) {
      conn.addRequestProperty("Connection", "Close");
    }
    conn.connect();
    Map<String, List<String>> headers = conn.getHeaderFields();
    int rc = conn.getResponseCode();
    if (rc != 200) {
      return new URLResponse(url, headers, conn.getResponseMessage(), rc);
    }
    return new URLResponse(url, headers, conn.getInputStream());
  }


  private static String toString(BufferedReader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append('\n');
    }
    return sb.toString();
  }

}

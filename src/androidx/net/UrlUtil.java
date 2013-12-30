//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * A collection of utility methods to manipulate URLs.
 *
 * @author Luke Liu
 */
public class UrlUtil {
  private static final int REDIRECT_RESPONSE_CODE = 302;
  private static final int MOVED_PERMANENTLY_RESPONSE_CODE = 301;
  public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

  private UrlUtil() {
  }

  public static URL getRedirectedUrl(URL url) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setInstanceFollowRedirects(false);
    conn.addRequestProperty("Connection", "close");
    int rc = conn.getResponseCode();
    if (rc != REDIRECT_RESPONSE_CODE && rc != MOVED_PERMANENTLY_RESPONSE_CODE) {
      throw new IOException("code " + rc + " '" + conn.getResponseMessage() + "'");
    }
    String location = conn.getHeaderField("Location");
    if (location == null) {
      throw new IOException("No 'Location' header found");
    }
    return new URL(location);
  }
  

  private static void copy(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[512];
    int bytesRead = 1;
    while (bytesRead > 0) {
      bytesRead = in.read(buf);
      if (bytesRead > 0) {
        out.write(buf, 0, bytesRead);
      }
    }
  }

  public static String doFormPost(URL url, InputStream stuffToPost) throws IOException {
    return doPost(url, APPLICATION_X_WWW_FORM_URLENCODED, stuffToPost);
  }

  public static String doPost(URL url, String contentType, InputStream stuffToPost) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    if (conn instanceof HttpsURLConnection) {
      HttpsURLConnection secureConn = (HttpsURLConnection) conn;
      secureConn.setHostnameVerifier(new HostnameVerifier() {
        public boolean verify(String host, SSLSession sslSession) {
          return true;
        }
      });
    }
//    if (conn instanceof Htt)
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", contentType);
    OutputStream ostr = null;
    try {
      ostr = conn.getOutputStream();
      copy(stuffToPost, ostr);
    } finally {
      if (ostr != null) {
        ostr.close();
      }
    }

    conn.connect();
    BufferedReader reader = null;
    try {
      int rc = conn.getResponseCode();
      if (rc != 200) {
        throw new IOException("code " + rc + " '" + conn.getResponseMessage() + "'");
      }
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 512);
      String response = toString(reader);
      return response;
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  public static String doPost(URL url, String contentType, String input) throws IOException {
    return doPost(url, contentType, new ByteArrayInputStream(input.getBytes()));
  }


  public static String doGet(URL url) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader reader = null;
    try {
      int rc = conn.getResponseCode();
      if (rc != 200) {
        throw new IOException("code " + rc + " '" + conn.getResponseMessage() + "'");
      }
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 512);
      return toString(reader);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  private static String buildUrl(String baseurl, Map<String, String> params) throws IOException {
    if (params.isEmpty()) {
      return baseurl;
    } else {
      return baseurl + "?" + buildQuery(params);
    }
  }

  public static String doFormPost(URL url, Map<String, String> params) throws IOException {
    String qry = buildQuery(params);
    return doPost(url, APPLICATION_X_WWW_FORM_URLENCODED, qry);
  }

  public static String buildQuery(Map<String, String> params) {
    StringBuilder sb = null;
    for (String key : params.keySet()) {
      String value = params.get(key);
      if (sb == null) {
        sb = new StringBuilder();
        sb.append(escape(key)).append('=').append(escape(value));
      } else {
        sb.append("&").append(escape(key)).append('=').append(escape(value));
      }
    }
    return sb.toString();
  }

  public static String doGet(String baseurl, Map<String, String> params) throws IOException {
    return doGet(new URL(buildUrl(baseurl, params)));
  }

  private static String escape(String s) {
    return URLEncoder.encode(s);
  }

  public static String getXML(URL url) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      return toString(reader);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  private static String toString(BufferedReader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    while ( (line = reader.readLine()) != null) {
      sb.append(line).append('\n');
    }
    return sb.toString();
  }

  private static String readString(BufferedReader reader) throws IOException {
    String line;
    StringBuilder sb = new StringBuilder();
    while ( (line = reader.readLine()) != null) {
        sb.append(line);
    }
    return sb.toString();
  }


  private static Map<String, String> getParams(BufferedReader reader) throws IOException {
    Map<String, String> params = new HashMap<String, String>();
    String line;
    int eq;
    while ( (line = reader.readLine()) != null) {
      eq = line.indexOf('=');
      if (eq > 0) {
        String key = line.substring(0, eq);
        String value = line.substring(eq + 1);
        params.put(key, value);
      }
    }
    return params;
  }

}


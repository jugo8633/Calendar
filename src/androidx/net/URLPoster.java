//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;

import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;
import com.hp.ij.common.ccphttpclient.CCPHttpClient;

/**
 * Disabling Certificate Validation in an HTTPS Connection
 * 
 * @author Luke Liu
 */
public class URLPoster {
  private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
  private URL url;
  private HostnameVerifier verifier;
  private Context context;
  private final int INT_GDATA_VERSION = 2; 
  private int intConnectionTimeout = 20 * 1000;
  
  public URLPoster(URL url, HostnameVerifier verifier, Context context) {
    this.url = url;
    this.verifier = verifier;
    this.context = context;
  }

  public URLResponse postForm(Map<String, List<String>> request_headers, Map<String, String> params) throws IOException {
    String qry = UrlUtil.buildQuery(params);
    return doPost(request_headers, APPLICATION_X_WWW_FORM_URLENCODED, qry);
  }

  private URLResponse doPost(Map<String, List<String>> request_headers, String contentType, String input) throws IOException {
    byte[] bytes = input.getBytes();
    return doPost(request_headers, contentType, new ByteArrayInputStream(bytes), bytes.length);
  }

  private URLResponse doPost(Map<String, List<String>> request_headers, String contentType,
                             InputStream stuffToPost, int contentLength) throws IOException {
	try {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, new TrustManager[] { new MyTrustManager() },
				new SecureRandom());
		HttpsURLConnection
				.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection
				.setDefaultHostnameVerifier(new MyHostnameVerifier());
	} catch (Exception e) {
		LogX.i(e.toString());
	}

	// -----set using proxy or not-----
	String strHostname = androidx.net.Proxy.getHost(this.context);
	int intPort = androidx.net.Proxy.getPort(this.context);
	java.net.Proxy proxy = null;
	if(strHostname != null)	proxy = new java.net.Proxy(Proxy.Type.HTTP, (SocketAddress)new InetSocketAddress(strHostname, intPort));
	
	HttpsURLConnection conn = null;
	if( proxy != null)	conn = (HttpsURLConnection) url.openConnection(proxy);
	else	conn = (HttpsURLConnection) url.openConnection();
	// -----set using proxy or not-----
		 
    /*
     * Use the default truststore is <java-home>/lib/security/cacerts.
       
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
     
    if (conn instanceof HttpsURLConnection) {
        HttpsURLConnection secureConn = (HttpsURLConnection) conn;
        if (verifier != null) {
          secureConn.setHostnameVerifier(verifier);
        }
      }
    */
	conn.setConnectTimeout(intConnectionTimeout);
	conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setInstanceFollowRedirects(false);
    if (request_headers != null) {
      for (String header : request_headers.keySet()) {
        List<String> hvalues = request_headers.get(header);
        for (String hvalue : hvalues) {
          conn.addRequestProperty(header, hvalue);
        }
      }
    }
    conn.setRequestProperty("Content-Type", contentType);
    conn.setRequestProperty("GData-Version", Integer.toString(INT_GDATA_VERSION));
    if (contentLength > 0) {
      conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
    }
    OutputStream ostr = null;
    try {
      conn.connect();
      ostr = conn.getOutputStream();
      copy(stuffToPost, ostr);
    } catch(IOException ioException) {
    	LogX.d("Socket timeout");
    	throw new IOException(Integer.toString(CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT));
    } finally {
      if (ostr != null) {
        ostr.close();
      }
    }

    //conn.connect();
    Map<String, List<String>> headers = conn.getHeaderFields();
    int rc = conn.getResponseCode();
    if (rc != 200) {
    	if(rc >= 400 || rc < 500)
    		if(rc == 403)	rc = -37;
    		else	rc = CCPHttpClient.ERROR_RESPONSE_4XX; 
    	else	rc = CCPHttpClient.ERROR_RESPONSE_5XX; 
    		
    	return new URLResponse(url, headers, conn.getResponseMessage(), rc);
    }
    return new URLResponse(url, headers, conn.getInputStream());
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
  
	/**
	 * MyTrustManager
	 */
	private class MyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}
	
	/**
	 * MyHostnameVerifier
	 */
	private class MyHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

	}

}
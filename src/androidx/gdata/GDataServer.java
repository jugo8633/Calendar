//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;
import java.net.SocketException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import android.util.Log;
import androidx.xml.XMLUtil;
import androidx.LogX;
import com.hp.ij.common.ccphttpclient.CCPHttpClient;
import com.hp.ij.common.ccphttpclient.CCPHttpClientCallback;
import com.hp.ij.common.ccphttpclient.RequestInfo;
import com.hp.ij.common.ccphttpclient.CCPHttpClient.PostContentBuilder;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.HttpVersion;

/**
 * Include basic HTTP connection to get data, [GET, POST, PUT, DELETE]
 * 
 * @author Luke Liu
 *
 */
public abstract class GDataServer implements CCPHttpClientCallback {
	
  private String baseUrl;
  private String username;
  private String auth;

  /**
   * HttpResponse callback content.
   */
  private HttpResponse mHttpResponse;
  
  private CCPHttpClient ccpHttpClient;
  
  private Context context;
  
  protected GDataServer(String baseUrl, String username, String auth, Context context) {
    this.baseUrl = baseUrl;
    if (username.indexOf('@') == -1) {
      this.username = username + "@gmail.com";
    } else {
      this.username = username;
    }
    this.auth = auth;
    this.context = context;

    int intEventId = 1;
    int intTimeout = 20;
    CCPHttpClient.setProxyEnabled(false);
    ccpHttpClient = new CCPHttpClient();
    ccpHttpClient.initRequestEx(this, intEventId, intTimeout, this.context);
  }

  protected Node extractFeedNode(String result) throws IOException {
    return extractNode(result, "feed");
  }

  protected Node extractEntryNode(String result) throws IOException {
    return extractNode(result, "entry");
  }

  protected Node extractNode(String result, String nodeName) throws IOException {
    Document xml;
    try {
      xml = XMLUtil.stringToDocument(result);
    } catch (SAXException e) {
      throw new IOException(e.getMessage());
    }
    Node feedNode = XMLUtil.findNamedElementNode(xml, nodeName);
    if (feedNode == null) {
      throw new IOException("Unrecognized response: no <" + nodeName + "> element found");
    }
    return feedNode;
  }

  protected String doAuthorizedGet(URL url, Context contextService) throws IOException {
    return doGet(auth, url, contextService);
  }

  protected boolean doAuthorizedDelete(URL url) throws IOException {
	  return doDelete(auth, url, 200);
  }

  protected String getBaseUrl() {
    return baseUrl;
  }

  protected String getUsername() {
    return username;
  }

  private static String doGet(String auth, URL url, Context contextService) throws IOException {
	// -----set using proxy or not-----
	String strHostname = androidx.net.Proxy.getHost(contextService);
	int intPort = androidx.net.Proxy.getPort(contextService);
	java.net.Proxy proxy = null;
	if(strHostname != null)	proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, (java.net.SocketAddress)new java.net.InetSocketAddress(strHostname, intPort));
	
	HttpURLConnection conn = null;
	if( proxy != null)	conn = (HttpURLConnection) url.openConnection(proxy);
	else	conn = (HttpURLConnection) url.openConnection();
	// -----set using proxy or not-----
    
    int intTimeout = 20; 
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Authorization", "GoogleLogin auth=" + auth);
    conn.setConnectTimeout(intTimeout * 1000);
    BufferedReader reader = null;
    try {
      int rc = conn.getResponseCode();
      if (rc != 200) {
    	if(rc == -1)	rc = CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT;  
    	else if(rc >= 400 && rc < 500)	rc = CCPHttpClient.ERROR_RESPONSE_4XX;
    	else	rc = CCPHttpClient.ERROR_RESPONSE_5XX; 

        throw new IOException(Integer.toString(rc));
      } else {
          reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 512);
          return toString(reader);
      }
    } catch(SocketException socketException) {
    	LogX.d("Disabled network connection.");
    	throw new IOException(Integer.toString(CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT));
    } catch(IOException ioException) {
    	LogX.d("DoGet Exception Socket is not connected");
    	throw new IOException(Integer.toString(CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT));
    } catch(NullPointerException nullPointerException) {
    	LogX.d("DoGet exception invalid content");
    	throw new IOException(Integer.toString(CCPHttpClient.ERROR_RESPONSE_5XX));
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  protected boolean doDelete(String auth, URL url, int intExceptedResponse) throws IOException {
	  ccpHttpClient.removeHttpHeaderAll();
	  ccpHttpClient.setHttpHeader("If-Match", "*");
	  ccpHttpClient.setHttpHeader("Authorization", "GoogleLogin auth=" + auth);
	  ccpHttpClient.setHttpHeader("Content-Type", "application/atom+xml;charset=UTF-8");
	  int intPortNumber = 80;
	  long longHttpReturn = ccpHttpClient.startDelMethod(url.toString(), intPortNumber, null);
	  LogX.i(Long.toString(longHttpReturn));
	  waitForCallback();
	  
	  //intExceptedResponse
	  boolean boolReturn = false;
	  int intErrorCode = 0;
	  if(intExceptedResponse == mHttpResponse.getStatusLine().getStatusCode())	boolReturn = true;
	  else {
		  intErrorCode = Integer.parseInt(mHttpResponse.getHeaders("ErrorCode")[0].getValue());
		  switch(intErrorCode) {
		  case CCPHttpClient.ERROR_RESPONSE_4XX:
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_RESPONSE_4XX));
		  case CCPHttpClient.ERROR_RESPONSE_5XX: 	 
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_RESPONSE_5XX));
		  default: //CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT));
		  }
	  }
	  
	  return boolReturn;
  }

  protected String doAtomPost(URL url, String input) throws IOException {
    byte[] bytes = input.getBytes();
    return doAtomPost(url, new ByteArrayInputStream(bytes), bytes.length);
  }

  protected String doAtomPut(URL url, String input) throws IOException {
    byte[] bytes = input.getBytes();
    return doAtomPut(url, new ByteArrayInputStream(bytes), bytes.length);
  }

  protected String doAtomDelete(URL url, String input) throws IOException {
    byte[] bytes = input.getBytes();
    return doAtomDelete(url, new ByteArrayInputStream(bytes), bytes.length);
  }

  protected String doAtomPost(URL url, InputStream stuffToPost, int contentLength) throws IOException {
    return doAtomPost(url, stuffToPost, contentLength, null, 201);
  }

  protected String doAtomDelete(URL url, InputStream stuffToPost, int contentLength) throws IOException {
    return doAtomPost(url, stuffToPost, contentLength, "DELETE", 200);
  }


  protected String doAtomPut(URL url, InputStream stuffToPost, int contentLength) throws IOException {
    return doAtomPut(url, stuffToPost, contentLength, "PUT", 200);
  }
  
  protected String doAtomPost(URL url, InputStream stuffToPost, int contentLength
		  , String httpMethod, int intExpectedResponseCode) throws IOException {
	  ccpHttpClient.removeHttpHeaderAll();
	  ccpHttpClient.setHttpHeader("X-HTTP-Method-Override", httpMethod);
	  ccpHttpClient.setHttpHeader("Authorization", "GoogleLogin auth=" + auth);
	  ccpHttpClient.setHttpHeader("Content-Type", "application/atom+xml;charset=UTF-8");

	  // Set up post content
	  PostContentBuilder postContentBuilder =  ccpHttpClient.new PostContentBuilder();
	  try {
		  postContentBuilder.setPostContent(convertStreamToString(stuffToPost));
	  } catch (UnsupportedEncodingException e1) {
		  // TODO Auto-generated catch block
		  e1.printStackTrace();
	  }

	  // Get a token from Post connection 
	  int intPortNumber = 80;
	  long longHttpReturn = ccpHttpClient.startPostMethod(url.toString(), intPortNumber, postContentBuilder);
	  LogX.i(Long.toString(longHttpReturn));
	  // Wait for callback method finished.
	  waitForCallback();
	  
	  int intErrorCode = 0;
	  if(intExpectedResponseCode != mHttpResponse.getStatusLine().getStatusCode()) {
		  intErrorCode = Integer.parseInt(mHttpResponse.getHeaders("ErrorCode")[0].getValue());
		  switch(intErrorCode) {
		  case CCPHttpClient.ERROR_RESPONSE_4XX:
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_RESPONSE_4XX));
		  case CCPHttpClient.ERROR_RESPONSE_5XX: 	 
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_RESPONSE_5XX));
		  default: //CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT));
		  }
	  }

	  InputStream inputStream = mHttpResponse.getEntity().getContent();
	  return convertStreamToString(inputStream);
  }
  
  protected String doAtomPut(URL url, InputStream stuffToPost, int contentLength
		  , String httpMethod, int intExpectedResponseCode) throws IOException {
	  ccpHttpClient.removeHttpHeaderAll();
	  ccpHttpClient.setHttpHeader("X-HTTP-Method-Override", httpMethod);
	  ccpHttpClient.setHttpHeader("If-Match", "*");
	  ccpHttpClient.setHttpHeader("Authorization", "GoogleLogin auth=" + auth);
	  ccpHttpClient.setHttpHeader("Content-Type", "application/atom+xml;charset=UTF-8");

	  // Set up post content
	  PostContentBuilder postContentBuilder =  ccpHttpClient.new PostContentBuilder();
	  try {
		  postContentBuilder.setPostContent(convertStreamToString(stuffToPost));
	  } catch (UnsupportedEncodingException e1) {
		  // TODO Auto-generated catch block
		  e1.printStackTrace();
	  }

	  // Get a token from Post connection 
	  int intPortNumber = 80;
	  long longHttpReturn = ccpHttpClient.startPutMethod(url.toString(), intPortNumber, postContentBuilder);
	  LogX.i(Long.toString(longHttpReturn));
	  // Wait for callback method finished.
	  waitForCallback();
	  
	  int intErrorCode = 0;
	  if(intExpectedResponseCode != mHttpResponse.getStatusLine().getStatusCode()) {
		  intErrorCode = Integer.parseInt(mHttpResponse.getHeaders("ErrorCode")[0].getValue());
		  switch(intErrorCode) {
		  case CCPHttpClient.ERROR_RESPONSE_4XX:
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_RESPONSE_4XX));
		  case CCPHttpClient.ERROR_RESPONSE_5XX: 	 
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_RESPONSE_5XX));
		  default: //CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT
			  throw new IOException(Integer.toString(CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT));
		  }
	  }
	  
	  InputStream inputStream = mHttpResponse.getEntity().getContent();
	  return convertStreamToString(inputStream);
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

  private static String toString(BufferedReader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    String line;
    while ( (line = reader.readLine()) != null) {
      sb.append(line).append('\n');
    }
    return sb.toString();
  }
  
  public void onCompleted(CCPHttpClient cbObj, RequestInfo requestinfo,
          HttpResponse response) {
	  LogX.i("[onCompleted] contentLength=" + response.getEntity().getContentLength());
	  LogX.i("[onCompleted] methodIndex=" + requestinfo.getMethodIndex());
	  LogX.i("[onCompleted] " + response.getStatusLine().toString());

	  synchronized(this) {
		  mHttpResponse = response;
		  notify();
	  }
  }

  public void onError(RequestInfo requestInfo, HttpResponse response,
          int errorCode) {
	  switch(errorCode) {
	  case CCPHttpClient.ERROR_RESPONSE_4XX:
		  break;

	  case CCPHttpClient.ERROR_RESPONSE_5XX:
		  break;
		  
	  default:
		  response = new BasicHttpResponse(new HttpVersion(1, 1), 102, null);
		  break;
	  }
	  synchronized(this) {
		  mHttpResponse = response;
		  mHttpResponse.setHeader("ErrorCode", Integer.toString(errorCode));
		  notify();
	  }	  
  }

  public void onProgress(CCPHttpClient cbObj, RequestInfo requestinfo,
          long totalsize, long readsize, long currentsize) {
  }
  
  /** 
  * To convert the InputStream to String we use the BufferedReader.readLine() 
  * method. We iterate until the BufferedReader return null which means 
  * there's no more data to read. Each line will appended to a StringBuilder 
  * and returned as String. 
  */
  public static String convertStreamToString(InputStream inputStream) {
	  BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	  StringBuilder sb = new StringBuilder();

	  String line = null;
	  try {
		  while ((line = reader.readLine()) != null) {
			  sb.append(line + "\n");
		  }
	  } catch (IOException e) {
		  e.printStackTrace();
	  } finally {
		  try {
			  inputStream.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }

	  return sb.toString();
  }

  /**
   * Wait for HttpClient's onComplete callback.
   */
  private void waitForCallback() {
	  synchronized (this) {
		  try {
			  wait();
		  } catch (InterruptedException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
	  }
  }
  
}

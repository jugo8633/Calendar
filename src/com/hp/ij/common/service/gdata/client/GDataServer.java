//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import androidx.xml.XMLUtil;

/**
 * GDataServer provides HTTP requests of GET, POST, PUT and DELETE to implement REST-style web service, and the methods of 
 * extracting data from XML.  
 * 
 * @author Luke Liu
 *
 */
public abstract class GDataServer {
	  private String baseUrl;
	  private String username;
	  private String auth;

	  protected GDataServer(String baseUrl, String username, String auth) {
	    this.baseUrl = baseUrl;
	    if (username.indexOf('@') == -1) {
	      this.username = username + "@gmail.com";
	    } else {
	      this.username = username;
	    }
	    this.auth = auth;
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
	      throw new IOException("Unrecognized response: no <"+ nodeName +"> element found");
	    }
	    return feedNode;
	  }

	  protected String doAuthorizedGet(URL url) throws IOException {
	    return doGet(auth, url);
	  }

	  protected String getBaseUrl() {
	    return baseUrl;
	  }

	  protected String getUsername() {
	    return username;
	  }

	  private static String doGet(String auth, URL url) throws IOException {
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("GET");
	    conn.setRequestProperty("Authorization", "GoogleLogin auth=" + auth);
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
	    return doAtomPost(url, stuffToPost, contentLength, "PUT", 200);
	  }

	  protected String doAtomPost(URL url, InputStream stuffToPost, int contentLength
	      , String httpMethod, int expectedResponseCode) throws IOException {
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    if (httpMethod != null) {
	      conn.setRequestProperty("X-HTTP-Method-Override", httpMethod);
	    }
	    conn.setRequestProperty("Authorization", "GoogleLogin auth=" + auth);
	    conn.setRequestProperty("Content-Type", "application/atom+xml");
	    conn.setDoOutput(true);
	    if (contentLength > 0) {
	      conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
	    }
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
	      if (rc != expectedResponseCode) {
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
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append('\n');
	    }
	    return sb.toString();
	  }
	}

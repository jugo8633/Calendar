//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;

public class URLResponse {
  private URL url;
  private String errorMessage;
  private int resultCode;
  private Map<String, List<String>> headers;
  private Map<String, List<String>> uppercaseHeaders;
  private InputStream inputStream;

  URLResponse(URL url, Map<String, List<String>> headers, String errorMessage, int resultCode) {
    this.url = url;
    this.headers = headers;
    this.inputStream = null;
    this.errorMessage = errorMessage;
    this.resultCode = resultCode;
    makeHeaderNamesUppercase();
  }

  URLResponse(URL url, Map<String, List<String>> headers, InputStream inputStream) {
    this.url = url;
    this.headers = headers;
    this.inputStream = inputStream;
    errorMessage = null;
    resultCode = 200;
    makeHeaderNamesUppercase();
  }

  public URL getURL() {
    return url;
  }

  private void makeHeaderNamesUppercase() {
    uppercaseHeaders = new HashMap<String, List<String>>(headers.size());
    List<String> values;
    if (headers != null) {
      for (String key : headers.keySet()) {
        if (key != null) {
          values = headers.get(key);
          uppercaseHeaders.put(key.toUpperCase(), values);
        }
      }
    }
  }

  /**
   * Get the header values corresponding to a header name in a case-insensitive way
   *
   * @param headerName
   * @return
   */
  public List<String> getHeaderValues(String headerName) {
    return uppercaseHeaders.get(headerName.toUpperCase());
  }

  public Map<String, List<String>> getRawHeaders() {
    return headers;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public int getResultCode() {
    return resultCode;
  }

  public String getInputStreamAsStringAndClose() throws IOException {
    if (inputStream == null) {
      return null;
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 512);

    try {
      return toString(reader);
    } finally {
      reader.close();
      inputStream = null;
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


//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;

import java.util.Map;
import java.util.HashMap;
import java.net.URL;
import java.io.IOException;
import java.io.StringReader;
import java.io.BufferedReader;

import android.content.Context;
import androidx.util.GUITask;
import androidx.net.URLPoster;
import androidx.net.AllHostsAreValid;
import androidx.net.URLResponse;

/**
 * Constructs a factory for creating authentication tokens for connecting
 * to a Google service with name {@code serviceName} for an application
 * with the name {@code applicationName}. The default domain
 * (www.google.com) will be used to authenticate.
 * 
 * @author Luke Liu
 */
public class GaiaLoginTask implements GUITask {
  private final String CLIENT_LOGIN_URL = "https://www.google.com/accounts/ClientLogin";
  private String service;
  private String email, password;
  private AsyncCallback<GaiaSession> callback;
  private GaiaSession session;
  private Context context;

  //HTTP response error code
  final int ERROR_EXECUTE_SOCKETTIMEOUT = -25;
  final int ERROR_RESPONSE_4XX = -35;
  final int ERROR_RESPONSE_5XX = -36;	
 
  public GaiaLoginTask(String service, String email, String password, AsyncCallback<GaiaSession> callback, Context context) {
    this.service = service;
    this.email = email;
    this.password = password;
    this.callback = callback;
    this.context = context;
  }

  // See http://code.google.com/apis/accounts/docs/AuthForInstalledApps.html for login info
  public void executeNonGuiTask() throws Exception {
    URLPoster form = new URLPoster(new URL(CLIENT_LOGIN_URL), new AllHostsAreValid(), this.context);
    Map<String, String> params = new HashMap<String, String>();
    params.put("accountType", "HOSTED_OR_GOOGLE");
    params.put("Email", email);
    params.put("Passwd", password);
    params.put("service", service);
    params.put("source", "Foxconn-Calendar-Android");
    URLResponse response = form.postForm(null, params);
    if (response.getResultCode() != 200) {
      throw new IOException(Integer.toString(response.getResultCode()));
    }
    
    Map<String, String> responseMap = parseResponse(response.getInputStreamAsStringAndClose());
//    String sid = responseMap.get("SID");
//    String lsid = responseMap.get("LSID");
    String auth = responseMap.get("Auth");
    if(auth == null)	throw new IOException(Integer.toString(response.getResultCode()));
    session = new GaiaSession(email, auth);
  }

  private Map<String, String> parseResponse(String responseBody) throws IOException {
	LogX.d(responseBody);  
    if(responseBody.indexOf("\n\n") != -1) {
    	int intBodyStart = responseBody.indexOf("\n\n") + 2; 
    	responseBody = responseBody.substring(intBodyStart);	
    }
    Map<String, String> params = new HashMap<String, String>();
    StringReader sr = new StringReader(responseBody);
    BufferedReader br = new BufferedReader(sr);
    String line;
    while ( (line = br.readLine()) != null) {
      int eq = line.indexOf('=');
      if (eq == -1) {
        throw new IOException("Bad response format: '" + line + "'");
      }
      String key = line.substring(0, eq);
      String value = line.substring(eq + 1);
      params.put(key, value);
    }
    return params;
  }

  public void after_execute() {
    callback.onSuccess(session);
  }

  public void onFailure(Throwable t) {
    callback.onFailure(t);
  }
  
}


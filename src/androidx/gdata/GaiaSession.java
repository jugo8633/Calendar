//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


/**
 * Constructs a user Session for maintain user's sticky connection state
 * of Google service. 
 * 
 * @author Luke Liu
 */
public class GaiaSession {
  // the following constants correspond to http://code.google.com/support/bin/answer.py?answer=62712&topic=10433
  public static final String SERVICE_CALENDAR = "cl";
  public static final String SERVICE_GBASE = "gbase";
  public static final String SERVICE_BLOGGER = "blogger";
  public static final String SERVICE_CONTACTS = "cp";
  public static final String SERVICE_WRITELY = "writely";
  public static final String SERVICE_PICASSA = "lh2";
  public static final String SERVICE_APPS = "apps";
  public static final String SERVICE_SPREADSHEET = "wise";
  public static final String SERVICE_YOUTUBE = "youtube";
  public static final String SERVICE_XAPI = "xapi";

  private String username;
  private String token;

  public GaiaSession(String username, String token) {
    this.username = username;
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public String getToken() {
    return token;
  }
}
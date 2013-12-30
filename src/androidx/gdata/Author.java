//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import java.io.Serializable;

/**
 * @author Luke Liu
 * 
 */
public class Author implements Serializable{
  private String name, email, uri;

  public Author(String name, String email, String uri) {
    this.name = name;
    this.email = email;
    this.uri = uri;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getUri() {
    return uri;
  }
}

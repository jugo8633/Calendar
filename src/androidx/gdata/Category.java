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
*
* @author Luke Liu
* Date: Nov 12, 2008
*/
public class Category implements Serializable{
  private String scheme;
  private String term;

  public Category(String scheme, String term) {
    this.scheme = scheme;
    this.term = term;
  }

  public String getScheme() {
    return scheme;
  }

  public String getTerm() {
    return term;
  }
}

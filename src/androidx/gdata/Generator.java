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
* @author jennings
* Date: Nov 12, 2008
*/
public class Generator implements Serializable{
  String version, uri, text;

  public Generator(String version, String uri, String text) {
    this.version = version;
    this.uri = uri;
    this.text = text;
  }

  public String getVersion() {
    return version;
  }

  public String getUri() {
    return uri;
  }

  public String getText() {
    return text;
  }
}

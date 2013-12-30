//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import java.io.PrintWriter;

/**
 * Unmarshal data object to XML document.
 * 
 * @author Luke Liu
 * 
 */
public interface XMLSerializer<T> {
  public static final String XML_PROLOG = "<?xml version='1.0' encoding='UTF-8'?>";
  public static final String STYLESHEET = "<?xml-stylesheet href=\"http://www.blogger.com/styles/atom.css\" type=\"text/css\"?>";
  public static final String FEED_PROLOG = "<feed xmlns='http://www.w3.org/2005/Atom'\n"+
      "      xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/'\n" +
      "      xmlns:gContact='http://schemas.google.com/contact/2008'\n" +
      "      xmlns:batch='http://schemas.google.com/gdata/batch'\n" +
      "      xmlns:gd='http://schemas.google.com/g/2005'>";

  public static final String FEED_EPILOG = "</feed>";
  public void serialize(PrintWriter out, T thing);
}

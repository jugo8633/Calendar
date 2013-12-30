//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.net;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * An implementation of HostnameVerifier that always returns true
 *
 * @author Luke Liu
 */
public class AllHostsAreValid implements HostnameVerifier {
  public boolean verify(String s, SSLSession sslSession) {
    return true;
  }
}

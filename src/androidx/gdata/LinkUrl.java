//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;

/**
 * @author Luke Liu
 */
public class LinkUrl extends ElementWithTypeAttribute {
  private String href;
  private String rel;
  private String title;

  public LinkUrl(String href, String rel, String type, String title) {
    super(type);
    this.href = href;
    this.rel = rel;
    this.title = title;
  }

  public String getHref() {
    return href;
  }

  public String getRel() {
    return rel;
  }

  public String getTitle() {
    return title;
  }
}

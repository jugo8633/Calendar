//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;

/**
*
* @author Luke Liu
* Date: Nov 13, 2009
*/
public class LinkUrlUtil {
  private LinkUrlUtil() {
  }

  public static LinkUrl getNext(ThingWithLinks feed) {
    return getLinkWithRel(feed, "next");
  }

  public static LinkUrl getSelf(ThingWithLinks feed) {
    return getLinkWithRel(feed, "self");
  }

  public static LinkUrl getEditLink(ThingWithLinks feed) {
    return getLinkWithRel(feed, "edit");
  }

  private static LinkUrl getLinkWithRel(ThingWithLinks feed, String rel) {
    for (LinkUrl link : feed.getLinks()) {
      if (rel.equals(link.getRel())) {
        return link;
      }
    }
    return null;
  }

  public static LinkUrl getPostLink(ThingWithLinks feed) {
    return getLinkWithRelEndingWith(feed, "#post");
  }

  public static LinkUrl getFeedLink(ThingWithLinks feed) {
    return getLinkWithRelEndingWith(feed, "#feed");
  }

  private static LinkUrl getLinkWithRelEndingWith(ThingWithLinks feed, String rel_suffix) {
    for (LinkUrl link : feed.getLinks()) {
      if (link.getRel().endsWith(rel_suffix)) {
        return link;
      }
    }
    return null;
  }

}

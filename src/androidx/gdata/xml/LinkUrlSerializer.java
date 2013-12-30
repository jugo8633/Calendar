//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.gdata.XMLSerializer;
import androidx.gdata.LinkUrl;
import static androidx.gdata.xml.StringEncoder.encode;
import java.io.PrintWriter;

/**
 * @author Luke Liu
 *         Date: Nov 12, 2008
 */
public class LinkUrlSerializer implements XMLSerializer<LinkUrl> {

  private String extraIndent = "";

  public void setExtraIndent(String extraIndent) {
    this.extraIndent = extraIndent;
  }
  

  public void serialize(PrintWriter out, LinkUrl link) {
    out.println(extraIndent + "  <link rel='" + link.getRel() + "'");
    out.println(extraIndent + "        type='" + link.getType() + "'");
    if (link.getTitle() != null) {
      out.println(extraIndent + "        href='" + encode(link.getHref()) + "'");
      out.println(extraIndent + "        title='" + encode(link.getTitle()) + "'/>");
    } else {
      out.println(extraIndent + "        href='" + encode(link.getHref()) + "'/>");
    }
  }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.gdata.XMLSerializer;
import androidx.gdata.Author;
import java.io.PrintWriter;

public class AuthorSerializer implements XMLSerializer<Author> {
  private String extraIndent = "";

  public void setExtraIndent(String extraIndent) {
    this.extraIndent = extraIndent;
  }
  
  public void serialize(PrintWriter out, Author author) {
    out.println(extraIndent + "  <author>");
    out.println(extraIndent + "    <name>" + author.getName() + "</name>");
    if (author.getUri() != null) {
      out.println(extraIndent + "    <uri>" + author.getUri() + "</uri>");
    }
    out.println(extraIndent + "    <email>" + author.getEmail() + "</email>");
    out.println(extraIndent + "  </author>");
  }
}

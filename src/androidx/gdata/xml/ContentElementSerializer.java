//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.gdata.ContentElement;
import java.io.PrintWriter;

public class ContentElementSerializer<T extends ContentElement> extends ElementSerializer<T> {
  protected String openingTag, closingTag;

  protected ContentElementSerializer(int indentLevel, String openingTag, String closingTag) {
    super(indentLevel);
    this.openingTag = openingTag;
    this.closingTag = closingTag;
  }

  protected void serializeOpeningTag(PrintWriter out, T element) {
    indent(out);
    out.print(openingTag);
  }

  protected void serializeElementBody(PrintWriter out, T element) {
    out.print(element.getContent());
  }

  protected void serializeClosingTag(PrintWriter out) {
    out.print(closingTag);
    out.println();
  }
}
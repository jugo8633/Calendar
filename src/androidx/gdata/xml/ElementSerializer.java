//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.gdata.XMLSerializer;
import java.io.PrintWriter;

public abstract class ElementSerializer<T> implements XMLSerializer<T> {
  private int indentLevel;

  protected ElementSerializer(int indentLevel) {
    this.indentLevel = indentLevel;
  }

  protected void indent(PrintWriter out) {
    for (int i = 0; i < indentLevel; ++i) {
      out.print("  ");
    }
  }

  public void serialize(PrintWriter out, T element) {
    serializeOpeningTag(out, element);
    serializeElementBody(out, element);
    serializeClosingTag(out);
  }

  protected abstract void serializeOpeningTag(PrintWriter out, T element);
  protected abstract void serializeElementBody(PrintWriter out, T element);
  protected abstract void serializeClosingTag(PrintWriter out);
}
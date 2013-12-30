//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.gdata.XMLSerializer;
import androidx.gdata.Category;
import java.io.PrintWriter;

public class CategorySerializer implements XMLSerializer<Category> {
  private String extraIndent = "";

  public void setExtraIndent(String extraIndent) {
    this.extraIndent = extraIndent;
  }

  public void serialize(PrintWriter out, Category category) {
    out.println(extraIndent + "<category scheme='" + category.getScheme() + "' term='" + category.getTerm() + "'/>");
  }
}
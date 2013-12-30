//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.gdata.Generator;
import androidx.gdata.XMLSerializer;
import java.io.PrintWriter;

public class GeneratorSerializer implements XMLSerializer<Generator> {
  public void serialize(PrintWriter out, Generator generator) {
    out.println("  <generator version='" + generator.getVersion() + "'");
    out.println("             uri='" + generator.getUri() + "'>" + generator.getText() + "</generator>");
  }
}

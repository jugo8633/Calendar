//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.gdata.Author;
import androidx.xml.XMLBuilder;
import org.w3c.dom.Node;

public class AuthorDeserializer extends XMLBuilder<Author> {
  public Author build(Node authorNode) {
    this.node = authorNode;

    String name = getText("name");
    String email = getText("email");
    String uri = getText("uri");

    return new Author(name, email, uri);
  }
}

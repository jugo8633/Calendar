//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import org.w3c.dom.Node;
import androidx.gdata.Title;
import androidx.xml.XMLBuilder;

/**
 * @author Luke Liu
 */
public class TitleDeserializer extends XMLBuilder<Title> {

  public Title build(Node titleNode) {
    node = titleNode;
    String type = getAttribute("type");
    String content = getText();
    return new Title(type, content);
  }
}
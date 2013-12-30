//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.xml;


import org.w3c.dom.Node;
import java.util.List;

/**
 * Provide methods of unmarshal a XML to data object.
 *  
 * @author Luke Liu
 */
public abstract class XMLBuilder<T> {
  protected Node node;

  public abstract T build(Node node);

  protected String getText(String name) {
    return XMLUtil.getChildContents(node, name);
  }

  protected String getText() {
    return XMLUtil.getChildTextNodes(node);
  }

  protected String getAttribute(String attname) {
    return XMLUtil.getNodeAttribute(node, attname);
  }

  protected Node getChildNode(String name) {
    return XMLUtil.findNamedElementNode(node, name);
  }

  protected List<Node> getChildNodes(String name) {
    return XMLUtil.findNamedElementNodes(node, name);
  }

}



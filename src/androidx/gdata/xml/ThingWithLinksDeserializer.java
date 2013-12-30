//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import androidx.xml.XMLBuilder;
import androidx.gdata.LinkUrl;
import androidx.gdata.When;
import org.w3c.dom.Node;
import java.util.List;

/**
 * @author Luke Liu
 * 
 */
public abstract class ThingWithLinksDeserializer<T> extends XMLBuilder<T> {
  private LinkUrlDeserializer linkBuilder = new LinkUrlDeserializer();
  private WhenDeserializer whenBuilder = new WhenDeserializer();

  protected LinkUrl[] getLinks() {
    List<Node> linkNodes = getChildNodes("link");
    LinkUrl[] links = new LinkUrl[linkNodes.size()];
    int i = 0;
    for (Node linkNode : linkNodes) {
      LinkUrl link = linkBuilder.build(linkNode);
      links[i++] = link;
    }
    return links;    
  }
  
  protected When[] getWhens() {
	    List<Node> whenNodes = getChildNodes("gd:when");
	    When[] whens = new When[whenNodes.size()];
	    int i = 0;
	    for (Node whenNode : whenNodes) {
	      When when = whenBuilder.build(whenNode);
	      whens[i++] = when;
	    }
	    return whens;
  }
  
}

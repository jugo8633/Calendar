//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar.xml;


import java.util.List;
import org.w3c.dom.Node;
import androidx.gdata.LinkUrl;
import androidx.gdata.xml.LinkUrlDeserializer;
import androidx.xml.XMLBuilder;

/**
 * 
 * @author Luke Liu
 *
 * @param <T>
 */
public abstract class ThingWithLinksDeserializer<T> extends XMLBuilder<T> {

	private LinkUrlDeserializer linkBuilder = new LinkUrlDeserializer();

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

}

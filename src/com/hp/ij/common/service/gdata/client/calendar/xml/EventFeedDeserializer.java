//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar.xml;


import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Node;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import com.hp.ij.common.service.gdata.client.calendar.EventFeed;
import com.hp.ij.common.service.gdata.client.calendar.EventFree;
import com.hp.ij.common.service.gdata.client.calendar.xml.EventDeserializer;
import androidx.gdata.Author;
import androidx.gdata.Generator;
import androidx.gdata.LinkUrl;
import androidx.gdata.xml.AuthorDeserializer;
import androidx.gdata.xml.GeneratorDeserializer;

/**
 * EventFeed Deserializer
 * 
 * @author Luke Liu
 *
 */
public class EventFeedDeserializer extends
		ThingWithLinksDeserializer<EventFeed> {
	
	AuthorDeserializer authorBuilder = new AuthorDeserializer();
	GeneratorDeserializer generatorBuilder = new GeneratorDeserializer();
	EventDeserializer eventBuilder = new EventDeserializer();

	@Override
	public EventFeed build(Node feedNode) {
	    node = feedNode;
	    String id = getText("id");
	    String updated = getText("updated");
	    String title = getText("title");
	    String subtitle = getText("subtitle");
	    
	    Node authorNode = getChildNode("author");
	    Author author = authorBuilder.build(authorNode);

	    Node generatorNode = getChildNode("generator");
	    Generator generator = generatorBuilder.build(generatorNode);
	    
	    List<Node> entryNodes = getChildNodes("entry");
	    
	    Event[] events = new Event[entryNodes.size()];
	    for(int i=0; i<events.length; i++) {
	    	events[i] = eventBuilder.build(entryNodes.get(i));
	    }
	    
	    LinkUrl[] links = getLinks();
	    
	    return new EventFeed(id, updated, title, subtitle, author, generator, events, links);
	}

}

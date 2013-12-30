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
import com.hp.ij.common.service.gdata.client.calendar.EventFeedFree;
import com.hp.ij.common.service.gdata.client.calendar.EventFree;
import com.hp.ij.common.service.gdata.client.calendar.xml.EventFreeDeserializer;
import androidx.gdata.Author;
import androidx.gdata.Generator;
import androidx.gdata.LinkUrl;
import androidx.gdata.xml.AuthorDeserializer;
import androidx.gdata.xml.GeneratorDeserializer;

/**
 * 
 * @author Luke Liu
 *
 */
public class EventFeedFreeDeserializer extends
		ThingWithLinksDeserializer<EventFeedFree> {
	
	//AuthorDeserializer authorBuilder = new AuthorDeserializer();
	//GeneratorDeserializer generatorBuilder = new GeneratorDeserializer();
	EventFreeDeserializer eventBuilder = new EventFreeDeserializer();

	@Override
	public EventFeedFree build(Node feedNode) {
		// TODO Auto-generated method stub
	    node = feedNode;
	    String id = getText("id");
	    //String updated = getText("updated");
	    String title = getText("title");
	    //String subtitle = getText("subtitle");
	    
	    //Node authorNode = getChildNode("author");
	    //Author author = authorBuilder.build(authorNode);

	    //Node generatorNode = getChildNode("generator");
	    //Generator generator = generatorBuilder.build(generatorNode);
	    List<Node> entryNodes = getChildNodes("entry");
	    
	    EventFree[] events = new EventFree[entryNodes.size()];
	    for(int i=0; i<events.length; i++) {
	    	events[i] = eventBuilder.build(entryNodes.get(i));
	    }
	    /*
	    // ----- Extend recurrence event to a concrete event -----
	    ArrayList<EventFree> arrayListEvents = new ArrayList<EventFree>();
		for (Node entryNode : entryNodes) {
			EventFree eventFree = eventBuilder.build(entryNode);
			for (int j = 0; j < eventFree.getWhens().length; j++) {
				EventFree eventRecurrence = eventFree.makeClone();
				eventRecurrence.setWhen(eventFree.getWhens()[j]);
				arrayListEvents.add(eventRecurrence);
			}
		}
		// So weird, ArrayList will add more elements than we allow.		
		EventFree[] events = new EventFree[arrayListEvents.size()];
		for(int i=0; i<events.length; i++)	events[i] = arrayListEvents.get(i);
		Arrays.sort(events);
		*/
		
	    //LinkUrl[] links = getLinks();
	    
	    return new EventFeedFree(id, title, events);
	    
	}

}

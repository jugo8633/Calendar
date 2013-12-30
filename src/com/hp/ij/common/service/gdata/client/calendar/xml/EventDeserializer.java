//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar.xml;


import org.w3c.dom.Node;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;

import androidx.gdata.Author;
import androidx.gdata.Category;
import androidx.gdata.LinkUrl;
import androidx.gdata.Recurrence;
import androidx.gdata.When;
import androidx.gdata.Where;
import androidx.gdata.xml.CategoryDeserializer;
import androidx.gdata.xml.AuthorDeserializer;
import androidx.gdata.xml.ThingWithLinksDeserializer;
import androidx.gdata.xml.WhenDeserializer;
import androidx.gdata.xml.WhereDeserializer;
import androidx.gdata.xml.RecurrenceDeserializer;

public class EventDeserializer extends ThingWithLinksDeserializer<Event> {

	private CategoryDeserializer categoryBuilder = new CategoryDeserializer();
	private AuthorDeserializer authorBuilder = new AuthorDeserializer();
	private WhenDeserializer whenBuilder = new WhenDeserializer();
	private WhereDeserializer whereBuilder = new WhereDeserializer();
	private RecurrenceDeserializer recurrenceDeserializer = new RecurrenceDeserializer(); 
	
	@Override
	public Event build(Node eventNode) {
		node = eventNode;
		String id = getText("id");
		
		String published = getText("published");
		String updated = getText("updated");
		
	    Node categoryNode = getChildNode("category");
	    Category category = categoryBuilder.build(categoryNode);
	    
	    String title = getText("title");
	    String content = getText("content");
	    
	    LinkUrl[] links = getLinks();
	    
	    Node authorNode = getChildNode("author");
	    Author author = authorBuilder.build(authorNode);
	    
	    String transparency = getText("gd:transparency");
	    String eventStatus = getText("gd:eventStatus");
	    
	    String strRecurrence = getText("gd:recurrence");
	    Recurrence recurrence = null;
	    if( strRecurrence != null )		recurrence = recurrenceDeserializer.build(strRecurrence); 
	    else	recurrence = new Recurrence(false);
	    
	    Node whenNode = getChildNode("gd:when");
	    When when = null;
	    if(whenNode != null)	when = whenBuilder.build(whenNode);
	    
	    /**
	     * Get all when
	     */
	    // When[] whens = getWhens();
	    
	    Node whereNode = getChildNode("gd:where");
	    Where where = whereBuilder.build(whereNode);

	    Event event = new Event(id, published, updated, category, title, content, links, author, transparency, eventStatus, when, where);
	    event.setRecurrence(recurrence);
	    return event;
 	}

}

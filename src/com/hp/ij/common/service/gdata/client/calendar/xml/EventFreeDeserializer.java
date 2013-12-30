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
import com.hp.ij.common.service.gdata.client.calendar.EventFree;

import androidx.RegEx;
import androidx.gdata.Author;
import androidx.gdata.Category;
import androidx.gdata.LinkUrl;
import androidx.gdata.When;
import androidx.gdata.Where;

import androidx.gdata.xml.CategoryDeserializer;
import androidx.gdata.xml.AuthorDeserializer;
import androidx.gdata.xml.ThingWithLinksDeserializer;
import androidx.gdata.xml.WhenDeserializer;
import androidx.gdata.xml.WhereDeserializer;

public class EventFreeDeserializer extends ThingWithLinksDeserializer<EventFree> {

	//private CategoryDeserializer categoryBuilder = new CategoryDeserializer();
	//private AuthorDeserializer authorBuilder = new AuthorDeserializer();
	private WhenDeserializer whenBuilder = new WhenDeserializer();
	//private WhereDeserializer whereBuilder = new WhereDeserializer();
	
	@Override
	public EventFree build(Node eventNode) {
		// TODO Auto-generated method stub
		node = eventNode;
		String id = getText("id");
		//String published = getText("published");
		//String updated = getText("updated");
		
		//Node categoryNode = getChildNode("category");
		//Category category = categoryBuilder.build(categoryNode);
	    
	    String title = getText("title");
	    //String content = getText("content");
	    
	    //LinkUrl[] links = getLinks();
	    
	    //Node authorNode = getChildNode("author");
	    //Author author = authorBuilder.build(authorNode);
	    
	    //String transparency = getText("gd:transparency");
	    //String eventStatus = getText("gd:eventStatus");
	    
	    /*
	    Node whenNode = getChildNode("gd:when");
	    When when = whenBuilder.build(whenNode);
	    */
	    
	    /*
	     * Get all when
	     */
	    // When[] whens = getWhens();
	    Node whenNode = getChildNode("gd:when");
	    When when = whenBuilder.build(whenNode);
	    
	    //Node whereNode = getChildNode("gd:where");
	    //Where where = whereBuilder.build(whereNode);

	    //return new Event(id, published, updated, category, title, content, links, author, transparency, eventStatus, whens, where);
	    return new EventFree(id, title, when);
 	}

}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar.xml;


import java.io.PrintWriter;
import com.hp.ij.common.service.gdata.client.calendar.EventFeed;
import androidx.gdata.XMLSerializer;

/**
 * EventFeedSerializer can serialize EventFeed to xml document 
 * @author Luke Liu
 *
 */
public class EventFeedSerializer implements XMLSerializer<EventFeed> {

	/**
	 * Default EventFeedSerializer constructor
	 */
	public EventFeedSerializer() {
		
	}
	
	public void serialize(PrintWriter out, EventFeed thing) {
		/*
	    out.println(XML_PROLOG);
	    out.println(FEED_PROLOG);
	    out.println("  <id>" + contactFeed.getId() + "</id>");
	    out.println("  <updated>" + contactFeed.getUpdated() + "</updated>");
	    categorySerializer.serialize(out, contactFeed.getCategory());
	    out.println("  <title type='text'>" + contactFeed.getTitle() + "</title>");
	    for (LinkUrl link : contactFeed.getLinks()) {
	      linkSerializer.serialize(out, link);
	    }
	    authorSerializer.serialize(out, contactFeed.getAuthor());
	    generatorSerializer.serialize(out, contactFeed.getGenerator());
	    openSearchSerializer.serialize(out, contactFeed.getOpenSearch());
	    for (Contact contact : contactFeed.getEntries()) {
	       contactSerializer.serialize(out, contact);
	    }
	    out.println(FEED_EPILOG);
	    */
	}

}

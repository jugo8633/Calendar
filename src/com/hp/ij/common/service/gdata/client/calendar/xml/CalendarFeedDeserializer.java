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
import com.hp.ij.common.service.gdata.client.calendar.Calendar;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import androidx.gdata.Author;
import androidx.gdata.LinkUrl;
import androidx.gdata.xml.AuthorDeserializer;

/**
 * CalendarFeed Deserializer 
 * 
 * @author Luke Liu
 *
 * @param <Calendar>
 */
public class CalendarFeedDeserializer extends
		ThingWithLinksDeserializer<CalendarFeed> {

	AuthorDeserializer mAuthorBuilder = new AuthorDeserializer();
	CalendarDeserializer mCalendarBuilder = new CalendarDeserializer();
	
	public CalendarFeed build(Node feedNode) {
		node = feedNode;
		Node authorNode = getChildNode("author");
		Author author = mAuthorBuilder.build(authorNode);
		
		List<Node> entryNodes = getChildNodes("entry");
		Calendar[] calendars = new Calendar[entryNodes.size()];
		for(int i=0; i<calendars.length; i++) calendars[i] = mCalendarBuilder.build(entryNodes.get(i)); 
		
	    LinkUrl[] links = getLinks();

	    return new CalendarFeed(author, calendars, links);
	}

}

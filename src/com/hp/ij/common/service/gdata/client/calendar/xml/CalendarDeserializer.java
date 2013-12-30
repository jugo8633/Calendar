//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar.xml;


import org.w3c.dom.Node;
import com.hp.ij.common.service.gdata.client.calendar.Calendar;
import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;

import androidx.gdata.AccessLevel;
import androidx.gdata.xml.AccessLevelDeserializer;
import androidx.gdata.xml.ThingWithLinksDeserializer;

/**
 * A Calendar Deserializer
 * 
 * @author Luke Liu
 *
 */
public class CalendarDeserializer extends ThingWithLinksDeserializer<Calendar> {
	
	private AccessLevelDeserializer accessLevelDeserializer = new AccessLevelDeserializer(); 

	public Calendar build(Node calendarNode) {
		node = calendarNode;
		String id = getText("id");
		String title = getText("title");
		
		Node nodeAccessLevel = getChildNode("gCal:accesslevel");
		AccessLevel accessLevel = accessLevelDeserializer.build(nodeAccessLevel);
		return new Calendar(id, title, accessLevel.getmValue());
	}

}

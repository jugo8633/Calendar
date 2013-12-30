//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar.xml;


import java.io.PrintWriter;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import androidx.gdata.Category;
import androidx.gdata.Reminder;
import androidx.gdata.XMLSerializer;
import androidx.gdata.xml.CategorySerializer;
import androidx.gdata.xml.WhereSerializer;
import androidx.gdata.xml.WhenSerializer;
import androidx.gdata.xml.RecurrenceSerializer;
import androidx.gdata.xml.ReminderSerializer;

/**
 * A serializer will serialize Event to a xml document.
 * 
 * @author Luke Liu
 *
 */
public class EventSerializer implements XMLSerializer<Event> {

	private CategorySerializer categorySerializer;
	private WhereSerializer whereSerializer; 
	private WhenSerializer whenSerializer;
	private RecurrenceSerializer recurrenceSerializer;
	private ReminderSerializer reminderSerializer;
	
	/**
	 * True for show XmlNamespace.
	 */
	private boolean showXmlNamespace;
	
	/**
	 * Default constructor for not show XMLPROLOG 
	 */
	public EventSerializer() {
		this(true);
	}
	
	public EventSerializer(boolean showXmlNamespace) {
		this.showXmlNamespace = showXmlNamespace;
		categorySerializer = new CategorySerializer(); 
		whereSerializer = new WhereSerializer();
		whenSerializer = new WhenSerializer();
		recurrenceSerializer = new RecurrenceSerializer();
		reminderSerializer = new ReminderSerializer();
	}
	
	public void serialize(PrintWriter out, Event entry) {
	    if (showXmlNamespace) {
	    	out.println("<entry xmlns='http://www.w3.org/2005/Atom' xmlns:gAcl='http://schemas.google.com/acl/2007' xmlns:batch='http://schemas.google.com/gdata/batch' xmlns:gCal='http://schemas.google.com/gCal/2005' xmlns:gd='http://schemas.google.com/g/2005'>");
	    } else {
	        out.println("  <entry>");
	    }

	    if (entry.getId() != null) {
	    	out.println("<id>" + entry.getId() + "</id>");
	    }
	    
	    if (entry.getCategory() != null) {
	    	categorySerializer.serialize(out, entry.getCategory());
	    }
	    
	    out.println("<title type='text'>" + entry.getTitle() + "</title>");
	    out.println("<content type='text'>" + entry.getContent() + "</content>");
	    
	    if(entry.getReminders() != null) {
	    	for(Reminder reminder : entry.getReminders()) reminderSerializer.serialize(out, reminder);
	    }
	    
	    //out.println("  <gd:transparency value='http://schemas.google.com/g/2005#event.opaque'></gd:transparency>");
	    //out.println("  <gd:eventStatus value='http://schemas.google.com/g/2005#event.confirmed'></gd:eventStatus>");
	    //out.println("  <gd:transparency value='http://schemas.google.com/g/2005#event.opaque'>" + entry.getTransparency() + "</gd:transparency>");
	    //out.println("  <gd:eventStatus value='http://schemas.google.com/g/2005#event.confirmed'>" + entry.getEventStatus() + "</gd:eventStatus>");

	    if (entry.getWhere() != null) {
	    	whereSerializer.serialize(out, entry.getWhere());
	    }

	    if (entry.getWhen() != null) {
	    	whenSerializer.serialize(out, entry.getWhen());
	    }
	    
	    if (entry.getRecurrence() != null ) {
	    	recurrenceSerializer.serialize(out, entry.getRecurrence());
	    }
	    
	    out.println("</entry>");
	}

}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;

import org.w3c.dom.Node;

import com.hp.ij.common.service.gdata.client.calendar.widget.LogX;

import androidx.gdata.Recurrence;
import androidx.util.TimeZoneUtil;
import androidx.xml.XMLBuilder;

/**
 * A gd:recurrence element contains syntax for recurring events based upon the iCalendar standard(RFC 2445)
 * More detail info refer to http://www.ietf.org/rfc/rfc2445.txt page 47 for time section
 * 
 * @author Luke Liu
 *
 */
public class RecurrenceDeserializer extends XMLBuilder<Recurrence> {

	@Override
	public Recurrence build(Node recurrenceNode) {
		this.node = recurrenceNode;
		String dtStart = getAttribute("dtStart");
		//String dtEnd = getAttribute("dtEnd");
		//String freq = getAttribute("freq");
		//String byDay = getAttribute("byDay");
		//String until= getAttribute("until");
		//String timeZone = getAttribute("timeZone");
		//boolean hasRecurrence = true;

		//return new Recurrence(hasRecurrence, dtStart, dtEnd, freq, byDay, until);
		return null;
	}
	
	/**
	 * Extract starttime, endtime, until, timezone, frequency from gd:recurrence string
	 *  
	 * @param strRecurrence
	 * @return Recurrence
	 */
	public Recurrence build(String strRecurrence) {
    	String[] strRecurrences = strRecurrence.split("\\n");
    	int intStartIndex, intEndIndex, intStringLength;
    	//----- TimeZone -----
    	String strTimeZone = "Z";
    	if(strRecurrences[0].indexOf("DTSTART;TZID=") != -1) {
        	intStartIndex = strRecurrences[0].indexOf("DTSTART;TZID=");
        	intEndIndex = strRecurrences[0].indexOf(":");
        	intStringLength = "DTSTART;TZID=".length();
        	strTimeZone = strRecurrences[0].substring(intStartIndex + intStringLength, intEndIndex);
    	}
    	
    	//----- StartTime -----
    	intStartIndex = strRecurrences[0].indexOf(":");
    	intStringLength = ":".length(); 
    	String strStartTime = strRecurrences[0].substring(intStartIndex + intStringLength);

    	//----- EndTime -----
    	intStartIndex = strRecurrences[1].indexOf(":");
    	intStringLength = ":".length(); 
    	String strEndTime = strRecurrences[1].substring(intStartIndex + intStringLength);

    	//----- Frequency -----
    	intStartIndex = strRecurrences[2].indexOf("RRULE:FREQ=");
    	intEndIndex = strRecurrences[2].indexOf(";");
    	intStringLength = "RRULE:FREQ=".length(); 
    	String strFrequency = strRecurrences[2].substring(intStartIndex + intStringLength, intEndIndex);
    	
    	//----- BYDAY -----
    	intStartIndex = strRecurrences[2].indexOf("BYDAY=");
    	intStringLength = "BYDAY=".length();
    	String strByday = "";
    	if(intStartIndex != -1) {
	    	intEndIndex = strRecurrences[2].indexOf(";", intStartIndex + intStringLength);
	    	if(intEndIndex == -1)	intEndIndex = strRecurrences[2].length(); 
	    	strByday = strRecurrences[2].substring(intStartIndex + intStringLength, intEndIndex);
    	}
    	 
    	//----- Until -----
    	intStartIndex = strRecurrences[2].indexOf("UNTIL=");
    	intStringLength = "UNTIL=".length();
    	String strUntil = strRecurrences[2].substring(intStartIndex + intStringLength);
    	if(strUntil.indexOf("Z") != -1)	strUntil = strUntil.substring(0, strUntil.indexOf("Z"));

    	Recurrence recurrence = new Recurrence(true, strStartTime, strEndTime, strFrequency, strByday, strUntil);
    	recurrence.setTimeZone(strTimeZone);
    	return recurrence; 
	}

}

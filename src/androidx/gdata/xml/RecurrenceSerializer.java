//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import java.io.PrintWriter;
import androidx.gdata.XMLSerializer;
import androidx.gdata.Recurrence;

public class RecurrenceSerializer implements XMLSerializer<Recurrence> {

	public void serialize(PrintWriter out, Recurrence thing) {
		if(thing.isHasRecurrence()) {
			
			/*
			out.println("  <gd:recurrence>DTSTART;VALUE=DATE-TIME:" + thing.getDtStart());
			//out.println("  DURATION:PT1H0M0S\r\n");
			out.println("  DTEND;VALUE=DATE-TIME:" + thing.getDtEnd());
			out.println("  RRULE:FREQ=" + thing.getFreq() + ";BYDAY=" + thing.getByDay() + ";UNTIL=" + thing.getUntil());
			out.println("  </gd:recurrence>");
			*/

			StringBuilder stringBuilderRRULE = new StringBuilder();
			if(thing.getFreq().compareTo("WEEKLY") == 0) {
				stringBuilderRRULE.append("RRULE:FREQ=" + thing.getFreq());
				stringBuilderRRULE.append(";BYDAY=" + thing.getByDay());
				stringBuilderRRULE.append(";UNTIL=" + thing.getUntil());
			} else {
				stringBuilderRRULE.append("RRULE:FREQ=" + thing.getFreq());
				stringBuilderRRULE.append(";UNTIL=" + thing.getUntil());
			}
				
			out.println("<gd:recurrence>DTSTART;TZID=" + thing.getTimeZone() + ":" + thing.getDtStart());
			out.println("DTEND;TZID=" + thing.getTimeZone() + ":" + thing.getDtEnd());
			out.println(stringBuilderRRULE.toString());
			out.println("</gd:recurrence>");
			
			/*
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("BEGIN:VTIMEZONE");
			stringBuilder.append("TZID:America/Los_Angeles");
			stringBuilder.append("X-LIC-LOCATION:America/Los_Angeles");
			stringBuilder.append("TZOFFSETFROM:-0800");
			stringBuilder.append("TZOFFSETTO:-0700");
			stringBuilder.append("TZNAME:PDT");
			stringBuilder.append("DTSTART:19700308T020000");
			stringBuilder.append("RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=2SU");
			stringBuilder.append("BEGIN:STANDARD");
			stringBuilder.append("TZOFFSETFROM:-0700");
			stringBuilder.append("TZOFFSETTO:-0800");
			stringBuilder.append("TZNAME:PST");
			stringBuilder.append("DTSTART:19701101T020000");
			stringBuilder.append("RRULE:FREQ=YEARLY;BYMONTH=11;BYDAY=1SU");
			stringBuilder.append("END:STANDARD");
			stringBuilder.append("END:VTIMEZONE");
			*/                  
			/*
			out.println("  <gd:recurrence>DTSTART:" + thing.getDtStart());
			out.println("  DTEND:" + thing.getDtEnd());
			out.println("  RRULE:FREQ=WEEKLY;BYDAY=MO;UNTIL=20091012T070000Z");
			out.println("  </gd:recurrence>");
			*/
			/*
			out.println("  <gd:recurrence>");
			out.println("  DTSTART;TZID=Asia/Taipei:20091005T060000");
			out.println("  DURATION:PT3600S");
			out.println("  RRULE:FREQ=DAILY;UNTIL=20091012T070000Z");
			*/
			/*
			out.println("  BEGIN:VTIMEZONE");
			out.println("  TZID:America/Los_Angeles");
			out.println("  X-LIC-LOCATION:America/Los_Angeles");
			out.println("  BEGIN:STANDARD");
			out.println("  TZOFFSETFROM:-0700");
			out.println("  TZOFFSETTO:-0800");
			out.println("  TZNAME:PST");
			out.println("  DTSTART:19671029T020000");
			out.println("  RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU");
			out.println("  END:STANDARD");
			out.println("  BEGIN:DAYLIGHT");
			out.println("  TZOFFSETFROM:-0800");
			out.println("  TZOFFSETTO:-0700");
			out.println("  TZNAME:PDT");
			out.println("  DTSTART:19870405T020000");
			out.println("  RRULE:FREQ=YEARLY;BYMONTH=4;BYDAY=1SU");
			out.println("  END:DAYLIGHT");
			out.println("  END:VTIMEZONE");
			*/
			//out.println("  </gd:recurrence>");
			
		} 
	}

}

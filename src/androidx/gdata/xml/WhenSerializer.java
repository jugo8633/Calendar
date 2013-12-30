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
import androidx.gdata.When;
import androidx.gdata.Reminder;
import androidx.gdata.xml.ReminderSerializer;

public class WhenSerializer implements XMLSerializer<When> {
	private ReminderSerializer reminderBuilder = new ReminderSerializer();
	private String extraIndent = "";

	public void setExtraIndent(String extraIndent) {
		this.extraIndent = extraIndent;
	}

	public void serialize(PrintWriter out, When when) {
	    out.println(extraIndent + "  <gd:when startTime='" + when.getStartTime() + "'");
	    out.println(extraIndent + "  endTime='" + when.getEndTime() + "'>");
	    for(Reminder reminder : when.getReminders()) {
	    	if(reminder.isHasReminder()) reminderBuilder.serialize(out, reminder);
	    }
	    
	    out.println(extraIndent + "  </gd:when>");
	}

}

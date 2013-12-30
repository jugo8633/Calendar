//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import org.w3c.dom.Node;
import androidx.gdata.Reminder;
import androidx.xml.XMLBuilder;

public class ReminderDeserializer extends XMLBuilder<Reminder> {

	@Override
	public Reminder build(Node nodeReminder) {
		this.node = nodeReminder;
		String strMinutes = getAttribute("minutes");
		String strMethod = getAttribute("method");
		return new Reminder(strMinutes, strMethod, true);
	}

}

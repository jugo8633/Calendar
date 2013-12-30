//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata.xml;


import org.w3c.dom.Node;
import androidx.xml.XMLBuilder;
import androidx.gdata.When;
import androidx.gdata.Reminder;
import androidx.util.TimeZoneUtil;

public class WhenDeserializer extends XMLBuilder<When> {

	private ReminderDeserializer reminderBuilder = new ReminderDeserializer();
	
	@Override
	public When build(Node whenNode) {
		this.node = whenNode;
		String startTime = TimeZoneUtil.getTimeByTimeZone(getAttribute("startTime"));
		String endTime = TimeZoneUtil.getTimeByTimeZone(getAttribute("endTime"));
		//String startTime = getAttribute("startTime");
		//String endTime = getAttribute("endTime");
		
	    Node reminderNode = getChildNode("gd:reminder");
	    Reminder[] reminders = new Reminder[1];
	    if( reminderNode != null) reminders[0] = reminderBuilder.build(reminderNode);

	    //List<Node> reminderNodes = getChildNodes("gd:reminder");
	    /*
	    Reminder[] reminders = new Reminder[reminderNodes.size()];
	    reminderNodes.toArray(reminders);
	    */
	    /*
	    Reminder[] reminders = new Reminder[reminderNodes.size()];
	    if(reminderNodes.size() != 0){
			for (int i=0; i<reminderNodes.size(); i++)	reminders[i] = reminderBuilder.build(reminderNodes.get(i));
	    } else {
	    	reminders = null;
	    }
	    */

		return new When(startTime, endTime, reminders);
	}

}

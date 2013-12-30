//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import java.io.Serializable;

/**
 * At most 5 reminders of an event
 * 
 * @author Luke Liu
 *
 */
public class Reminder implements Serializable{
	
	private String minutes, method;
	private boolean hasReminder;

	/**
	 * Constructor an Reminder; 
	 * 
	 * @param minutes
	 * @param method options: "email", "alert".
	 * @param recurrence true initialize a none empty Reminder, false initialize an empty Reminder. 
	 */
	public Reminder(String minutes, String method, boolean hasReminder) {
		if(hasReminder) {
			this.minutes = minutes;
			this.method = method;
		} 
		this.hasReminder = hasReminder;
	}
	
	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setHasReminder(boolean hasReminder) {
		this.hasReminder = hasReminder;
	}

	public boolean isHasReminder() {
		return hasReminder;
	}

}

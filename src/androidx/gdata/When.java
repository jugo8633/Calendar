//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import androidx.LogX;
import androidx.gdata.Reminder;

/**
 * startTime and endTime format please refer to RFC 3339 timestamp format
 * 
 * @author Luke Liu
 *
 */
public class When implements Serializable{
	
	private String startTime, endTime;
	private Reminder[] reminders;
	
	public When(String startTime, String endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Initialize When with reminders.
	 * 
	 * @param startTime
	 * @param endTime
	 * @param reminders
	 */
	public When(String startTime, String endTime, Reminder[] reminders) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.reminders = reminders;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setReminders(Reminder[] reminders) {
		this.reminders = reminders;
	}

	public Reminder[] getReminders() {
		return reminders;
	}
	
	private Date getDateTime(String strDateTime) {
		SimpleDateFormat simpleDateFormat = null;
		if(strDateTime.indexOf("T") != -1 )	simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		else	simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.parse(strDateTime, new ParsePosition(0));
	}

	/**
	 * Return a Date represent start time.
	 * 
	 * @return Date
	 */
	public Date getDateStart() {
		return getDateTime(startTime);
	}

	/**
	 * Return a Date represent end time.
	 * 
	 * @return Date
	 */
	public Date getDateEnd() {
		return getDateTime(endTime);
	}

	private GregorianCalendar getCalendarTime(Date date) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);
		return gregorianCalendar;
	}
	
	public GregorianCalendar getGregorianStart() {
		return getCalendarTime(getDateStart());
	}
	
	public GregorianCalendar getGregorianEnd() {
		return getCalendarTime(getDateEnd());
	}
	
}
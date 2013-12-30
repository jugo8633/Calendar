//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import android.os.Bundle;
import android.content.Intent;
import androidx.gdata.When;
import androidx.gdata.Where;
import androidx.gdata.Reminder;
import androidx.gdata.Recurrence;
import androidx.util.TimeZoneUtil;

/**
 * To add a recurrence event.
 * 
 * @author Luke Liu
 *
 */
public class AddRecurrenceEventCommand implements ICommand {

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;
	
	private String mStrTitle;
	private String mStrContent;
	private String mStrLocation;
	private String mStrStartTime;
	private String mStrEndTime;
	private String mStrFreq;
	private String mStrByDay;
	private String mStrUntil;
	private String mStrMinute;
	private String mStrMethod;
	private String mStrReminder;
	private String mStrRecurrence;
	
	public AddRecurrenceEventCommand(String[] strArguments, int intToken, BaseService baseService) {
		// Recurrence(boolean hasRecurrence, String dtStart, String dtEnd, String freq, String byDay, String until)
		// Event(title, content), Where(location), Recurrence, Reminder(minute, method, [true, false])
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mStrTitle = strArguments[0];
		this.mStrContent = strArguments[1];
		this.mStrLocation = strArguments[2];
		this.mStrStartTime = strArguments[3];
		this.mStrEndTime = strArguments[4];
		this.mStrFreq = strArguments[5];
		this.mStrByDay = strArguments[6];
		this.mStrUntil = strArguments[7];
		this.mStrMinute = strArguments[8];
		this.mStrMethod = strArguments[9];
		this.mStrReminder = strArguments[10];
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}
	
	public void execute() {
		// Initialize a Reminder or more than one Reminders.
		boolean booleanReminder = false;
		if(mStrReminder.compareTo(mStrReminder) == 0)	booleanReminder = true;
		if(mStrMinute.compareTo("On day of event") == 0) {
			Date dateReminder = getDateTime(mStrStartTime);
			GregorianCalendar gregorianCalendarReminder = new GregorianCalendar();
			gregorianCalendarReminder.setTime(dateReminder);
			mStrMinute = Integer.toString(gregorianCalendarReminder.get(Calendar.HOUR_OF_DAY) * 60 + gregorianCalendarReminder.get(Calendar.MINUTE));  
		}
		Reminder[] reminders = new Reminder[1];
		reminders[0] = new Reminder(mStrMinute, mStrMethod, booleanReminder);

		// Initialize a Recurrence.
		Recurrence recurrence = new Recurrence(true, getStrDateWithTimeZone(mStrStartTime), getStrDateWithTimeZone(mStrEndTime), mStrFreq, mStrByDay, mStrUntil);

		// Initialize a Where.
		Where where = new Where(mStrLocation);
		
		// Initialize an Event.
		Event event = new Event(mStrTitle, mStrContent, where, recurrence, reminders);

		// Post to Calendar for adding a new event of default calendar.
		Event eventNew = null;
		try {
			eventNew = mCalendarServer.postEventEntry("", event);
		} catch (IOException e) {
			Bundle bundleSend = new Bundle();
			eventNew = new Event(false);
			bundleSend.putSerializable("Data", eventNew);
			bundleSend.putInt("ErrorCode", Integer.parseInt(e.getMessage()));
			
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_ADD_RECURRENCE_EVENT);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);
			return;
		}
		
		// Get a response from Calendar, the response is a event entry, update cache.
		if(eventNew != null) {
			Bundle bundleSend = new Bundle();
			bundleSend.putSerializable("Data", eventNew);
			
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_ADD_RECURRENCE_EVENT);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);
		}		
	}
	
	private Date getDateTime(String strDateTime) {
		SimpleDateFormat simpleDateFormat = null;
		if(strDateTime.indexOf("T") != -1 )	simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		else	simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.parse(strDateTime, new ParsePosition(0));
	}
	
	/**
	 * Convert "yyyy-MM-ddT00:00:00" to "yyyy-MM-ddT00:00:00[+-]HH:mm"  
	 * 
	 * @param strDateStart
	 * @return
	 */
	public String getStrDateWithTimeZone(String strDate) {
		StringBuilder stringBuilderDate = new StringBuilder();
		stringBuilderDate.append(strDate);
		stringBuilderDate.append(TimeZoneUtil.getTimeZoneOffset());
		return stringBuilderDate.toString();
	}


}

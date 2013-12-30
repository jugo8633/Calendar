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
import android.content.Intent;
import android.os.Bundle;
import androidx.gdata.Recurrence;
import androidx.gdata.Reminder;
import androidx.gdata.When;
import androidx.gdata.Where;
import androidx.util.TimeZoneUtil;

/**
 * Update Event with fields
 * 
 * @author Luke Liu
 *
 */
public class UpdateEventCommand implements ICommand {

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;
	
	private String mStrCalendar;
	private String mStrEventId;
	private String mStrTitle;
	private String mStrContent;
	
	private String mStrLocation;
	
	private String mStrStartTime;
	private String mStrEndTime;
	
	private String mStrFreq;
	private String mStrByDay;
	private String mStrUntil;
	private String mStrRecurrence;
	
	private String mStrMinute;
	private String mStrMethod;
	private String mStrReminder;

	public UpdateEventCommand(String[] strArguments, int intToken, BaseService baseService) {
		// Recurrence(boolean hasRecurrence, String dtStart, String dtEnd, String freq, String byDay, String until)
		// Event(Calendar, event id, title, content), Where(location), When(start time, end time), Recurrence([DAILY, WEEKLY, MONTHLY, YEARLY], [SU, MO, TU, WE, TH, FR, SA, ""], end time, [true, false]), Reminder(minute, [email, alert], [true, false])
		// Parameters from Activity:
		// String[] strArgumentUpdateEvent = {"default", "f3dkufm6n66g4v8nhre7l6i9tc", "Tennis with Beth", "Meet for a quick lesson.", "Rolling Lawn Courts", "20090927T200000", "20090927T210000", "WEEKLY", "TU,TH", "20091231","false", "30", "alert", "true"};
		this.intToken = intToken;
		this.mBaseService = baseService;
		
		this.mStrCalendar = strArguments[0];
		this.mStrEventId = strArguments[1];
		this.mStrTitle = strArguments[2];
		this.mStrContent = strArguments[3];
		this.mStrLocation = strArguments[4];
		this.mStrStartTime = strArguments[5];
		this.mStrEndTime = strArguments[6];
		this.mStrFreq = strArguments[7];
		this.mStrByDay = strArguments[8];
		this.mStrUntil = strArguments[9];
		this.mStrRecurrence = strArguments[10];	// true or false
		this.mStrMinute = strArguments[11];
		this.mStrMethod = strArguments[12];
		this.mStrReminder = strArguments[13];	// true or false
		
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}
	
	public void execute() {
		// Initialize a Reminder or more than one Reminders.
		boolean booleanReminder = false;
		if(mStrReminder.compareTo("true") == 0)	booleanReminder = true;
		if(mStrMinute.compareTo("On day of event") == 0) {
			Date dateReminder = getDateTime(mStrStartTime);
			GregorianCalendar gregorianCalendarReminder = new GregorianCalendar();
			gregorianCalendarReminder.setTime(dateReminder);
			mStrMinute = Integer.toString(gregorianCalendarReminder.get(Calendar.HOUR_OF_DAY) * 60 + gregorianCalendarReminder.get(Calendar.MINUTE));  
		}
		Reminder[] reminders = new Reminder[1];
		reminders[0] = new Reminder(mStrMinute, mStrMethod, booleanReminder);

		// Initialize a Recurrence.
		boolean booleanRecurrence = false;
		When when = null;
		if(mStrRecurrence.compareTo("true") == 0)	booleanRecurrence = true;
		else	when = new When(getStrDateWithTimeZone(mStrStartTime), getStrDateWithTimeZone(mStrEndTime), reminders);
		
		Recurrence recurrence = new Recurrence(booleanRecurrence, mStrStartTime, mStrEndTime, mStrFreq, mStrByDay, mStrUntil);

		// Initialize a Where.
		Where where = new Where(mStrLocation);
		
		// Initialize an Event.
		Event event = null;
		if(recurrence.isHasRecurrence())
			event = new Event(mStrTitle, mStrContent, where, recurrence, reminders);
		else
			event = new Event(mStrTitle, mStrContent, where, when);
		
		// Initialize url.
		StringBuilder stringBuilderEventId = new StringBuilder("http://www.google.com/calendar/feeds/");
		stringBuilderEventId.append(mStrCalendar);
		stringBuilderEventId.append("/private/full/");
		stringBuilderEventId.append(mStrEventId);
		event.setId(stringBuilderEventId.toString());

		// Post to Calendar for adding a new event of default calendar.
		Event eventNew = null;
		try {
			eventNew = mCalendarServer.updateEventEntryFeed(event.getId(), event);
		} catch (IOException e) {
			Bundle bundleSend = new Bundle();
			eventNew = new Event(false);
			bundleSend.putSerializable("Data", eventNew);
			bundleSend.putInt("ErrorCode", Integer.parseInt(e.getMessage()));
			
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_UPDATE_EVENT);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);
			return;
		}
		
		// Get a response from Calendar, the response is a event entry, update cache.
		if(eventNew != null) {
			Bundle bundleSend = new Bundle();
			bundleSend.putSerializable("Data", eventNew);
			
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_UPDATE_EVENT);
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

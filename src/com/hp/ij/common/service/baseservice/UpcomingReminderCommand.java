//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import java.io.IOException;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.AllEvent;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import com.hp.ij.common.service.gdata.client.calendar.EventFeed;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedFree;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import com.hp.ij.common.service.gdata.client.calendar.SingleDayEvent;
import com.hp.ij.common.service.gdata.client.calendar.WidgetEvent;
import android.content.Intent;
import android.os.Bundle;
import androidx.LogX;
import androidx.util.TimeZoneUtil;

public class UpcomingReminderCommand implements ICommand {
	
	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;

	/**
	 * Date start and end.
	 */
	private String mStrDateStart, mStrDateEnd;
	
	/**
	 * Which calendar, [{Calendar}, "default"]. 
	 */
	private String mStrCalendar;
	
	/**
	 * Visibility, ["private", "public"].
	 */
	private String mStrVisibility;
	
	/**
	 * Calendar projection, "FREE_BUSY". 
	 */
	private EventFeedProjection mProjection;

	public UpcomingReminderCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mStrDateStart = strArguments[0];
		this.mStrDateEnd = strArguments[1];
		this.mStrCalendar = strArguments[2];
		this.mStrVisibility = strArguments[3]; 
		
		if(strArguments[4].compareTo("FULL") == 0) this.mProjection = EventFeedProjection.FULL;
		else if(strArguments[4].compareTo("FREE_BUSY") == 0) this.mProjection = EventFeedProjection.FREE_BUSY;
		else this.mProjection = EventFeedProjection.FREE_BUSY; 
		
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}

	public void execute() {
		SingleDayEvent singleDayEvent = null;
		CalendarFeed calendarFeed;
		if(CalendarState.getCalendarFeed() == null) return;
		else calendarFeed = CalendarState.getCalendarFeed();

		EventFeed[] eventFeeds = new EventFeed[calendarFeed.getmCalendars().length];
		try {
			for (int i = 0; i < eventFeeds.length; i++) {
				if(calendarFeed.getmCalendars()[0] == null) return;
				String strCalendar = calendarFeed.getmCalendars()[i].getmId();
				LogX.i("Start to query calendar(for Reminder) with start time: " + getStrDateStart(mStrDateStart) + " end time: " + getStrDateEnd(mStrDateStart));
				eventFeeds[i] = mCalendarServer.getEventFeed(
						strCalendar, mStrVisibility, mProjection,
						mStrDateStart, mStrDateEnd, mBaseService);
			}

			singleDayEvent = new SingleDayEvent(eventFeeds);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		for(int i=0; i<singleDayEvent.getmEventFeeds().length; i++) {
			if(i != 0)	continue;
			for(int j=0; j<singleDayEvent.getmEventFeeds()[i].getEvents().length; j++) {
				Event event = singleDayEvent.getmEventFeeds()[i].getEvents()[j];
				if(event.getWhen().getReminders()[0] != null) {
					SimpleDateFormat simpleDateFormat;
					if(event.getWhen().getStartTime().indexOf("T") == -1)
						simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					else
						simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
 
					Date dateStartTime = simpleDateFormat.parse(getStrDateStart(event.getWhen().getStartTime()), new ParsePosition(0));
					GregorianCalendar gregorianCalendar = new GregorianCalendar();
					GregorianCalendar gregorianCalendarNow = new GregorianCalendar();
					GregorianCalendar gregorianCalendarNowEnd = (GregorianCalendar)gregorianCalendar.clone();
					gregorianCalendar.setTime(dateStartTime);
					gregorianCalendar.add(Calendar.MINUTE, 0 - Integer.parseInt(event.getWhen().getReminders()[0].getMinutes()));
					final int intMinuteToRemind = 5;
					gregorianCalendarNowEnd.add(Calendar.MINUTE, intMinuteToRemind);
					if((gregorianCalendar.compareTo(gregorianCalendarNow) >= 0) && ((gregorianCalendar.compareTo(gregorianCalendarNowEnd) <= 0))) {
						ArrayList<String> arrayListHeaderName = new ArrayList<String>();
						ArrayList<String> arrayListContent = new ArrayList<String>();
						
						final String strContent = event.getTitle() + " is starting at " + event.getWhen().getStartTime() + " in " + event.getWhere().getValueString();
						arrayListHeaderName.add("Event Title");
						arrayListContent.add(strContent);

						Intent intentReminder = new Intent(IntentCommand.INTENT_ACTION);
						Bundle bundleSend = new Bundle();
						
						final String b_Header = "b_Header";	
					    final String b_Content = "b_Content";  
						bundleSend.putStringArrayList(b_Header, arrayListHeaderName);
				        bundleSend.putStringArrayList(b_Content, arrayListContent);              
				        intentReminder.putExtras(bundleSend);                               
				        intentReminder.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				        intentReminder.setFlags(IntentCommand.GOOGLE_CALENDAR_REMINDER);
				        mBaseService.sendBroadcast(intentReminder);
				        LogX.i("Reminder: " + strContent);
					}
					
				}
				
			}
		}
		/*
		ArrayList<WidgetEvent> widgetEvents = new ArrayList<WidgetEvent>();
		
		for(int i=0; i<singleDayEvent.getmEventFeeds().length; i++) {
			for(int j=0; j<singleDayEvent.getmEventFeeds()[i].getEvents().length; j++) {
				Event event = singleDayEvent.getmEventFeeds()[i].getEvents()[j];
				String strCalendarTitle = singleDayEvent.getmEventFeeds()[i].getTitle();
				if(event.getReminders()[0]. == null)	break;
				
					
				String strEventStart = event.getWhen().getStartTime();
				String str

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				Date date = simpleDateFormat.parse(getStrDateStart(strDate), new ParsePosition(0));

				
				widgetEvents.add(new WidgetEvent(strCalendarTitle, event.getTitle(), event.getWhen().getStartTime(), event.getWhen().getEndTime()));
			}
		}
		
		Collections.sort(widgetEvents);
		*/
	}

	/**
	 * Convert "yyyy-MM" to yyyy-MM-01T00:00:00
	 * 
	 * @param strDateStart
	 * @return
	 */
	public String getStrDateStart(String strDate) {
		StringBuilder stringBuilderDate = new StringBuilder();
		stringBuilderDate.append(strDate + "-01T00:00:00");
		stringBuilderDate.append(TimeZoneUtil.getTimeZoneOffset());
		return stringBuilderDate.toString();
	}
	
	/**
	 * Convert DateEnd from "yyyy-MM" to "yyyy-'MM+1'-01T00:00:00".
	 * 
	 * @param strDate
	 * @return DateEnd with format "yyyy-MM-ddThh:mm:ss"
	 */
	public String getStrDateEnd(String strDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = simpleDateFormat.parse(getStrDateStart(strDate), new ParsePosition(0));
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);
		gregorianCalendar.add(Calendar.MONTH, 1);
		gregorianCalendar.add(Calendar.SECOND, -1);
		StringBuffer stringBufferDate = new StringBuffer();
		simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(1));
		stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset());
		return stringBufferDate.toString();
	}

}

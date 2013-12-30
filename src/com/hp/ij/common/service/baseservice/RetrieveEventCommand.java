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

/**
 * Retrieve Events with specify Calendar, Visibility, Projection
 * 
 * @author Luke Liu
 *
 */
public class RetrieveEventCommand implements ICommand {

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
	 * Calendar projection, ["FULL", "FREE_BUSY"]. 
	 */
	private EventFeedProjection mProjection;
	
	/**
	 * Initialize retrieve event command. 
	 * 
	 * @param strArguments, string index from 0 StrDateStart, StrDateEnd, Calendar, Visibility, Projection.
	 * @param intToken, token send to Activity.
	 * @param baseService, pass a BaseService for send Intent.
	 */
	public RetrieveEventCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mStrDateStart = strArguments[0];
		this.mStrDateEnd = strArguments[1];
		this.mStrCalendar = strArguments[2];
		this.mStrVisibility = strArguments[3]; 
		
		if(strArguments[4].compareTo("FULL") == 0) this.mProjection = EventFeedProjection.FULL;
		else if(strArguments[4].compareTo("FREE_BUSY") == 0) this.mProjection = EventFeedProjection.FREE_BUSY;
		else this.mProjection = EventFeedProjection.FULL;
		
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}
	
	/**
	 * Iterate all calendars for retrieve its events, then generate a EventFeed[]. 
	 */
	public void execute() {
		CalendarFeed calendarFeed;
		if(CalendarState.getCalendarFeed() == null) return;
		else calendarFeed = CalendarState.getCalendarFeed();

		EventFeed[] eventFeeds = new EventFeed[calendarFeed.getmCalendars().length];
		SingleDayEvent singleDayEvent = null;
		
		try {
			/*
			 * Exclusive all-day events(start:04-27 end:04-28) before today
			 */
			for (int i = 0; i < eventFeeds.length; i++) {
				if(calendarFeed.getmCalendars()[0] == null) return;
				String strCalendar = calendarFeed.getmCalendars()[i].getmId();
				EventFeed eventFeed = mCalendarServer.getEventFeed(
						strCalendar, mStrVisibility, mProjection,
						getStrDate(mStrDateStart), getStrDateEnd(mStrDateStart), mBaseService);
				LogX.i("Start to query calendar(day view) with date start: " + getStrDate(mStrDateStart) + " date end: " + getStrDateEnd(mStrDateStart));
				
				if(eventFeed != null) {
					ArrayList<Event> arrayListOfEvent = new ArrayList<Event>();
					for(int j=0; j<eventFeed.getEvents().length; j++) {
						if(!eventFeed.getEvents()[j].getWhen().getEndTime().equalsIgnoreCase(mStrDateStart)) {
							eventFeed.getEvents()[j].checkEditOrNot(calendarFeed.getmCalendars()[i].getmAccessLevel());
							arrayListOfEvent.add(eventFeed.getEvents()[j]);
						}
					}
					Event[] events = arrayListOfEvent.toArray(new Event[0]);
					eventFeed.setEvent(events);
					eventFeeds[i] = eventFeed;
				}
				
			}

			singleDayEvent = new SingleDayEvent(eventFeeds);
		} catch (IOException e) {
			Bundle bundleException = new Bundle();
			singleDayEvent = new SingleDayEvent(false, Integer.parseInt(e.getMessage()));
			bundleException.putSerializable("Data", singleDayEvent);
			Intent intentException = new Intent(IntentCommand.INTENT_ACTION);
			intentException.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT);
			intentException.putExtras(bundleException);
			mBaseService.sendBroadcast(intentException);
			return;
		}
		
		singleDayEvent.setStrMYearMonthDay(mStrDateStart);
		
		// ----- debug -----
		for (int i = 0; i < singleDayEvent.getmEventFeeds().length; i++ ) {
			LogX.i("Calendar(Day view): " + singleDayEvent.getmEventFeeds()[i].getTitle());
			for (int j = 0; j < singleDayEvent.getmEventFeeds()[i].getEvents().length; j++) {
				LogX.i("	Event(Day view): " + singleDayEvent.getmEventFeeds()[i].getEvents()[j].getTitle() + " date start:" + singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhen().getDateStart() + " date end: " + singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhen().getDateEnd());
			}
		}
		// ----- debug -----
		
		Bundle bundleSend = new Bundle();
		bundleSend.putSerializable("Data", singleDayEvent);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT);
		intentSend.putExtras(bundleSend);
		
		mBaseService.sendBroadcast(intentSend);
	}

	/**
	 * Convert "yyyy-MM-dd" to yyyy-MM-ddT00:00:00
	 * 
	 * @param strDateStart
	 * @return
	 */
	public String getStrDate(String strDate) {
		StringBuilder stringBuilderDate = new StringBuilder();
		stringBuilderDate.append(strDate + "T00:00:00");
		stringBuilderDate.append(TimeZoneUtil.getTimeZoneOffset());
		return stringBuilderDate.toString();
	}
	
	public String getStrDateEnd(String strDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = simpleDateFormat.parse(getStrDate(strDate), new ParsePosition(0));
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);
		gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
		gregorianCalendar.add(Calendar.SECOND, -1);
		StringBuffer stringBufferDate = new StringBuffer();
		simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(1));
		stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset());
		return stringBufferDate.toString(); 
	}

}

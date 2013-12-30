//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.text.FieldPosition;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.AllEvent;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedFree;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import android.content.Intent;
import android.os.Bundle;
import androidx.LogX;
import androidx.util.TimeZoneUtil;

/**
 * Retrieve reduced Events with specify Calendar, Visibility, Projection
 * 
 * @author Luke Liu
 *
 */
public class RetrieveEventFreeCommand implements ICommand {

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
	
	/**
	 * Indicate logout or not
	 */
	private static boolean boolBoardcast;
	
	public RetrieveEventFreeCommand(String[] strArguments, int intToken, BaseService baseService) {
		RetrieveEventFreeCommand.setBoolLogout(false);
		
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
		String strDateIndex = getStrDateStart(mStrDateStart);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = simpleDateFormat.parse(getStrDateEnd(strDateIndex), new ParsePosition(0));
		int intCacheMonthCount = 6;
		GregorianCalendar[] gregorianCalendars = new GregorianCalendar[intCacheMonthCount * 2 + 1];
		for(int i=0; i<gregorianCalendars.length; i++)	gregorianCalendars[i] = new GregorianCalendar();
		
		RetrieveEventFreeCommand.setBoolLogout(true);
		
		// spiral month cache
		gregorianCalendars[0].setTime(date);
		for(int i=1; i<=intCacheMonthCount; i++) {
			gregorianCalendars[2*i - 1].setTime(date);
			gregorianCalendars[2*i].setTime(date);
			
			gregorianCalendars[2*i - 1].add(Calendar.MONTH, i);
			gregorianCalendars[2*i].add(Calendar.MONTH, 0-i);
		}

		for(int i=0; i<gregorianCalendars.length; i++) {
			StringBuffer stringBufferDate = new StringBuffer();
			mStrDateStart = simpleDateFormat.format(gregorianCalendars[i].getTime(), stringBufferDate, new FieldPosition(1)).toString().substring(0, 7);
			if(RetrieveEventFreeCommand.isBoolLogout())	executeWithoutCache();
			else break;
		}
	}

	/**
	 * Convert "yyyy-MM" to yyyy-MM-01T00:00:00[+,-]hh:mm
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
	 * Convert DateEnd from "yyyy-MM" to "yyyy-'MM+1'-01T00:00:00[+,-]hh:mm".
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

	/**
	 * Iterate all calendars for retrieve its events, then generate a EventFeedFree[]. 
	 */
	private void executeWithCache() {
		// Check whether cache exist?
		AllEvent allEvent = null;
		if(CalendarState.queryCache(mStrDateStart)) allEvent = CalendarState.getAllEvent(mStrDateStart);
		else {
			// Cache not exist, retrieve cache and write to cache.
			EventFeedFree[] eventFeedFrees = new EventFeedFree[CalendarState.getCalendarFeed().getmCalendars().length];
			try {
				for (int i = 0; i < eventFeedFrees.length; i++) {
					String strCalendar = CalendarState.getCalendarFeed()
					.getmCalendars()[i].getmId();
					eventFeedFrees[i] = mCalendarServer.getEventFeedFree(
							strCalendar, mStrVisibility, mProjection,
							getStrDateStart(mStrDateStart), getStrDateEnd(mStrDateStart), mBaseService);
					LogX.i("Start to query calendar with start time: " + getStrDateStart(mStrDateStart) + " end time: " + getStrDateEnd(mStrDateStart));
				}

				allEvent = new AllEvent(eventFeedFrees);
			} catch (IOException e) {
				Bundle bundleSend = new Bundle();
				allEvent = new AllEvent(false, Integer.parseInt(e.getMessage()));
				bundleSend.putSerializable("Data", allEvent);
				Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
				intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT_FREE);
				intentSend.putExtras(bundleSend);
				
				mBaseService.sendBroadcast(intentSend);	
				return;
			}
			
			allEvent.setmStrYearMonth(mStrDateStart);
			CalendarState.setAllEvent(allEvent);
		}
		
		Bundle bundleSend = new Bundle();
		bundleSend.putSerializable("Data", allEvent);
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT_FREE);
		intentSend.putExtras(bundleSend);
		
		mBaseService.sendBroadcast(intentSend);		
	}
	
	/**
	 * Iterate all calendars for retrieve its events, then generate a EventFeedFree[]. 
	 */
	private void executeWithoutCache() {
		AllEvent allEvent = null;
		
		CalendarFeed calendarFeed;
		if(CalendarState.getCalendarFeed() == null) return;
		else calendarFeed = CalendarState.getCalendarFeed();

		EventFeedFree[] eventFeedFrees = new EventFeedFree[calendarFeed.getmCalendars().length];
		try {
			for (int i = 0; i < eventFeedFrees.length; i++) {
				if(calendarFeed.getmCalendars()[0] == null) return;
				String strCalendar = calendarFeed.getmCalendars()[i].getmId();
				eventFeedFrees[i] = mCalendarServer.getEventFeedFree(strCalendar, mStrVisibility, mProjection, getStrDateStart(mStrDateStart), getStrDateEnd(mStrDateStart), mBaseService);
				LogX.i("Start to query calendar(EventFree) with start time: " + getStrDateStart(mStrDateStart) + " end time: " + getStrDateEnd(mStrDateStart) + " in iteration:" + i);
			}

			allEvent = new AllEvent(eventFeedFrees);
			allEvent.setmStrYearMonth(mStrDateStart);
			CalendarState.setAllEvent(allEvent);

			// ----- debug -----
			for (int i = 0; i < allEvent.getmEventFeedFrees().length; i++ ) {
				LogX.i("Calendar: " + allEvent.getmEventFeedFrees()[i].getTitle());
				for (int j = 0; j < allEvent.getmEventFeedFrees()[i].getEvents().length; j++) {
					LogX.i("	Event: " + allEvent.getmEventFeedFrees()[i].getEvents()[j].getTitle() + " start:" + allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getStartTime() + " end: " + allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getEndTime());
				}
			}
			// ----- debug -----
			
			Bundle bundleSend = new Bundle();
			bundleSend.putSerializable("Data", allEvent);
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT_FREE);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);		
		} catch (IOException e) {
			Bundle bundleSend = new Bundle();
			allEvent = new AllEvent(false, Integer.parseInt(e.getMessage()));
			bundleSend.putSerializable("Data", allEvent);
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT_FREE);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);
			return;
		}
		
	}

	public static void setBoolLogout(boolean boolLogout) {
		RetrieveEventFreeCommand.boolBoardcast = boolLogout;
	}

	public static boolean isBoolLogout() {
		return boolBoardcast;
	}
	
}

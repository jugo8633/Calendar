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
import com.hp.ij.calendar.CalendarWidgetBroadcast;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedFree;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import com.hp.ij.common.service.gdata.client.calendar.EventFree;
import com.hp.ij.common.service.gdata.client.calendar.WidgetEvent;
import android.content.Intent;
import android.os.Bundle;
import androidx.LogX;
import androidx.util.TimeZoneUtil;

public class UpcomingAlarmCommand implements ICommand {

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

	public UpcomingAlarmCommand(String[] strArguments, int intToken, BaseService baseService) {
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
		AllEvent allEvent = null;
		
		CalendarFeed calendarFeed;
		if(CalendarState.getCalendarFeed() == null) return;
		else calendarFeed = CalendarState.getCalendarFeed();

		EventFeedFree[] eventFeedFrees = new EventFeedFree[calendarFeed.getmCalendars().length];
		try {
			for (int i = 0; i < eventFeedFrees.length; i++) {
				if(calendarFeed.getmCalendars()[0] == null) return;
				String strCalendar = calendarFeed.getmCalendars()[i].getmId();
				eventFeedFrees[i] = mCalendarServer.getEventFeedFree(
						strCalendar, mStrVisibility, mProjection,
						getStrDateStart(mStrDateStart), getStrDateEnd(mStrDateStart), mBaseService);
				LogX.i("Start to query calendar(for alarm) with start time: " + getStrDateStart(mStrDateStart) + " end time: " + getStrDateEnd(mStrDateStart));
			}

			allEvent = new AllEvent(eventFeedFrees);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		allEvent.setmStrYearMonth(mStrDateStart);

		Bundle bundleSend = new Bundle();
		bundleSend.putSerializable("Data", allEvent);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_UPDATE_ALARM);
		intentSend.putExtras(bundleSend);
		
		mBaseService.sendBroadcast(intentSend);		
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

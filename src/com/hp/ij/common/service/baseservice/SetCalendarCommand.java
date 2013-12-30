//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.Calendar;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import android.content.Intent;
import android.os.Bundle;

/**
 * Choose which calendar's event, user want to see it.
 * 
 * @author Luke Liu
 *
 */
public class SetCalendarCommand implements ICommand{

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;
	private String[] mStrCalendarIds;

	public SetCalendarCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
		this.mStrCalendarIds = strArguments;
	}
	
	public void execute() {
		CalendarFeed calendarFeed = CalendarState.getMCalendarFeedOriginal().clone();
		Calendar[] calendars;
		if(mStrCalendarIds.length != 0) {
			calendars = new Calendar[mStrCalendarIds.length];
			for(int i=0; i<mStrCalendarIds.length; i++) {
				for(int j=0; j<calendarFeed.getmCalendars().length; j++) {
					if(this.mStrCalendarIds[i].compareTo(calendarFeed.getmCalendars()[j].getmId()) == 0)	calendars[i] = calendarFeed.getmCalendars()[j];
				}
			}
		}
		else calendars = null;
		
		calendarFeed.setmCalendars(calendars);
		CalendarState.setCalendarFeed(calendarFeed);
		
		Bundle bundleSend = new Bundle();
		bundleSend.putSerializable("Data", calendarFeed);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_SET_CALENDAR);
		intentSend.putExtras(bundleSend);
		
		mBaseService.sendBroadcast(intentSend);
	}

}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import java.io.IOException;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import com.hp.ij.common.service.gdata.client.calendar.EventFeed;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import com.hp.ij.common.service.gdata.client.calendar.SingleDayEvent;
import android.content.Intent;
import android.os.Bundle;

/**
 * Retrieve single Event with specify Calendar, Visibility, Projection, EId
 * 
 * @author Luke Liu
 *
 */
public class RetrieveSingleEventCommand implements ICommand {

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;

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
	 * EventId
	 */
	private String mStrEventId;
	
	public RetrieveSingleEventCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mStrCalendar = strArguments[0];
		this.mStrVisibility = strArguments[1]; 
		
		if(strArguments[2].compareTo("FULL") == 0) this.mProjection = EventFeedProjection.FULL;
		else if(strArguments[2].compareTo("FREE_BUSY") == 0) this.mProjection = EventFeedProjection.FREE_BUSY;
		else this.mProjection = EventFeedProjection.FULL;
		
		this.mStrEventId = strArguments[3];
		
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}
	
	public void execute() {
		Event event = null;
		try {
			event = mCalendarServer.getSingleEvent(mStrCalendar, mStrVisibility, mProjection, mStrEventId, mBaseService);
		} catch (IOException e) {
			event = new Event(false);
			Bundle bundleSend = new Bundle();
			bundleSend.putSerializable("Data", event);
			bundleSend.putInt("ErrorCode", Integer.parseInt(e.getMessage()));
			
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_SINGLE_EVENT);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);
			return;
		}
		Bundle bundleSend = new Bundle();
		bundleSend.putSerializable("Data", event);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_SINGLE_EVENT);
		intentSend.putExtras(bundleSend);
		
		mBaseService.sendBroadcast(intentSend);
	}

}

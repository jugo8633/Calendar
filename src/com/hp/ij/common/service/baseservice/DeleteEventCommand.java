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
import com.hp.ij.common.service.gdata.client.calendar.AllEvent;
import com.hp.ij.common.service.gdata.client.calendar.BundleDeleteReturn;
import com.hp.ij.common.service.gdata.client.calendar.BundleLogin;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import android.content.Intent;
import android.os.Bundle;
import androidx.LogX;

/**
 * Delete event with regardless of whether updated or not.
 * 
 * @author Luke Liu
 *
 */
public class DeleteEventCommand implements ICommand {

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;
	
	private String mStrCalendar;
	private String mStrVisibility;
	private EventFeedProjection mProjection;
	private String mStrEid;

	public DeleteEventCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mStrCalendar= strArguments[0];
		this.mStrVisibility = strArguments[1];
		
		if(strArguments[2].compareTo("FULL") == 0) this.mProjection = EventFeedProjection.FULL;
		else if(strArguments[2].compareTo("FREE_BUSY") == 0) this.mProjection = EventFeedProjection.FREE_BUSY;
		else this.mProjection = EventFeedProjection.FULL;

		this.mStrEid = strArguments[3];
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}
	
	public void execute() {
		boolean boolDeleteSuccess = false;
		try {
			boolDeleteSuccess = mCalendarServer.deleteEvent(mStrCalendar, mStrVisibility, mProjection, mStrEid);
		} catch (IOException e) {
			Bundle bundleSend = new Bundle();
			BundleDeleteReturn bundleDeleteReturn = new BundleDeleteReturn(false, Integer.parseInt(e.getMessage()));
			bundleSend.putSerializable("Data", bundleDeleteReturn);
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_DELETE_EVENT);
			intentSend.putExtras(bundleSend);

			mBaseService.sendBroadcast(intentSend);
			return;
		}
		
		Bundle bundleSend = null;
		BundleDeleteReturn bundleDeleteReturn;
		
		if(boolDeleteSuccess)	bundleDeleteReturn = new BundleDeleteReturn(true);
		else	bundleDeleteReturn = new BundleDeleteReturn(false);
		
		bundleSend = new Bundle();
		bundleDeleteReturn = new BundleDeleteReturn(true);
		bundleSend.putSerializable("Data", bundleDeleteReturn);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_DELETE_EVENT);
		intentSend.putExtras(bundleSend);

		mBaseService.sendBroadcast(intentSend);
	}

}

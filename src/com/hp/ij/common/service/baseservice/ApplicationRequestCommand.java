//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;

/**
 * Accept request from other application through Intent
 * 
 * @author Luke Liu
 *
 */
public class ApplicationRequestCommand implements ICommand {

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;

	public ApplicationRequestCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		if(CalendarState.getGaiaSession() == null) return;	// not log-in yet
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}

	public void execute() {
		if(mCalendarServer == null)	return;
		String strStartTime = "";
		String strEndTime = "";
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		StringBuffer stringBufferDate = new StringBuffer();
		simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(0));
		strStartTime =  stringBufferDate.substring(0, "yyyy-MM".length());
		strEndTime = strStartTime;

		String strCalendar = "default";
		String strVisibility = "private";
		String strProjection = "FREE_BUSY";
		String[] strUpcomingArguments = {strStartTime, strEndTime, strCalendar, strVisibility, strProjection};

		UpcomingAlarmCommand upcomingAlarmCommand = new UpcomingAlarmCommand(strUpcomingArguments, intToken, mBaseService);
		upcomingAlarmCommand.execute();
	}

}

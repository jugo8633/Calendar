//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.calendar.CalendarWidgetBroadcast;
import com.hp.ij.common.service.gdata.client.calendar.WidgetEvent;

import android.content.Intent;
import android.os.Bundle;
import androidx.LogX;
import androidx.util.TimeZoneUtil;

public class LogoutCommand implements ICommand {

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;

	public LogoutCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);
	}

	public void execute() {
		/*
		RetrieveCalendarCommand.stopUpdateWidgetAlarm();
		RetrieveEventFreeCommand.setBoolLogout(false);
		CalendarState.clearCalendarState();
		*/
		Bundle bundleSend = new Bundle();
		bundleSend.putBoolean("Data", true);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_LOGOUT);
		intentSend.putExtras(bundleSend);
		
		mBaseService.sendBroadcast(intentSend);
		
		//----- signal calendar widget -----
		CalendarWidgetBroadcast broadcast = new CalendarWidgetBroadcast();
		broadcast.broadcastMsg(mBaseService, CalendarWidgetBroadcast.CALENDAR_LOGOUT);

		//android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import java.io.IOException;
import java.lang.Thread;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import android.content.Intent;
import android.os.Bundle;
import androidx.LogX;
import androidx.util.TimeZoneUtil;

/**
 * Retrieve allcalendar or owncalendars command
 * 
 * @author Luke Liu
 *
 */
public class RetrieveCalendarCommand implements ICommand {

	private int intToken;
	private BaseService mBaseService;
	private CalendarServer mCalendarServer;
	private static boolean mHasThreadWidgetUpdate = false;
	private static boolean mHasThreadAlarmUpdate = false;
	private static boolean mHasThreadReminderUpdate = false;
	
	/**
	 * Which calendar, [{Calendar}, "default"]. 
	 */
	private String mStrCalendar;
	
	/**
	 * Visibility, ["allcalendars", "owncalendars"].
	 */
	private String mStrVisibility;
	
	/**
	 * Calendar projection, "FULL" only. 
	 */
	private EventFeedProjection mProjection;
	

	private static final int intPoolSize = 3;
	private final int intMaxPoolSize = 2;
	private final int intTimeMinute = 35;
	private final long longKeepAliveTime = 60 * intTimeMinute;
	private static ThreadPoolExecutor THREAD_POOL_EXECUTOR;
	private static ExecutorService EXECUTOR_SERVICE;
	private static Thread[] threads = new Thread[intPoolSize];
	private static ArrayBlockingQueue<Runnable> BLOCKING_QUEUE;
	
	/**
	 * Initialize retrieve event command. 
	 * 
	 * @param strArguments, string index from 0 StrDateStart, StrDateEnd, Calendar, Visibility, Projection.
	 * @param intToken, token send to Activity.
	 * @param baseService, pass a BaseService for send Intent.
	 */
	public RetrieveCalendarCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.intToken = intToken;
		this.mBaseService = baseService;
		this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(), CalendarState.getGaiaSession().getToken(), mBaseService);

		this.mStrCalendar = strArguments[0];
		this.mStrVisibility = strArguments[1];
		this.mProjection = EventFeedProjection.FULL;
	}
	
	public void execute() {
		CalendarFeed calendarFeed;
		try {
			calendarFeed = mCalendarServer.getCalendarFeed(mStrCalendar, mStrVisibility, mProjection, mBaseService);
			// Don't replace the cache CalendarFeed, if exists.
			if(CalendarState.getCalendarFeed() == null) {
				CalendarState.setCalendarFeed(calendarFeed);
				CalendarState.copyCalendarFeed();
			}
			
			Bundle bundleSend = new Bundle();
			bundleSend.putSerializable("Data", calendarFeed);
			
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_CALENDAR);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);
		} catch (IOException e) {
			e.printStackTrace();
			calendarFeed = new CalendarFeed(false, Integer.parseInt(e.getMessage()));
			Bundle bundleSend = new Bundle();
			bundleSend.putSerializable("Data", calendarFeed);
			
			Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
			intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_RETRIEVE_CALENDAR);
			intentSend.putExtras(bundleSend);
			
			mBaseService.sendBroadcast(intentSend);
		}
		
		// construct three thread for broadcasting data
		if(EXECUTOR_SERVICE == null) {
			EXECUTOR_SERVICE = Executors.newFixedThreadPool(intPoolSize);
			
			mHasThreadWidgetUpdate = true;
			threads[0] = threadWidgetUpdate;
			threadWidgetUpdate.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			EXECUTOR_SERVICE.execute(threadWidgetUpdate);

			mHasThreadAlarmUpdate = true;
			threads[1] = threadAlarmUpdate;
			threadAlarmUpdate.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			EXECUTOR_SERVICE.execute(threadAlarmUpdate);
			
			mHasThreadReminderUpdate = true;
			threads[2] = threadReminderUpdate;
			threadReminderUpdate.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			EXECUTOR_SERVICE.execute(threadReminderUpdate);

		}
	}
	
	private Thread threadWidgetUpdate = new Thread() {

		public void run() {
			while(mHasThreadWidgetUpdate) {
				try {
					String strStartTime = "";
					String strEndTime = "";
					GregorianCalendar gregorianCalendar = new GregorianCalendar();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					StringBuffer stringBufferDate = new StringBuffer();
					simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(0));
					strStartTime = stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset()).toString();

					gregorianCalendar = getWidgetEndTime(gregorianCalendar);
					stringBufferDate.delete(0, stringBufferDate.length());
					simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(0));
					strEndTime = stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset()).toString();

					String strCalendar = "default";
					String strVisibility = "private";
					String[] strUpcomingArguments = {strStartTime, strEndTime, strCalendar, strVisibility};
					int intToken = 0;

					UpcomingWidgetCommand upcompingWidgetCommand = new UpcomingWidgetCommand(strUpcomingArguments, intToken, RetrieveCalendarCommand.this.mBaseService);
					upcompingWidgetCommand.execute();
					
					int intUpdateMinute = 30;
					Thread.currentThread().sleep(1000 * 60 * intUpdateMinute);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};
		
	private Thread threadReminderUpdate = new Thread() {

		public void run() {
			while(mHasThreadReminderUpdate) {
				try {
					String strStartTime = "";
					String strEndTime = "";
					GregorianCalendar gregorianCalendar = new GregorianCalendar();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					StringBuffer stringBufferDate = new StringBuffer();
					simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(0));
					strStartTime = stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset()).toString();

					int intWeek = 4;
					int intDayInWeek = 7;
					int intHourInDay = 24;
					gregorianCalendar.add(Calendar.HOUR, intWeek * intDayInWeek * intHourInDay);
					stringBufferDate.delete(0, stringBufferDate.length());
					simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(0));
					strEndTime = stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset()).toString();

					String strCalendar = "default";
					String strVisibility = "private";
					String strProjection = "FULL";
					String[] strUpcomingArguments = {strStartTime, strEndTime, strCalendar, strVisibility, strProjection};
					int intToken = 0;

					UpcomingReminderCommand upcomingReminderCommand = new UpcomingReminderCommand(strUpcomingArguments, intToken, RetrieveCalendarCommand.this.mBaseService);
					upcomingReminderCommand.execute();

					int intUpdateMinute = 5;
					Thread.currentThread().sleep(1000 * 60 * intUpdateMinute);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};

	private Thread threadAlarmUpdate = new Thread() {

		public void run() {
			while(mHasThreadAlarmUpdate) {
				try {
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
					int intToken = 0;

					UpcomingAlarmCommand upcomingAlarmCommand = new UpcomingAlarmCommand(strUpcomingArguments, intToken, RetrieveCalendarCommand.this.mBaseService);
					upcomingAlarmCommand.execute();

					int intUpdateMinute = 15;
					Thread.currentThread().sleep(1000 * 60 * intUpdateMinute);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};
	
	/**
	 * Stop update Widget and Alarm.
	 */
	public static void stopUpdateWidgetAlarm() {
		mHasThreadWidgetUpdate = false;
		mHasThreadAlarmUpdate = false;
		mHasThreadReminderUpdate = false;
		EXECUTOR_SERVICE.shutdownNow();
		EXECUTOR_SERVICE = null;
	}
	
	private GregorianCalendar getWidgetEndTime(GregorianCalendar gregorianCalendar) {
		gregorianCalendar.add(Calendar.SECOND, 0 - gregorianCalendar.get(Calendar.SECOND));
		gregorianCalendar.add(Calendar.MINUTE, 0 - gregorianCalendar.get(Calendar.MINUTE));
		gregorianCalendar.add(Calendar.HOUR_OF_DAY, 0 - gregorianCalendar.get(Calendar.HOUR_OF_DAY));
		gregorianCalendar.add(Calendar.DATE, 1);
		gregorianCalendar.add(Calendar.SECOND, -1);
		return gregorianCalendar;
	}
	
}

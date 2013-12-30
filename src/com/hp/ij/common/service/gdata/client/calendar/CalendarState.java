//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import com.hp.ij.common.service.baseservice.RetrieveCalendarCommand;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import androidx.gdata.GaiaSession;

/**
 * Session Management of Calendar and user State. 
 * 
 * @author Luke Liu
 *
 */
public class CalendarState {
	
	/**
	 * Google account
	 */
	private static String strUsername;
	
	/**
	 * GaiaSession
	 */
	private static GaiaSession gaiaSession;

	/**
	 * CalendarFeed.
	 */
	private static CalendarFeed mCalendarFeed;

	/**
	 * Default CalendarFeed.
	 */
	private static CalendarFeed mCalendarFeedOriginal;
	
	/**
	 * AllEvents as cache, use allEvent.mStrYearMonth as index.
	 */
	private static AllEvent[] mAllEvents;
	
	/**
	 * Cache number of AllEvents.
	 */
	private static int mIntCacheNumber = 12;
	
	/**
	 * Number to write to cache.
	 */
	private static long mLongUid = 0;
	
	public static void setStrUsername(String strUsername) {
		CalendarState.strUsername = strUsername;
	}

	public static String getStrUsername() {
		return strUsername;
	}

	/**
	 * After user log in succeed, cache Session, and initialize AllEvent.
	 * @param gaiaSession
	 */
	public static void setGaiaSession(GaiaSession gaiaSession) {
		CalendarState.gaiaSession = gaiaSession;
		mAllEvents = new AllEvent[mIntCacheNumber];
	}

	public static GaiaSession getGaiaSession() {
		return gaiaSession;
	}

	public synchronized static void setCalendarFeed(CalendarFeed calendarFeed) {
		CalendarState.mCalendarFeed = calendarFeed;
	}

	public synchronized static CalendarFeed getCalendarFeed() {
		return mCalendarFeed;
	}

	public static void setAllEvents(AllEvent[] allEvents) {
		CalendarState.mAllEvents = allEvents;
	}

	public static AllEvent[] getAllEvents() {
		return mAllEvents;
	}

	public static void setmIntCacheNumber(int mIntCacheNumber) {
		CalendarState.mIntCacheNumber = mIntCacheNumber;
	}

	public static int getmIntCacheNumber() {
		return mIntCacheNumber;
	}

	/**
	 * Query whether AllEvent exist or not.
	 * 
	 * @param strYearMonth
	 * @return true: exist, false not.
	 */
	public static boolean queryCache(String strYearMonth) {
		boolean boolReturn = false;
		for(AllEvent allEvent : mAllEvents) {
			if((allEvent != null) && (allEvent.getmStrYearMonth().compareTo(strYearMonth) == 0)) {
				boolReturn = true;
				break;
			}
		}
		return boolReturn;
	}
	
	/**
	 * Get certain AllEvent by YearMonth.
	 * 
	 * @param strYearMonth format "yyyyMM"
	 * @return AllEvent 
	 */
	public static AllEvent getAllEvent(String strYearMonth) {
		int i = 0;
		for(i = 0; i<mAllEvents.length; i++) {
			if(mAllEvents[i].getmStrYearMonth().compareTo(strYearMonth) == 0) break;
		}
		
		return mAllEvents[i];
	}
	
	/**
	 * set AllEvent to cache
	 * 
	 * @param allEvent to be assigned to cache.
	 */
	public static void setAllEvent(AllEvent allEvent) {
		mAllEvents[(int)(mLongUid % mIntCacheNumber)] = allEvent;
		mLongUid++;
	}

	public static void setMCalendarFeedOriginal(CalendarFeed mCalendarFeedOriginal) {
		CalendarState.mCalendarFeedOriginal = mCalendarFeedOriginal;
	}

	public static CalendarFeed getMCalendarFeedOriginal() {
		return mCalendarFeedOriginal;
	}
	
	public static void copyCalendarFeed() {
		mCalendarFeedOriginal = mCalendarFeed.clone();
	}
	
	/**
	 * Clear existing data for logout.
	 */
	public static void clearCalendarState() {
		strUsername = null;
		gaiaSession = null;
		mCalendarFeed = null;
		mCalendarFeedOriginal = null;
		mAllEvents = null;
		//RetrieveCalendarCommand.stopUpdateWidgetAlarm();
	}
}

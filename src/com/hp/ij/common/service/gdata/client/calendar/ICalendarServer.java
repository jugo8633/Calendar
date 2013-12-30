//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.IOException;

import android.content.Context;

import com.hp.ij.common.service.gdata.client.calendar.Event;

/**
 * Define an interface for CalendarServer, to retrieve Event.
 * 
 * @author Luke Liu
 */
public interface ICalendarServer {

	/**
	 * Default Google Calendar root url
	 */
	public static String DEFAULT_CALENDAR = "http://www.google.com/calendar/feeds/default/private/full";
	
	/**
	 * Retrieve EventFeed with specify date-time range
	 * 
	 * @param strUserId
	 * @param Visibility of the EventFeed
	 * @param Projection of the EventFeed
	 * @param Start Date-Time
	 * @param End Date-Time
	 * @return EventFeed
	 * @throws IOException
	 */
	EventFeed getEventFeed(String strUserId, String strVisibility, EventFeedProjection intProjection, String strDateStart, String strDateEnd, Context contextService) throws IOException;
	EventFeedFree getEventFeedFree(String strUserId, String strVisibility, EventFeedProjection eventFeedProjection, String strDateStart, String strDateEnd, Context contextService) throws IOException;
	Event getSingleEvent(String strUserId, String strVisibility, EventFeedProjection intProjection, String strEid, Context contextService) throws IOException;
	CalendarFeed getCalendarFeed(String strUserId, String strVisibility, EventFeedProjection intProjection, Context contextService) throws IOException;

	Event postEventEntry(String DEFAULT_CALENDAR, Event entry) throws IOException;
	boolean deleteEvent(String strUserId, String strVisibility, EventFeedProjection intProjection, String strEid) throws IOException;
	Event updateEventEntryFeed(String strUrl, Event entry) throws IOException;
	
	//public CalendarFeed getSelf(CalendarFeed feed) throws IOException;
	//public CalendarFeed getNext(CalendarFeed feed) throws IOException;
	
}
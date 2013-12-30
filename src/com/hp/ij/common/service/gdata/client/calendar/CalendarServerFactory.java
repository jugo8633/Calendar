//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;

import android.content.Context;


/**
 * Create an unique CalendarServer
 * 
 * @author Luke Liu
 *
 */
public class CalendarServerFactory {

	  private static final String baseURL = "http://www.google.com/calendar/feeds/";

	  private static CalendarServer mCalendarServer;
	  
	  public static CalendarServer getServer(String username, String auth, Context context) {
		  if(mCalendarServer == null) {
			  if(username == null || auth == null)	mCalendarServer = null;
			  else
				  mCalendarServer = new CalendarServer(baseURL, username, auth, context);
		  }
			  
		  return mCalendarServer;
	  }

}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.Serializable;

/**
 * Contain events corresponding to selected calendars.
 * 
 * @author Luke Liu
 *
 */
public class SingleDayEvent implements Serializable {

	/**
	 * yyyy-MM-dd as unique index.
	 */
	private String strMYearMonthDay;
	
	private EventFeed[] mEventFeeds;
	
	private boolean mBoolValid;
	
	private int mIntSC;

	public void setmEventFeeds(EventFeed[] mEventFeeds) {
		this.mEventFeeds = mEventFeeds;
	}

	public EventFeed[] getmEventFeeds() {
		return mEventFeeds;
	}

	public SingleDayEvent(EventFeed[] eventFeeds) {
		this.mEventFeeds = eventFeeds;
	}

	public SingleDayEvent(EventFeed eventFeed) {
		EventFeed[] eventFeeds = new EventFeed[1];
		eventFeeds[0] = eventFeed;
		this.mEventFeeds = eventFeeds;
	}
	
	public SingleDayEvent(boolean boolValid, int intSC) {
		this.setmBoolValid(boolValid);
		this.setmIntSC(intSC);
	}
	
	public void setStrMYearMonthDay(String strMYearMonthDay) {
		this.strMYearMonthDay = strMYearMonthDay;
	}

	public String getStrMYearMonthDay() {
		return strMYearMonthDay;
	}

	public void setmBoolValid(boolean mBoolValid) {
		this.mBoolValid = mBoolValid;
	}

	public boolean ismBoolValid() {
		return mBoolValid;
	}

	public void setmIntSC(int mIntSC) {
		this.mIntSC = mIntSC;
	}

	public int getmIntSC() {
		return mIntSC;
	}

}

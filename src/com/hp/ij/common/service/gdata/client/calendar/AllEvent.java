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
 * Contains selected calendar's events.
 * 
 * @author Luke Liu
 *
 */
public class AllEvent implements Serializable {

	/**
	 * Use [yyyy-MM] as an index.
	 */
	private String mStrYearMonth;
	
	private EventFeedFree[] mEventFeedFrees;
	
	private boolean mBoolValid;
	
	private int mIntSC;

	public void setmEventFeedFrees(EventFeedFree[] mEventFeedFrees, String strYearMonth) {
		this.mEventFeedFrees = mEventFeedFrees;
		this.setmStrYearMonth(strYearMonth);
	}
	
	public AllEvent(boolean boolValid, int intSC) {
		this.setmBoolValid(boolValid);
		this.setmIntSC(intSC);
	}

	public EventFeedFree[] getmEventFeedFrees() {
		return mEventFeedFrees;
	}

	public AllEvent(EventFeedFree[] eventFeedFrees) {
		this.mEventFeedFrees = eventFeedFrees;
		this.setmBoolValid(true);
	}
	
	/**
	 * Field mStrYearMonth = "0000-00" as null index.
	 */
	public AllEvent() {
		this.mStrYearMonth = "0000-00";
	}

	public void setmStrYearMonth(String mStrYearMonth) {
		this.mStrYearMonth = mStrYearMonth;
	}

	public String getmStrYearMonth() {
		return mStrYearMonth;
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

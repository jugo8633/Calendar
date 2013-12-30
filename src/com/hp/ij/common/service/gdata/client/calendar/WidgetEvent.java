//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Event for broadcasting in the period of time
 * 
 * @author Luke Liu
 *
 */
public class WidgetEvent implements Comparable<WidgetEvent> {

	private String mStrCalendarTitle;
	private String mStrEventTitle;
	private String mStrStartTime;
	private String mStrEndTime;
	private String mStrStartDate;
	
	public WidgetEvent(String strCalendarTitle, String strEventTitle, String strStartTime, String strEndTime, String strStartDate) {
		this.setmStrCalendarTitle(strCalendarTitle);
		this.setmStrEventTitle(strEventTitle);
		this.setmStrStartTime(strStartTime);
		this.setmStrEndTime(strEndTime);
		this.setmStrStartDate(strStartDate);
	}
	
	public int compareTo(WidgetEvent another) {
		int intReturn = 0;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			Date dateThis = simpleDateFormat.parse(mStrStartTime);
			Date dateCompare = simpleDateFormat.parse(another.getmStrStartTime());
			switch(dateThis.compareTo(dateCompare)) {
			case 0:
				intReturn = 0;
				break;
				
			case -1:
				intReturn = -1;
				break;
			
			case 1:
				intReturn = 1;
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		/*
		if(mStrStartTime.compareToIgnoreCase(another.getmStrStartTime()) == -1)	intReturn = -1;
		else if(mStrStartTime.compareToIgnoreCase(another.getmStrStartTime()) == 0)	intReturn = 0;
		else intReturn = 1;
		*/
		return intReturn;
	}

	public void setmStrCalendarTitle(String mStrCalendarTitle) {
		this.mStrCalendarTitle = mStrCalendarTitle;
	}

	public String getmStrCalendarTitle() {
		return mStrCalendarTitle;
	}

	public void setmStrEventTitle(String mStrEventTitle) {
		this.mStrEventTitle = mStrEventTitle;
	}

	public String getmStrEventTitle() {
		return mStrEventTitle;
	}

	public void setmStrStartTime(String mStrStartTime) {
		this.mStrStartTime = mStrStartTime;
	}

	public String getmStrStartTime() {
		return mStrStartTime;
	}

	public void setmStrEndTime(String mStrEndTime) {
		this.mStrEndTime = mStrEndTime;
	}

	public String getmStrEndTime() {
		return mStrEndTime;
	}

	public void setmStrStartDate(String mStrStartDate) {
		this.mStrStartDate = mStrStartDate;
	}

	public String getmStrStartDate() {
		return mStrStartDate;
	}

}

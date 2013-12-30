//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Calendar represent single calendar be owned by a user.
 * 
 * @author Luke Liu
 *
 */
public class Calendar implements Serializable {

	/**
	 * Unique id of a Calendar.
	 */
	private String mId;
	
	/** 
	 * Event title. 
	 */
	private String mTitle;
	
	/**
	 * AccessLevel
	 */
	private String mAccessLevel;
	
	public Calendar(String strId, String strTitle, String strAccessLevel) {
		this.setmId(strId);
		this.setmTitle(strTitle);
		this.setmAccessLevel(strAccessLevel);
	}

	public void setmId(String mId) {
		this.mId = exetractId(mId);
		//this.mId = mId;
	}

	public String getmId() {
		return mId;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmTitle() {
		return mTitle;
	}
	
	/**
	 * A regular expression to extract id from url
	 * 
	 * @param strUrl
	 * @return id 
	 */
	private String exetractId(String strUrl) {
		Pattern pattern = Pattern.compile("(?:full/)(.+)");
		Matcher matcher = pattern.matcher(strUrl);
		matcher.find();
		return matcher.group(1);
	}

	public void setmAccessLevel(String mAccessLevel) {
		this.mAccessLevel = mAccessLevel;
	}

	public String getmAccessLevel() {
		return mAccessLevel;
	}
	
}

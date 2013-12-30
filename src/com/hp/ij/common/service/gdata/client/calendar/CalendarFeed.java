//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.Serializable;
import java.lang.Cloneable;
import androidx.gdata.Author;
import androidx.gdata.LinkUrl;
import androidx.gdata.ThingWithLinks;

/**
 * Representation of a CalendarFeed
 * 
 * @author Luke Liu
 *
 */
public class CalendarFeed extends ThingWithLinks implements Serializable, Cloneable{

	private Author mAuthor;
	private Calendar[] mCalendars;
	private boolean mBoolValid;
	private int mIntSC;
	
	public CalendarFeed(Author author, Calendar[] calendars, LinkUrl[] links) {
		super(links);
		this.setmAuthor(author);
		this.setmCalendars(calendars);
		this.setmBoolValid(true);
		this.setmIntSC(mIntSC);
	}
	
	public CalendarFeed(boolean boolValid, int intSC) {
		super(null);
		this.setmBoolValid(boolValid);
		this.setmIntSC(intSC);
	}

	public void setmAuthor(Author mAuthor) {
		this.mAuthor = mAuthor;
	}

	public Author getmAuthor() {
		return mAuthor;
	}

	public void setmCalendars(Calendar[] mCalendars) {
		this.mCalendars = mCalendars;
	}

	public Calendar[] getmCalendars() {
		return mCalendars;
	}
	
	public CalendarFeed clone() {
		try {
			return (CalendarFeed)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	public void setmBoolValid(boolean mBoolData) {
		this.mBoolValid = mBoolData;
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

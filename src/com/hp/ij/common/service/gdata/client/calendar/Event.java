//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.Serializable;
import java.lang.Comparable;
import androidx.util.TimeZoneUtil;
import androidx.RegEx;
import androidx.gdata.Category;
import androidx.gdata.LinkUrl;
import androidx.gdata.Author;
import androidx.gdata.When;
import androidx.gdata.Where;
import androidx.gdata.Recurrence;
import androidx.gdata.Reminder;

/**
 * Event represent the event be contained in a Calendar.
 * 
 * @author Luke Liu
 *
 */
public class Event implements Serializable, Comparable<Event> {

	/**
	 * Unique id of an Event; 
	 */
	private String id;
	private String published;
	private String updated;
	private Category category;
	
	/**
	 * Event title. 
	 */
	private String title;
	
	/**
	 * Event content.
	 */
	private String content;
	private LinkUrl[] links;
	private Author author;
	private String transparency;
	private String eventStatus;
	
	/**
	 * Times for recurrence events.
	 */
	private When[] whens;
	
	/**
	 * Where the event be hold.
	 */
	private Where where;
	
	/**
	 * Start time and end time.
	 */
	private When when;
	
	/**
	 * Recurrence time.
	 */
	private Recurrence recurrence;
	 
	private Reminder[] reminders;
	
	/**
	 * Whether the event can be edited or not?
	 */
	private boolean boolIsEdited;
	
	/**
	 * Whether the event is valid or not.
	 */
	private boolean boolIsValid = true;
	
	public Event(boolean boolValid) {
		setBoolIsValid(boolValid);
	}
	
	public Event(String id, String published, String updated, Category category, String title, String content, 
			LinkUrl[] links, Author author, String transparency, String eventStatus, When[] whens, Where where) {
		this.id = RegEx.exetractId("(?:full/)(.+)", id);
		this.published = published;
		this.updated = updated;
		this.category = category;
		this.title = title;
		this.content = content;
		this.links = links;
		this.author = author;
		this.transparency = transparency;
		this.eventStatus = eventStatus;
		this.whens = whens;
		this.where = where;
	}
	
	public Event(String id, String published, String updated, Category category, String title, String content, 
			LinkUrl[] links, Author author, String transparency, String eventStatus, When when, Where where) {
		this.id = RegEx.exetractId("(?:full/)(.+)", id);
		if(this.id.indexOf("_") != -1)	this.id = this.id.substring(0, this.id.indexOf("_"));
		this.published = published;
		this.updated = updated;
		this.category = category;
		this.title = title;
		this.content = content;
		this.links = links;
		this.author = author;
		this.transparency = transparency;
		this.eventStatus = eventStatus;
		this.when = when;
		this.where = where;
	}
	
	/**
	 * Construct a new single-occurrence event with or without reminder.
	 * 
	 * @param title 
	 * @param content
	 * @param where
	 * @param when
	 */
	public Event(String title, String content, Where where, When when) {
		this.title = title;
		this.content = content;
		this.where = where;
		this.when = when;
		this.category = new Category("http://schemas.google.com/g/2005#kind", "http://schemas.google.com/g/2005#event");
	}
	
	/**
	 * Construct a new recurring event with 
	 * @param title
	 * @param content
	 * @param where
	 * @param recurrence
	 */
	public Event(String title, String content, Where where, Recurrence recurrence, Reminder[] reminders) {
		this.id = "";
		this.title = title;
		this.content = content;
		this.where = where;
		this.recurrence = recurrence;
		if(reminders[0].isHasReminder())	this.setReminders(reminders);
		this.category = new Category("http://schemas.google.com/g/2005#kind", "http://schemas.google.com/g/2005#event");
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String strId) {
		this.id = strId;
	}

	public String getPublished() {
		return published;
	}

	public String getUpdated() {
		return updated;
	}

	public Category getCategory() {
		return category;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public LinkUrl[] getLinks() {
		return links;
	}

	public Author getAuthor() {
		return author;
	}

	public String getTransparency() {
		return transparency;
	}

	public String getEventStatus() {
		return eventStatus;
	}

	public When[] getWhens() {
		return whens;
	}

	public Where getWhere() {
		return where;
	}

	public Event makeClone() {
		//get initial bit-by-bit copy, which handles all immutable fields
		return new Event(id, published, updated, category, title, content, links, author, transparency, eventStatus, whens, where);
	}

	public void setWhen(When when) {
		this.when = when;
	}

	public When getWhen() {
		return when;
	}

	public int compareTo(Event eventAnother) {
		return when.getStartTime().compareToIgnoreCase(eventAnother.when.getStartTime());
	}

	public void setRecurrence(Recurrence recurrence) {
		this.recurrence = recurrence;
	}

	public Recurrence getRecurrence() {
		return recurrence;
	}

	public void setReminders(Reminder[] reminders) {
		this.reminders = reminders;
	}

	public Reminder[] getReminders() {
		return reminders;
	}
	
	public String getTimeAMPM() {
		return TimeZoneUtil.getTimeAMPM(this.when.getStartTime(), this.when.getEndTime());
	}

	public void setBoolIsEdited(boolean boolIsEdited) {
		this.boolIsEdited = boolIsEdited;
	}

	public boolean isBoolIsEdited() {
		return boolIsEdited;
	}
	
	public void checkEditOrNot(String strAccessLevel) {
		boolean boolIsEdited = false;
		
		if(strAccessLevel.compareToIgnoreCase("owner") == 0)	boolIsEdited = true;
		else if(strAccessLevel.compareToIgnoreCase("editor") == 0)	boolIsEdited = true;
		else	boolIsEdited = false;
		
		setBoolIsEdited(boolIsEdited);
	}

	public void setBoolIsValid(boolean boolIsValid) {
		this.boolIsValid = boolIsValid;
	}

	public boolean isBoolIsValid() {
		return boolIsValid;
	}
	
}

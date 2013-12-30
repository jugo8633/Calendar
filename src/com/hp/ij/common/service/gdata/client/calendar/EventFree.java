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
import androidx.RegEx;
import androidx.gdata.Category;
import androidx.gdata.LinkUrl;
import androidx.gdata.Author;
import androidx.gdata.When;
import androidx.gdata.Where;

public class EventFree implements Serializable, Comparable<EventFree> {

	private String id;
	//private String published;
	//private String updated;
	//private Category category;
	private String title;
	//private String content;
	//private LinkUrl[] links;
	//private Author author;
	//private String transparency;
	//private String eventStatus;
	//private When[] whens;
	//private Where where;
	private When when;

	/*
	public Event(String id, String published, String updated, Category category, String title, String content, 
			LinkUrl[] links, Author author, String transparency, String eventStatus, When[] whens, Where where) {
		this.id = id;
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
	*/
	
	public EventFree(String id, String title, When when) {
		this.id = RegEx.exetractId("(?:full/)(.+)", id);
		if(this.id.indexOf("_") != -1)	this.id = this.id.substring(0, this.id.indexOf("_"));
		//this.published = published;
		//this.updated = updated;
		//this.category = category;
		this.setTitle(title);
		//this.content = content;
		//this.links = links;
		//this.author = author;
		//this.transparency = transparency;
		//this.eventStatus = eventStatus;
		this.when = when;
		//this.where = where;
	}
	
	public String getId() {
		return id;
	}

	/*
	public String getPublished() {
		return published;
	}
	*/

	/*
	public String getUpdated() {
		return updated;
	}
	*/

	/*
	public Category getCategory() {
		return category;
	}
	*/

	/*
	public String getTitle() {
		return title;
	}
	*/

	/*
	public String getContent() {
		return content;
	}
	*/

	/*
	public LinkUrl[] getLinks() {
		return links;
	}
	*/
	
	/*
	public Author getAuthor() {
		return author;
	}
	*/

	/*
	public String getTransparency() {
		return transparency;
	}
	*/

	/*
	public String getEventStatus() {
		return eventStatus;
	}
	*/

	/*
	public When[] getWhens() {
		return whens;
	}
	*/

	/*
	public Where getWhere() {
		return where;
	}
	*/

	/*
	public EventFree makeClone() {
		//get initial bit-by-bit copy, which handles all immutable fields
		//return new Event(id, published, updated, category, title, content, links, author, transparency, eventStatus, whens, where);
		return new EventFree(id, title, whens);
	}
	*/

	public void setWhen(When when) {
		this.when = when;
	}

	public When getWhen() {
		return when;
	}

	public int compareTo(EventFree eventFreeAnother) {
		return when.getStartTime().compareToIgnoreCase(eventFreeAnother.when.getStartTime());
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
}

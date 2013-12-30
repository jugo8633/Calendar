//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.Serializable;
import androidx.RegEx;
import androidx.gdata.Author;
import androidx.gdata.Category;
import androidx.gdata.Generator;
import androidx.gdata.LinkUrl;
import androidx.gdata.OpenSearch;
import androidx.gdata.ThingWithLinks;

/**
 * Representation of EventFeed
 * @author Luke Liu
 *
 */
public class EventFeedFree implements Serializable{

	private String id;
	//private String updated;
	private String title;
	//private String subtitle;
	//private Author author;
	//private Generator generator;
	private EventFree[] eventFrees;

	/*
	public EventFeedFree(String id, String updated, String title, String subtitle, Author author, Generator generator, 
			Event[] events, LinkUrl[] links) {
		super(links);
		this.id = id;
		this.updated = updated;
		this.title = title;
		this.subtitle = subtitle;
		this.author = author;
		this.generator = generator;
		this.events = events;
	}
	*/

	public EventFeedFree(String id, String title, EventFree[] eventFrees) {
		//super(links);
		this.id = RegEx.exetractId("(?:/feeds/)(.+)(?:/private/)", id);
		//this.updated = updated;
		this.title = title;
		//this.subtitle = subtitle;
		//this.author = author;
		//this.generator = generator;
		this.eventFrees = eventFrees;
	}

	public String getId() {
		return id;
	}

	/*
	public String getUpdated() {
		return updated;
	}
	*/

	public String getTitle() {
		return title;
	}

	/*
	public String getSubtitle() {
		return subtitle;
	}
	*/

	/*
	public Author getAuthor() {
		return author;
	}
	*/

	/*
	public Generator getGenerator() {
		return generator;
	}
	*/

	public EventFree[] getEvents() {
		return eventFrees;
	}

}

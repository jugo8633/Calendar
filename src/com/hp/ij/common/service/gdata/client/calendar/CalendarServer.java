//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import org.w3c.dom.Node;
import com.hp.ij.common.service.gdata.client.calendar.xml.CalendarFeedDeserializer;
import com.hp.ij.common.service.gdata.client.calendar.xml.EventDeserializer;
import com.hp.ij.common.service.gdata.client.calendar.xml.EventFeedDeserializer;
import com.hp.ij.common.service.gdata.client.calendar.xml.EventFeedFreeDeserializer;
import com.hp.ij.common.service.gdata.client.calendar.xml.EventSerializer;
import androidx.LogX;
import androidx.gdata.GDataServer;
import androidx.gdata.LinkUrl;
import androidx.gdata.LinkUrlUtil;

/**
 * Inherit from GDataServer and implements ICalendarServer, includes making requests to Calendar Server, and extracting data from which.
 * See http://code.google.com/apis/calendar/docs/2.0/reference.html.
 * More detail please refer to
 * http://code.google.com/apis/calendar/docs/2.0/developers_guide_protocol.html.
 *
 * @author Luke Liu
 */
public class CalendarServer extends GDataServer implements ICalendarServer {
	
	private static EventFeedDeserializer feedBuilder = new EventFeedDeserializer();
	private static EventFeedFreeDeserializer feedFreeBuilder = new EventFeedFreeDeserializer();
	private static CalendarFeedDeserializer calendarFeedDeserializer = new CalendarFeedDeserializer();
	private static EventSerializer eventSerializer = new EventSerializer();
	private static EventDeserializer eventBuilder = new EventDeserializer();

	protected CalendarServer(String baseUrl, String username, String auth, Context context) {
		super(baseUrl, username, auth, context);
	}

	public EventFeed getEventFeed(String strUserId, String strVisibility, EventFeedProjection eventFeedProjection, String strDateStart, String strDateEnd, Context contextService) throws IOException {
		String stringProjection;
		switch(eventFeedProjection) {
		case FULL:
			stringProjection = "full";
			break;
		case FREE_BUSY:
			stringProjection = "free-busy";
			break;
		default:
			stringProjection = "full";
		}
		
		/**
		 * Some other Calendar query parameters must be specify.
		 * ctz, singleevents, orderby, featureevents, max-results
		 */
		StringBuilder stringBuilderArguments = new StringBuilder();
		stringBuilderArguments.append("&singleevents=true&orderby=starttime&featureevents=true&max-results=9999");

		strDateStart = Uri.encode(strDateStart);
		strDateEnd = Uri.encode(strDateEnd);
		
		URL url = new URL(getBaseUrl() + strUserId + "/" + strVisibility + "/" + stringProjection + "?start-min=" + strDateStart + 
				"&start-max=" + strDateEnd + stringBuilderArguments.toString());
		
	    return toEventFeed(url, contextService);
	}

	private EventFeed toEventFeed(URL url, Context contextService) throws IOException {
	    String result = doAuthorizedGet(url, contextService);
	    Node feedNode = extractFeedNode(result);
	    return feedBuilder.build(feedNode);
	}

	private boolean deleteEventURL(URL url) throws IOException {
	    return doAuthorizedDelete(url);
	    //Node feedNode = extractFeedNode(result);
	    //return feedBuilder.build(feedNode);
	}

	private EventFeedFree toEventFeedFree(URL url, Context contextService) throws IOException {
	    String result = doAuthorizedGet(url, contextService);
	    Node feedNode = extractFeedNode(result);
	    return feedFreeBuilder.build(feedNode);
	}

	public EventFeedFree getEventFeedFree(String strUserId, String strVisibility, EventFeedProjection eventFeedProjection, String strDateStart, String strDateEnd, Context contextService) throws IOException {
		String stringProjection;
		switch(eventFeedProjection) {
		case FULL:
			stringProjection = "full";
			break;
		case FREE_BUSY:	// for specific fields of "Available" events.
			stringProjection = "full";
			break;
		case BASIC:
			stringProjection = "basic";
			break;
		default:
			stringProjection = "full";
		}

		/**
		 * Some other Calendar query parameters must be specify.
		 * ctz, singleevents, orderby, featureevents, max-results
		 */
		StringBuilder stringBuilderArguments = new StringBuilder();
		stringBuilderArguments.append("&singleevents=true&orderby=starttime&featureevents=true&max-results=9999");
		stringBuilderArguments.append("&fields=id,title,entry(id,title,gd:when(" + Uri.encode("@") + "startTime," + Uri.encode("@") +"endTime))");
		
		strDateStart = Uri.encode(strDateStart);
		strDateEnd = Uri.encode(strDateEnd);

		/**
		 * Decide visible and projection and date range.
		 */
		URL url = new URL(getBaseUrl() + strUserId + "/" + strVisibility + "/" + stringProjection + "?start-min=" + strDateStart + 
				"&start-max=" + strDateEnd + stringBuilderArguments.toString());
		
	    return toEventFeedFree(url, contextService);
	}

	/**
	 * post event to default calendar
	 */
	public Event postEventEntry(String strUrl, Event entry)
			throws IOException {
		strUrl = DEFAULT_CALENDAR;
	    StringWriter sw = new StringWriter();
	    PrintWriter out = new PrintWriter(sw);
	    eventSerializer.serialize(out, entry);
	    out.flush();
	    URL url = new URL(strUrl);
	    String atomPost = sw.toString();

	    String result = doAtomPost(url, atomPost);
	    Node feedNode = extractEntryNode(result);
	    return eventBuilder.build(feedNode);
	}

	public CalendarFeed getCalendarFeed(String strUserId, String strVisibility,
			EventFeedProjection intProjection, Context contextService) throws IOException {
		String stringProjection;
		switch(intProjection) {
		case FULL:
			stringProjection = "full";
			break;
		case FREE_BUSY:
			stringProjection = "free-busy";
			break;
		default:
			stringProjection = "full";
		}
		
		URL url = new URL(getBaseUrl() + strUserId + "/" + strVisibility + "/" + stringProjection);
		
		/*
		 * Set EventFeed return type full or free-busy
		 */
	    return toCalendarFeed(url, contextService);
	}
	
	private CalendarFeed toCalendarFeed(URL url, Context contextService) throws IOException {
	    String result = doAuthorizedGet(url, contextService);
	    Node feedNode = extractFeedNode(result);
	    return calendarFeedDeserializer.build(feedNode);
	}

	public Event getSingleEvent(String strUserId, String strVisibility,
			EventFeedProjection intProjection, String strEid, Context contextService)
			throws IOException {
		String stringProjection;
		switch(intProjection) {
		case FULL:
			stringProjection = "full";
			break;
		case FREE_BUSY:
			stringProjection = "free-busy";
			break;
		default:
			stringProjection = "full";
		}

		/**
		 * Specify which Eid.
		 */
		URL url = new URL(getBaseUrl() + strUserId + "/" + strVisibility + "/" + stringProjection + "/" + strEid);

		return toEventEntry(url, contextService);
	}
	
	private Event toEventEntry(URL url, Context contextService) throws IOException {
	    String result = doAuthorizedGet(url, contextService);
	    Node feedNode = extractEntryNode(result);
	    return eventBuilder.build(feedNode);
	}
	
	public boolean deleteEvent(String strUserId, String strVisibility,
			EventFeedProjection eventFeedProjection, String strEid)
			throws IOException {
		String stringProjection = "";
		switch(eventFeedProjection) {
		case FULL:
			stringProjection = "full";
			break;
		case FREE_BUSY:
			stringProjection = "free-busy";
			break;
		default:
			stringProjection = "full";
		}
		
		/**
		 * Decide visible and projection and date range.
		 */
		URL url = new URL(getBaseUrl() + strUserId + "/" + strVisibility + "/" + stringProjection + "/" + strEid);
		
		/*
		 * Set EventFeed return type full or free-busy
		 */
	    return deleteEventURL(url);
	}

	public Event updateEventEntryFeed(String strUrl, Event entry)
			throws IOException {
	    StringWriter sw = new StringWriter();
	    PrintWriter out = new PrintWriter(sw);
	    eventSerializer.serialize(out, entry);
	    out.flush();
	    URL url = new URL(strUrl);
	    String atomPut = sw.toString();

	    String result = doAtomPut(url, atomPut);
	    Node feedNode = extractEntryNode(result);
	    return eventBuilder.build(feedNode);
	}

}


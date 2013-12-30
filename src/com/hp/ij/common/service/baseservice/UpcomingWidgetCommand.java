//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.common.service.baseservice;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;

import androidx.LogX;

import androidx.util.TimeZoneUtil;

import com.hp.ij.calendar.CalendarWidgetBroadcast;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServer;
import com.hp.ij.common.service.gdata.client.calendar.CalendarServerFactory;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import com.hp.ij.common.service.gdata.client.calendar.EventFeed;
import com.hp.ij.common.service.gdata.client.calendar.EventFeedProjection;
import com.hp.ij.common.service.gdata.client.calendar.SingleDayEvent;
import com.hp.ij.common.service.gdata.client.calendar.WidgetEvent;

/**
 * Check for the last up-coming events for count 4
 *
 * @author Luke Liu
 *
 */
public class UpcomingWidgetCommand implements ICommand {
    private int            intToken;
    private BaseService    mBaseService;
    private CalendarServer mCalendarServer;

    /**
     * Date start and end.
     */
    private String mStrDateStart, mStrDateEnd;

    /**
     * Which calendar, [{Calendar}, "default"].
     */
    private String mStrCalendar;

    /**
     * Visibility, ["private", "public"].
     */
    private String mStrVisibility;

    /**
     * Calendar projection, ["FULL", "FREE_BUSY"].
     */
    private EventFeedProjection mProjection;

    public UpcomingWidgetCommand(String[] strArguments, int intToken, BaseService baseService) {
        this.intToken       = intToken;
        this.mBaseService   = baseService;
        this.mStrDateStart  = strArguments[0];
        this.mStrDateEnd    = strArguments[1];
        this.mStrCalendar   = strArguments[2];
        this.mStrVisibility = strArguments[3];
        this.mProjection    = EventFeedProjection.FULL;

        if ((CalendarState.getStrUsername() != null) && (CalendarState.getGaiaSession() != null)) {
            this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(),
                    CalendarState.getGaiaSession().getToken(), mBaseService);
        } else {
            this.mCalendarServer = null;
        }
    }

    public void execute() {
        SingleDayEvent singleDayEvent = null;
        CalendarFeed   calendarFeed;

        if (CalendarState.getCalendarFeed() == null) {
            return;
        } else {
            calendarFeed = CalendarState.getCalendarFeed();
        }

        EventFeed[] eventFeeds = new EventFeed[calendarFeed.getmCalendars().length];

        try {
            for (int i = 0; i < eventFeeds.length; i++) {
                if (calendarFeed.getmCalendars()[0] == null) {
                    return;
                }

                String strCalendar = calendarFeed.getmCalendars()[i].getmId();

                eventFeeds[i] = mCalendarServer.getEventFeed(strCalendar, mStrVisibility, mProjection, mStrDateStart,
                        mStrDateEnd, mBaseService);
            }

            singleDayEvent = new SingleDayEvent(eventFeeds);
        } catch (IOException e) {
            e.printStackTrace();

            return;
        }

        ArrayList<WidgetEvent> widgetEvents = new ArrayList<WidgetEvent>();

        for (int i = 0; i < singleDayEvent.getmEventFeeds().length; i++) {
            for (int j = 0; j < singleDayEvent.getmEventFeeds()[i].getEvents().length; j++) {
                Event  event            = singleDayEvent.getmEventFeeds()[i].getEvents()[j];
                String strCalendarTitle = singleDayEvent.getmEventFeeds()[i].getTitle();

                widgetEvents.add(new WidgetEvent(strCalendarTitle, event.getTitle(), event.getWhen().getStartTime(),
                                                 event.getWhen().getEndTime(),
                                                 event.getWhen().getDateStart().toString()));
            }
        }

        Collections.sort(widgetEvents);

        int                     intCountWidgetEvent = 5;
        CalendarWidgetBroadcast broadcast           = new CalendarWidgetBroadcast();

        for (int i = 0; i < widgetEvents.size(); i++) {
            if (i < intCountWidgetEvent) {
                WidgetEvent widgetEvent = widgetEvents.get(i);

                broadcast.addEvent(i, widgetEvent.getmStrCalendarTitle(), widgetEvent.getmStrEventTitle(),
                                   TimeZoneUtil.getStartAndEndTimeAMPM(widgetEvent.getmStrStartTime(),
                                       widgetEvent.getmStrEndTime()));
                LogX.i("Event(for widget):" + i + " Calendart Title: " + widgetEvent.getmStrCalendarTitle()
                       + " Event Title: " + widgetEvent.getmStrEventTitle() + " Time: "
                       + TimeZoneUtil.getStartAndEndTimeAMPM(widgetEvent.getmStrStartTime(),
                           widgetEvent.getmStrEndTime()));
            }
        }

        broadcast.broadcastMsg(mBaseService, CalendarWidgetBroadcast.CALENDAR_UPDATE);
    }
}

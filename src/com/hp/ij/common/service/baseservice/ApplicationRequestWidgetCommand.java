//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.common.service.baseservice;


import java.io.IOException;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

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
 * Retrieve allcalendar or owncalendars command
 *
 * @author Luke Liu
 *
 */
public class ApplicationRequestWidgetCommand implements ICommand {
    private int            intToken;
    private BaseService    mBaseService;
    private CalendarServer mCalendarServer;

    /**
     * Date start and end. Format "yyyy-MM-dd'T'HH:mm:ss"
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

    public ApplicationRequestWidgetCommand(String[] strArguments, int intToken, BaseService baseService) {
        this.intToken       = intToken;
        this.mBaseService   = baseService;
        this.mStrDateStart  = strArguments[0];
        this.mStrDateEnd    = strArguments[0];
        this.mStrCalendar   = "default";
        this.mStrVisibility = "private";
        this.mProjection    = EventFeedProjection.FULL;

        if ((CalendarState.getStrUsername() != null) && (CalendarState.getGaiaSession() != null)) {
            this.mCalendarServer = CalendarServerFactory.getServer(CalendarState.getStrUsername(),
                    CalendarState.getGaiaSession().getToken(), mBaseService);
        } else {
            this.mCalendarServer = null;
        }
    }

    public void execute() {
        String   strStartTime = "";
        String   strEndTime   = "";
        String[] strDateStart = getDateTimeArray(mStrDateStart);

        // String[] strDateEnd = getDateTimeArray(mStrDateEnd);
        GregorianCalendar gregorianCalendar = new GregorianCalendar(Integer.parseInt(strDateStart[0]),
                                                  Integer.parseInt(strDateStart[1]) - 1,
                                                  Integer.parseInt(strDateStart[2]), Integer.parseInt(strDateStart[3]),
                                                  Integer.parseInt(strDateStart[4]), Integer.parseInt(strDateStart[5]));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        StringBuffer     stringBufferDate = new StringBuffer();

        simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(0));
        strStartTime      = stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset()).toString();
        gregorianCalendar = getWidgetEndTime(gregorianCalendar);
        stringBufferDate.delete(0, stringBufferDate.length());
        simpleDateFormat.format(gregorianCalendar.getTime(), stringBufferDate, new FieldPosition(0));
        strEndTime = stringBufferDate.append(TimeZoneUtil.getTimeZoneOffset()).toString();

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

                eventFeeds[i] = mCalendarServer.getEventFeed(strCalendar, mStrVisibility, mProjection, strStartTime,
                        strEndTime, mBaseService);
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
                LogX.i("Event(from activity to widget):" + i + " Calendart Title: "
                       + widgetEvent.getmStrCalendarTitle() + " Event Title: " + widgetEvent.getmStrEventTitle()
                       + " Time: "
                       + TimeZoneUtil.getStartAndEndTimeAMPM(widgetEvent.getmStrStartTime(),
                           widgetEvent.getmStrEndTime()));
            }
        }

        broadcast.broadcastMsg(mBaseService, CalendarWidgetBroadcast.CALENDAR_UPDATE);
    }

    /**
     * Extract "yyyy-MM-ddTHH:mm:ss" to String array
     *
     * @param strDateTime
     * @return
     */
    private String[] getDateTimeArray(String strDateTime) {
        String   strDate          = strDateTime.substring(0, strDateTime.indexOf("T"));
        String   strTime          = strDateTime.substring(strDateTime.indexOf("T") + "T".length());
        String[] strArrayDate     = strDate.split("-");
        String[] strArrayTime     = strTime.split(":");
        String[] strArrayDateTime = new String[strArrayDate.length + strArrayTime.length];

        System.arraycopy(strArrayDate, 0, strArrayDateTime, 0, strArrayDate.length);
        System.arraycopy(strArrayTime, 0, strArrayDateTime, strArrayDate.length, strArrayTime.length);

        return strArrayDateTime;
    }

    private GregorianCalendar getWidgetEndTime(GregorianCalendar gregorianCalendar) {
        gregorianCalendar.add(Calendar.SECOND, 0 - gregorianCalendar.get(Calendar.SECOND));
        gregorianCalendar.add(Calendar.MINUTE, 0 - gregorianCalendar.get(Calendar.MINUTE));
        gregorianCalendar.add(Calendar.HOUR_OF_DAY, 0 - gregorianCalendar.get(Calendar.HOUR_OF_DAY));
        gregorianCalendar.add(Calendar.DATE, 1);
        gregorianCalendar.add(Calendar.SECOND, -1);

        return gregorianCalendar;
    }
}

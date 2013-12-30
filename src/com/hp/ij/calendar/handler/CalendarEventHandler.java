//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.handler;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.hp.ij.calendar.CalendarAPIServiceEvent;
import com.hp.ij.calendar.CalendarDayEvent;
import com.hp.ij.calendar.CalendarDayEventInfoEvent;
import com.hp.ij.calendar.CalendarEditEventEvent;
import com.hp.ij.calendar.CalendarEvent;
import com.hp.ij.calendar.CalendarLoadEvent;
import com.hp.ij.calendar.CalendarLoginEvent;
import com.hp.ij.calendar.CalendarLogoutEvent;
import com.hp.ij.calendar.CalendarMonthEvent;
import com.hp.ij.calendar.CalendarNewEventEvent;
import com.hp.ij.calendar.CalendarSettingEvent;
import com.hp.ij.calendar.CalendarSyncEvent;
import com.hp.ij.calendar.CalendarTimeDlgEvent;

import frame.event.EventMessage;

public class CalendarEventHandler {
    public Map<Integer, CalendarEvent> EventData = new HashMap<Integer, CalendarEvent>();

    public CalendarEventHandler(Activity act) {
        if (null != act) {
            initEvent(act);
        }
    }

    private void initEvent(Activity activity) {
        releaseEvent();
        EventData.put(EventMessage.RUN_LOADWND, new CalendarLoadEvent(activity));
        EventData.put(EventMessage.RUN_LOGINWND, new CalendarLoginEvent(activity));
        EventData.put(EventMessage.RUN_MONTHWND, new CalendarMonthEvent(activity));
        EventData.put(EventMessage.RUN_LOGOUTWND, new CalendarLogoutEvent(activity));
        EventData.put(EventMessage.RUN_DAYWND, new CalendarDayEvent(activity));
        EventData.put(EventMessage.RUN_SERVICE, new CalendarAPIServiceEvent(activity));
        EventData.put(EventMessage.RUN_NEWEVENTWND, new CalendarNewEventEvent(activity));
        EventData.put(EventMessage.RUN_EVENTINFOWND, new CalendarDayEventInfoEvent(activity));
        EventData.put(EventMessage.RUN_SYNCWND, new CalendarSyncEvent(activity));
        EventData.put(EventMessage.RUN_SETTINGWND, new CalendarSettingEvent(activity));
        EventData.put(EventMessage.RUN_EDITEVENTWND, new CalendarEditEventEvent(activity));
        EventData.put(EventMessage.RUN_TIMEWND, new CalendarTimeDlgEvent(activity));
    }

    public void releaseEvent() {
        if (EventData != null) {
            if (EventData.size() > 0) {
                EventData.clear();
            }
        }
    }

    public CalendarEvent getEvent(int nEventId) {
        if (EventData.containsKey(nEventId)) {
            return EventData.get(nEventId);
        }

        return null;
    }

    public boolean isValidWnd(int nEventId) {
        return EventData.containsKey(nEventId);
    }
}

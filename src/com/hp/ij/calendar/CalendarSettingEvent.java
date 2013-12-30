//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.ArrayList;

import android.app.Activity;

import com.hp.ij.calendar.data.CalendarMultiCalendarData;

import frame.event.EventMessage;

public class CalendarSettingEvent implements CalendarEvent {
    private static final String TAG         = "CalendarSettingEvent";
    private CalendarApplication application = null;
    private CalendarApp         theAct      = null;

    public CalendarSettingEvent(Activity actMain) {
        theAct      = (CalendarApp) actMain;
        application = (CalendarApplication) theAct.getApplication();
    }

    public int handleEvent(int nMsg, Object objData) {
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            break;
        case EventMessage.WND_STOP :
            CalendarMultiCalendarData calendarMultiCalendarData =
                new CalendarMultiCalendarData(application.getCalendarData());
            int nRunWnd = calendarMultiCalendarData.getPreWindowId();

            calendarMultiCalendarData = null;

            return nRunWnd;
        case EventMessage.WND_EXCP :
            break;
        case EventMessage.WND_MULTI :
            ArrayList<String> arrCalendar = new ArrayList<String>();

            arrCalendar = (ArrayList<String>) objData;

            int nCount = arrCalendar.size();

            if (0 >= nCount) {

                /**
                 * no any calendar to be selected
                 * clear all event data and event detail data
                 */
                application.getEventData().clearEvent();
                application.getEventData().clearEventDetail();

                return EventMessage.RUN_MONTHWND;
            }

            String   szTitle                = null;
            String   szMid                  = null;
            String[] strArgumentSetCalendar = new String[nCount];

            for (int i = 0; i < nCount; i++) {
                szTitle = arrCalendar.get(i);
                szMid   = application.getEventData().getCalendarMidByTitle(szTitle);

                if (null != szMid) {
                    strArgumentSetCalendar[i] = szMid;
                }
            }

            if ((null != strArgumentSetCalendar) && (0 < strArgumentSetCalendar.length)) {
                theAct.showProgressDlg(true, null, TAG + "87");
                application.CService.setCalendar(strArgumentSetCalendar);
            }

            break;
        case EventMessage.WND_FINISH :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }
}

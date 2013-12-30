//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.Calendar;

import android.app.Activity;

import com.hp.ij.calendar.data.CalendarMultiCalendarData;

import frame.event.EventMessage;

public class CalendarDayEvent implements CalendarEvent {
    private static final String TAG            = "CalendarDayEvent";
    private CalendarApplication application    = null;
    private CalendarDayWnd      calendarDayWnd = null;
    private CalendarApp         theAct         = null;

    public CalendarDayEvent(Activity actMain) {
        theAct         = (CalendarApp) actMain;
        application    = (CalendarApplication) theAct.getApplication();
        calendarDayWnd = (CalendarDayWnd) application.getWindow(EventMessage.RUN_DAYWND);
    }

    public int handleEvent(int nMsg, Object objData) {
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            break;
        case EventMessage.WND_STOP :
            break;
        case EventMessage.WND_EXCP :
            break;
        case EventMessage.WND_MONTH_VIEW :
            return EventMessage.RUN_MONTHWND;
        case EventMessage.WND_TO_DAY :
            break;
        case EventMessage.WND_NEW_EVENT :
            application.getActivity().showProgressDlg(true, null, null);

            if (!application.checkNetWorkEnable()) {
                ((CalendarDayWnd) application.getWindow(EventMessage.RUN_DAYWND)).setFootbarSelect(R.id.fbDayView);

                return 0;
            }

            Calendar dateNow;

            dateNow = Calendar.getInstance();
            dateNow.set(Calendar.YEAR, application.getSelYear());
            dateNow.set(Calendar.MONTH, application.getSelMonth() - 1);
            dateNow.set(Calendar.DAY_OF_MONTH, application.getSelDay());
            application.getEventData().setDetailDateStart(-1, dateNow);
            application.getEventData().setDetailDateEnd(-1, dateNow);
            application.setRecurrenceSelected(0);
            application.setReminderSelected(0);
            application.setAllDayCheck(false);
            ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).setWindowIdFrom(
                EventMessage.RUN_DAYWND);
            ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).initEditText(null, null, null);
            ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).setDateDataNow();
            ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).setTimeDataNow();

            return EventMessage.RUN_NEWEVENTWND;
        case EventMessage.WND_PRINT :
            theAct.showProgressDlg(true, null, TAG + "75");

            return EventMessage.RUN_PRINTPREVIEW_D;
        case EventMessage.WND_EVENTSEL :
            int    nIndex    = application.getEventData().getCurrDayEventSelected();
            String szEventId = application.getEventData().getEventId(nIndex);

            application.CService.getSingleEvent(szEventId);
            theAct.showProgressDlg(true, null, TAG + "83");

            break;
        case EventMessage.WND_MULTI :
            ((CalendarSettingWnd) application.getWindow(EventMessage.RUN_SETTINGWND)).syncMultiCalendarData();

            CalendarMultiCalendarData calendarMultiCalendarData =
                new CalendarMultiCalendarData(application.getCalendarData());

            calendarMultiCalendarData.setPreWindowId(EventMessage.RUN_DAYWND);
            calendarMultiCalendarData = null;

            return EventMessage.RUN_SETTINGWND;
        case EventMessage.WND_SYNC :

            /**
             * check calendar
             * if no select any calendar don't run stnc
             */
            int nCount = application.getEventData().getCalendarEnableCount();

            if (0 < nCount) {
                application.initEventData();
                calendarDayWnd.reflesgMonthView();
                application.setSyncWndType(0);    // sync run window
                theAct.showWnd(EventMessage.RUN_MONTHWND, true);
                application.CService.getFreeEvent(application.getYearOfToday(), application.getMonthOfToday());

                return EventMessage.RUN_SYNCWND;
            }

            break;
        case EventMessage.RUN_PROGWND :
            theAct.showProgressDlg(true, null, TAG + "112");

            break;
        case EventMessage.WND_FINISH :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }
}

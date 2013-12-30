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

public class CalendarMonthEvent implements CalendarEvent {
    private static final String TAG              = "CalendarMonthEvent";
    private CalendarApplication application      = null;
    private CalendarMonthWnd    calendarMonthWnd = null;
    private CalendarApp         theAct           = null;

    public CalendarMonthEvent(Activity actMain) {
        theAct           = (CalendarApp) actMain;
        application      = (CalendarApplication) theAct.getApplication();
        calendarMonthWnd = (CalendarMonthWnd) application.getWindow(EventMessage.RUN_MONTHWND);
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
            String szResult = (String) objData;

            if (null != szResult) {
                if (szResult.equals("SUCCESS")) {}
            }

            break;
        case EventMessage.WND_DAY_VIEW :
        	application.getActivity().showProgressDlg(true, null, TAG + "51");
            if (!application.checkNetWorkEnable()) {
                ((CalendarMonthWnd) application.getWindow(EventMessage.RUN_MONTHWND)).setFootbarSelect(
                    R.id.fbMonthView);

                return 0;
            }

            if (!isHaveEnableCalendar()) {
                return EventMessage.RUN_DAYWND;
            }

            String szCurrDate = String.format("%04d-%02d-%02d", application.getSelYear(), application.getSelMonth(),
                                              application.getSelDay());

            if (application.isEventDate(application.getSelYear(), application.getSelMonth(), application.getSelDay())) {
                application.CService.getEvent(szCurrDate);
            }else{
            	application.getActivity().showProgressDlg(false, null, TAG + "69");
            	application.getEventData().clearEventDetail();
            	return EventMessage.RUN_DAYWND;
            }
        case EventMessage.WND_TO_DAY :
            break;
        case EventMessage.WND_NEW_EVENT :
            application.getActivity().showProgressDlg(true, null, null);

            if (!application.checkNetWorkEnable()) {
                ((CalendarMonthWnd) application.getWindow(EventMessage.RUN_MONTHWND)).setFootbarSelect(
                    R.id.fbMonthView);

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
                EventMessage.RUN_MONTHWND);
            ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).initEditText(null, null, null);
            ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).setDateDataNow();
            ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).setTimeDataNow();

            return EventMessage.RUN_NEWEVENTWND;
        case EventMessage.WND_PRINT :
            theAct.showProgressDlg(true, null, TAG + "92");

            return EventMessage.RUN_PRINTPREVIEW_M;
        case EventMessage.WND_ITEM_CLICK :
            break;
        case EventMessage.WND_MULTI :

            /**
             * sync CalendarSettingWnd multi calendar data
             */
            ((CalendarSettingWnd) application.getWindow(EventMessage.RUN_SETTINGWND)).syncMultiCalendarData();

            CalendarMultiCalendarData calendarMultiCalendarData =
                new CalendarMultiCalendarData(application.getCalendarData());

            calendarMultiCalendarData.setPreWindowId(EventMessage.RUN_MONTHWND);
            calendarMultiCalendarData = null;

            return EventMessage.RUN_SETTINGWND;
        case EventMessage.WND_SYNC :
            application.initEventData();
            calendarMonthWnd.reflesgMonthView();
            application.setSyncWndType(0);    // sync run window

            if (isHaveEnableCalendar()) {
                application.CService.getFreeEvent(application.getYearOfToday(), application.getMonthOfToday());

                return EventMessage.RUN_SYNCWND;
            }

            break;
        case EventMessage.RUN_PROGWND :
            ((CalendarApp) theAct).showProgressDlg(true, null, TAG + "120");

            break;
        case EventMessage.WND_FINISH :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }

    private boolean isHaveEnableCalendar() {

        /**
         * check calendar
         * if no select any calendar don't run stnc
         */
        int nCount = application.getEventData().getCalendarEnableCount();

        if (0 >= nCount) {
            return false;
        }

        return true;
    }
}

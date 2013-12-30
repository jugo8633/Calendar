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

import frame.event.EventMessage;

public class CalendarDayEventInfoEvent implements CalendarEvent {
    private static final String TAG         = "CalendarDayEventInfoEvent";
    private CalendarApplication application = null;
    String                      szEventId   = null;
    private CalendarApp         theAct      = null;

    public CalendarDayEventInfoEvent(Activity actMain) {
        theAct      = (CalendarApp) actMain;
        application = (CalendarApplication) actMain.getApplication();
    }

    public int handleEvent(int nMsg, Object objData) {
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            break;
        case EventMessage.WND_BACK :
            return EventMessage.RUN_DAYWND;
        case EventMessage.WND_EXCP :
            break;
        case EventMessage.WND_REMINDER :
            break;
        case EventMessage.WND_EDIT :
            application.getActivity().showProgressDlg(true, null, null);

            if (!application.checkNetWorkEnable()) {
                ((CalendarDayEventInfoWnd) application.getWindow(EventMessage.RUN_EVENTINFOWND)).setFootbarSelect(-1);

                return 0;
            }

            szEventId = (String) objData;

            if (null == szEventId) {
                return 0;
            }

            application.setEventId(szEventId);
            initEditWnd();

            return EventMessage.RUN_EDITEVENTWND;
        case EventMessage.WND_DELETE :
            if (!application.checkNetWorkEnable()) {
                ((CalendarDayEventInfoWnd) application.getWindow(EventMessage.RUN_EVENTINFOWND)).setFootbarSelect(-1);

                return 0;
            }

            szEventId = (String) objData;

            if (null == szEventId) {
                return 0;
            }

            application.setEventId(szEventId);

            String szCaption = theAct.getString(R.string.would_you_like_to_delete_this_event);

            theAct.showCheckDlg(true, szCaption, EventMessage.RUN_EVENTINFOWND);

            break;
        case EventMessage.WND_CHK_YES :    // delete event check dialog send yes
            szEventId = application.getEventId();

            if (null == szEventId) {
                return 0;
            }

            theAct.showCheckDlg(false, null, -1);
            theAct.showProgressDlg(true, null, TAG + "84");
            application.CService.deleteEvent(szEventId);
            application.setEventId(null);

            break;
        case EventMessage.WND_PRINT :
            theAct.showProgressDlg(true, null, TAG + "90");

            return EventMessage.RUN_PRINTPREVIEW_E;
        case EventMessage.WND_FINISH :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }

    private void initEditWnd() {
        application.setRecurrenceSelected(0);
        application.setReminderSelected(0);
        application.setAllDayCheck(false);
        ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND)).setWindowIdFrom(
            EventMessage.RUN_DAYWND);

        int    nEventIndex   = application.getEventData().getCurrDayEventSelected();
        String szEventTitle  = application.getEventData().getDetailEvent(nEventIndex);
        String szWhere       = application.getEventData().getWhere(nEventIndex);
        String szDescription = application.getEventData().getDetailMessage(nEventIndex);

        ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND)).clearEditSelected();
        ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND)).initEditText(szEventTitle,
                szWhere, szDescription);

        int      nIndex    = application.getEventData().getCurrDayEventSelected();
        Calendar startDate = application.getEventData().getDetailDateStart(nIndex);
        Calendar endDate   = application.getEventData().getDetailDateEnd(nIndex);

        ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND)).setDateDataFrom(
            startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH) + 1, startDate.get(Calendar.DAY_OF_MONTH));
        ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND)).setDateDataTo(
            endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH) + 1, endDate.get(Calendar.DAY_OF_MONTH));
        ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND)).setTimeDataFrom(
            startDate.get(Calendar.HOUR), startDate.get(Calendar.MINUTE), startDate.get(Calendar.AM_PM));
        ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND)).setTimeDataTo(
            endDate.get(Calendar.HOUR), endDate.get(Calendar.MINUTE), endDate.get(Calendar.AM_PM));
    }
}

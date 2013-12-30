//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Activity;

import com.hp.ij.calendar.data.CalendarNewEventData;

import frame.event.EventMessage;

public class CalendarEditEventEvent implements CalendarEvent {
    private static final String TAG          = "CalendarEditEventEvent";
    private CalendarApplication application  = null;
    private CalendarNewEventWnd editEventWnd = null;
    private CalendarApp         theAct       = null;

    public CalendarEditEventEvent(Activity actMain) {
        theAct       = (CalendarApp) actMain;
        application  = (CalendarApplication) actMain.getApplication();
        editEventWnd = ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND));
    }

    public int handleEvent(int nMsg, Object objData) {
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            application.getActivity().showProgressDlg(false, null, null);

            break;
        case EventMessage.WND_STOP :
            return EventMessage.RUN_DAYWND;
        case EventMessage.WND_EXCP :
            break;
        case EventMessage.WND_EDIT_EVENT :
            theAct.showProgressDlg(true, null, TAG + "45");

            String[] arrData     = new String[11];
            boolean  bReCurrence = editEventWnd.IsReCurrence();
            String   szEventId   = application.getEventId();

            if (!bReCurrence) {
                arrData = editEventWnd.getNewEventData(false);

                if (null == arrData) {
                    theAct.showProgressDlg(false, null, TAG + "55");

                    return 0;
                }

                application.CService.updateEvent(szEventId, arrData[0], arrData[1], arrData[2], arrData[3], arrData[4],
                                                 arrData[5], arrData[6], arrData[7]);
            } else {
                arrData = editEventWnd.getNewEventData(true);

                if (null == arrData) {
                    theAct.showProgressDlg(false, null, TAG + "66");

                    return 0;
                }

                application.CService.updateRecurrenceEvent(szEventId, arrData[0], arrData[1], arrData[2], arrData[3],
                        arrData[4], arrData[5], arrData[6], arrData[7], arrData[8], arrData[9], arrData[10]);
            }

            break;
        case EventMessage.WND_SHOW_DATE :
            return EventMessage.RUN_DATEWND;
        case EventMessage.RUN_TIMEWND :
            return EventMessage.RUN_TIMEWND;
        case EventMessage.WND_CLOSE_DATE :
            CalendarNewEventData calendarNewEventData = new CalendarNewEventData(application.getCalendarData());

            calendarNewEventData.setDateDialogShowStatus(0);
            calendarNewEventData = null;
            ((CalendarApp) theAct).removeDialog(EventMessage.RUN_DATEWND);

            break;
        case EventMessage.WND_DATE_SELECT :
            String[] arrDate = new String[3];

            arrDate = (String[]) objData;

            int nYear  = Integer.valueOf(arrDate[0]);
            int nMonth = Integer.valueOf(arrDate[1]);
            int nDay   = Integer.valueOf(arrDate[2]);
            int nResId = application.getDateShowFromResId();

            if (R.id.tvNewEventFromDate == nResId) {
                editEventWnd.setDateDataFrom(nYear, nMonth, nDay);
            } else if (R.id.tvNewEventToDate == nResId) {
                editEventWnd.setDateDataTo(nYear, nMonth, nDay);
            }

            editEventWnd.invalidView(editEventWnd.UPDATE_DATE);

            CalendarNewEventData calendarNewEventData2 = new CalendarNewEventData(application.getCalendarData());

            calendarNewEventData2.setDateDialogShowStatus(0);
            calendarNewEventData2 = null;
            ((CalendarApp) theAct).removeDialog(EventMessage.RUN_DATEWND);

            break;
        case EventMessage.WND_FINISH :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }
}

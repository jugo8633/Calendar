//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Activity;

import frame.event.EventMessage;

public class CalendarAPIServiceEvent implements CalendarEvent {
    private static final String TAG              = "CalendarAPIServiceEvent";
    private CalendarApplication application      = null;
    private CalendarMonthWnd    calendarMonthWnd = null;
    private CalendarApp         theAct           = null;

    public CalendarAPIServiceEvent(Activity actMain) {
        theAct           = (CalendarApp) actMain;
        application      = (CalendarApplication) actMain.getApplication();
        calendarMonthWnd = (CalendarMonthWnd) application.getWindow(EventMessage.RUN_MONTHWND);
    }

    public int handleEvent(int nMsg, Object objData) {
        String szResult  = null;
        String szAlarm   = null;
        String szCaption = null;
        int    nRunWnd   = -1;

        szResult = (String) objData;

        switch (nMsg) {
        case EventMessage.SERVICE_LOGIN :
            if (checkServiceResultEnable(szResult)) {
                if (null != application.CService) {
                    application.CService.getAllCalendar();
                }

                application.initSelDay();

                return EventMessage.RUN_MONTHWND;
            }

            break;
        case EventMessage.SERVICE_RETRIEVE_CALENDAR :
            String szTotalCalendar = null;

            szTotalCalendar = (String) objData;

            int nzTotalCalendar = 0;

            if (checkServiceResultEnable(szTotalCalendar)) {
                try {
                    nzTotalCalendar = Integer.parseInt(szTotalCalendar);;
                } catch (NumberFormatException e) {
                    nzTotalCalendar = -1;
                }

                if (0 < nzTotalCalendar) {    // success!!
                    theAct.showProgressDlg(true, null, TAG + "67");
                    application.initEventData();

                    int nYear  = theAct.getCurrentYear();
                    int nMonth = theAct.getCurrentMonth();

                    application.CService.getFreeEvent(nYear, nMonth);

                    /**
                     * sync CalendarSettingWnd multi calendar data
                     */
                    ((CalendarSettingWnd) application.getWindow(EventMessage.RUN_SETTINGWND)).syncMultiCalendarData();
                } else {                       // default error
                    theAct.showProgressDlg(false, null, TAG + "80");
                    szAlarm = theAct.getString(R.string.Unable_2_connect_2_Internet);
                    theAct.showAlarmDlg(true, null, szAlarm);
                }
            }

            break;
        case EventMessage.SERVICE_RETRIEVE_EVENT_FREE :
            String szTotalEventFree = null;
            int    nzTotalEventFree = 0;

            szTotalEventFree = (String) objData;

            if (checkServiceResultEnable(szTotalEventFree)) {
                try {
                    nzTotalEventFree = Integer.parseInt(szTotalEventFree);
                } catch (NumberFormatException e) {
                    nzTotalEventFree = -1;
                }

                if (0 < nzTotalEventFree) {    // success!!
                    if (!application.getIsReceiveEventFree()) {
                        application.setReceiveEventFree(true);
                    }

                    theAct.updateMonthEvent();

                    int nSyncWndType = application.getSyncWndType();

                    if (0 == nSyncWndType) {
                        theAct.removeDialog(EventMessage.RUN_SYNCWND);
                        application.setSyncWndType(1);
                        theAct.showProgressDlg(false, null, TAG + "103");

                        return EventMessage.RUN_SYNCWND;
                    }
                } else if (0 > nzTotalEventFree) {
                    theAct.showProgressDlg(false, null, TAG + "118");
                    szAlarm = theAct.getString(R.string.Unable_2_connect_2_Internet);
                    theAct.showAlarmDlg(true, null, szAlarm);
                    theAct.removeDialog(EventMessage.RUN_SYNCWND);
                    application.setSyncWndType(1);
                }

                theAct.showProgressDlg(false, null, TAG + "103");
            }

            break;
        case EventMessage.SERVICE_RETRIEVE_EVENT :
            String szTotalEvent = null;

            szTotalEvent = (String) objData;

            if (checkServiceResultEnable(szTotalEvent)) {
                int nzTotalEvent = 0;

                try {
                    nzTotalEvent = Integer.parseInt(szTotalEvent);
                } catch (NumberFormatException e) {
                    nzTotalEvent = -1;
                }

                theAct.showProgressDlg(false, null, TAG + "145");

                if (0 <= nzTotalEvent) {
                    return EventMessage.RUN_DAYWND;
                }
            }

            break;
        case EventMessage.SERVICE_RETRIEVE_SINGLE_EVENT :
            theAct.showProgressDlg(false, null, TAG + "151");

            return EventMessage.RUN_EVENTINFOWND;
        case EventMessage.WND_NEW_EVENT :      // add new event finish
            nRunWnd = ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).getWindowIdFrom();

            if (szResult.equals(EventMessage.FALSE)) {
                syncDayEvent(false);
                theAct.showProgressDlg(false, null, TAG + "156");
                szCaption = theAct.getString(R.string.Unable_2_connect_2_Internet);
                theAct.showAlarmDlg(true, null, szCaption);
            } else {
                int nYear  = application.getSelYear();
                int nMonth = application.getSelMonth();
                int nDay   = application.getSelDay();

                if (!application.getEventData().isEventDate(nYear, nMonth, nDay)) {
                    application.setEventData(nYear, nMonth, nDay, 0);
                }

                syncEventData();

                if (EventMessage.RUN_DAYWND == nRunWnd) {
                    syncDayEvent(true);
                }
            }

            return nRunWnd;
        case EventMessage.SERVICE_DELETE_EVENT :
            if (szResult.equals(EventMessage.FALSE)) {
                theAct.showProgressDlg(false, null, TAG + "180");
                szCaption = theAct.getString(R.string.Unable_2_connect_2_Internet);
                theAct.showAlarmDlg(true, null, szCaption);
            } else {
                int nIndex = application.getEventData().getCurrDayEventSelected();

                application.getEventData().deleteEventDetail(nIndex);

                int nCount = application.getEventData().getEventDetailCount();

                application.getEventData().setCurrDayEventSelected(-1);
                application.getEventData().setCurrDayEventTitle(null);

                if (0 >= nCount) {
                    int nYear  = application.getSelYear();
                    int nMonth = application.getSelMonth();
                    int nDay   = application.getSelDay();

                    application.getEventData().deleteEventData(nYear, nMonth, nDay);
                }

                syncEventData();

                return EventMessage.RUN_DAYWND;
            }

            break;
        case EventMessage.SERVICE_UPDATE_EVENT :
            nRunWnd = ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).getWindowIdFrom();

            if (szResult.equals(EventMessage.FALSE)) {
                syncDayEvent(false);
                theAct.showProgressDlg(false, null, TAG + "208");
                szCaption = theAct.getString(R.string.Unable_2_connect_2_Internet);
                theAct.showAlarmDlg(true, null, szCaption);
            } else {
                if (EventMessage.RUN_DAYWND == nRunWnd) {
                	syncEventData();
                    syncDayEvent(true);
                }
            }

            return nRunWnd;
        case EventMessage.SERVICE_SET_CALENDAR :
            if (szResult.equals(EventMessage.FALSE)) {
                theAct.showProgressDlg(false, null, TAG + "224");
                szCaption = theAct.getString(R.string.Unable_2_connect_2_Internet);
                theAct.showAlarmDlg(true, null, szCaption);
            } else {
                int nCalendarEnableCount = application.getEventData().getCalendarEnableCount();

                if (0 < nCalendarEnableCount) {
                    syncEventData();
                } else {
                    application.initEventData();
                    calendarMonthWnd.reflesgMonthView();
                }

                return EventMessage.RUN_MONTHWND;
            }

            break;
        case EventMessage.SHOW_ALARM_DLG :
            theAct.showAlarmDlg(true, null, szResult);

            break;
        case EventMessage.SERVICE_LOGOUT :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }

    private void syncEventData() {
        theAct.showProgressDlg(true, null, TAG + "252");
        application.initEventData();
        calendarMonthWnd.reflesgMonthView();
        application.setSyncWndType(1);    // sync no run window
        application.CService.getFreeEvent(application.getYearOfToday(), application.getMonthOfToday());
    }

    private boolean checkServiceResultEnable(String szResult) {
        String szAlarm = null;

        if (szResult.equals(theAct.getString(R.string.Unable_2_connect_2_Internet))) {
            theAct.showProgressDlg(false, null, TAG + "263");
            szAlarm = theAct.getString(R.string.Unable_2_connect_2_Internet);
            theAct.showAlarmDlg(true, null, szAlarm);
        } else if (szResult.equals(theAct.getString(R.string.No_Google_Calendar_Content_Found))) {
            theAct.showProgressDlg(false, null, TAG + "267");
            szAlarm = theAct.getString(R.string.No_Google_Calendar_Content_Found);
            theAct.showAlarmDlg(true, null, szAlarm);
        } else if (szResult.equals(theAct.getString(R.string.Unable_2_connect_2_Google_Calendar))) {
            theAct.showProgressDlg(false, null, TAG + "271");
            szAlarm = theAct.getString(R.string.Unable_2_connect_2_Google_Calendar);
            theAct.showAlarmDlg(true, null, szAlarm);
        } else if (szResult.equals(theAct.getString(R.string.Unable_2_recognize_username_password))) {
            theAct.showProgressDlg(false, null, TAG + "275");
            szAlarm = theAct.getString(R.string.Unable_2_recognize_username_password);
            theAct.showAlarmDlg(true, null, szAlarm);
        } else {
            return true;
        }

        return false;
    }

    private void syncDayEvent(boolean bClearData) {
        if (bClearData) {
            theAct.showProgressDlg(true, null, TAG + "286");
            application.clearEventDetailData();
        }

        String szCurrDate = String.format("%04d-%02d-%02d", application.getSelYear(), application.getSelMonth(),
                                          application.getSelDay());

        application.setCurrDate();
        application.CService.getEvent(szCurrDate);
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.handler;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.hp.ij.calendar.CalendarApp;
import com.hp.ij.calendar.CalendarApplication;
import com.hp.ij.calendar.CalendarDayEventInfoWnd;
import com.hp.ij.calendar.CalendarWidgetBroadcast;
import com.hp.ij.calendar.R;

import frame.event.EventMessage;

public class CalendarWndEventHandler {
    private static final String TAG         = "CalendarWndEventHandler";
    private CalendarApp         calendarApp = null;
    private CalendarApplication application = null;
    private Handler             wndHandler  = null;

    public CalendarWndEventHandler(CalendarApp app) {
        calendarApp = app;
        application = (CalendarApplication) app.getApplication();
        initWndHandler(application.getThreadHandlerLooper(Process.THREAD_PRIORITY_AUDIO));
    }

    private void initWndHandler(Looper looper) {
        wndHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                int nRes = 0;

                switch (msg.what) {
                case EventMessage.WND_MSG :
                    if (EventMessage.SERVICE_LOGOUT == msg.arg2) {
                        if (EventMessage.WND_CHK_YES == msg.arg1) {
                            application.setRunWnd(EventMessage.RUN_LOADWND);
                            application.boardcastToWidget(calendarApp, CalendarWidgetBroadcast.WIDGET_DEFAULT);
                            application.notifyClockLogout(application.getActivity().getApplicationContext());
                            application.CService.logout();

                            return;
                        }
                    }

                    if (application.isValidEvent(msg.arg2)) {
                        nRes = application.getEvent(msg.arg2).handleEvent(msg.arg1, msg.obj);
                    }

                    break;
                case EventMessage.SERVICE_MSG :
                    if (application.isValidEvent(msg.arg2)) {
                        nRes = application.getEvent(msg.arg2).handleEvent(msg.arg1, msg.obj);

                        if (EventMessage.WND_FINISH == nRes) {
                            nRes = 0;
                            calendarApp.finish();
                        }
                    }

                    break;
                case EventMessage.SHOW_ALARM_DLG :
                    switch (msg.arg1) {
                    case EventMessage.WND_STOP :
                        calendarApp.showAlarmDlg(false, null, null);
                        application.setSyncWndType(-1);
                        calendarApp.removeDialog(EventMessage.RUN_SYNCWND);

                        break;
                    }

                    break;
                case EventMessage.SHOW_PROGRESS_DLG :
                    switch (msg.arg1) {
                    case EventMessage.WND_STOP :
                        calendarApp.showProgressDlg(false, null, TAG + "76");

                        break;
                    case EventMessage.WND_SHOW :
                        calendarApp.showProgressDlg(true, null, TAG + "80");

                        break;
                    }

                    break;
                case EventMessage.SHOW_CHECK_DLG :
                    switch (msg.arg1) {
                    case EventMessage.WND_STOP :
                        calendarApp.showCheckDlg(false, null, -1);

                        int nRunWnd = application.getRunWnd();

                        if (EventMessage.RUN_EVENTINFOWND == nRunWnd) {
                            ((CalendarDayEventInfoWnd) application.getWindow(
                                EventMessage.RUN_EVENTINFOWND)).initButton();
                        }

                        break;
                    }

                    break;
                case EventMessage.SHOW_SYNC_ALARM_DLG :
                    switch (msg.arg1) {
                    case EventMessage.WND_CLOSE :
                        calendarApp.showAlarmDlg(false, null, null);

                        break;
                    case EventMessage.WND_SYNC :
                        calendarApp.showAlarmDlg(false, null, null);
                        application.setSyncWndType(0);    // sync run window
                        calendarApp.showWnd(EventMessage.RUN_SYNCWND, false);
                        application.CService.getFreeEvent(application.getYearOfToday(), application.getMonthOfToday());

                        break;
                    }

                    break;
                case EventMessage.RUN_PRINTPREVIEW_M :
                case EventMessage.RUN_PRINTPREVIEW_D :
                case EventMessage.RUN_PRINTPREVIEW_E :
                    calendarApp.LaunchPrintPreview(msg.what);

                    break;
                }

                if ((EventMessage.RUN_WND < nRes) && (nRes < EventMessage.RUN_MAX)) {
                    calendarApp.showWnd(nRes, true);
                } else if (EventMessage.WND_FINISH == nRes) {
                    String szCaption = calendarApp.getString(R.string.would_you_like_to_log_out);

                    calendarApp.showCheckDlg(true, szCaption, EventMessage.SERVICE_LOGOUT);
                }
            }
        };
    }

    public Handler getHandler() {
        return wndHandler;
    }
}

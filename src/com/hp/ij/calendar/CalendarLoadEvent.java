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

public class CalendarLoadEvent implements CalendarEvent {
    private static final String TAG         = "CalendarLoadEvent";
    private CalendarApplication application = null;
    private Activity            theAct      = null;

    public CalendarLoadEvent(Activity actMain) {
        theAct      = actMain;
        application = (CalendarApplication) actMain.getApplication();
    }

    public int handleEvent(int nMsg, Object objData) {
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            if (null != application.getWindow(EventMessage.RUN_LOADWND)) {
                int nPosi = ((CalendarLoadWnd) application.getWindow(EventMessage.RUN_LOADWND)).getProgressPosition();

                if ((0 < nPosi) && (nPosi < 100)) {
                    ((CalendarLoadWnd) application.getWindow(EventMessage.RUN_LOADWND)).runLoading();

                    return 0;
                }

                if (((CalendarApp) theAct).m_bServiceReady) {
                    ((CalendarLoadWnd) application.getWindow(EventMessage.RUN_LOADWND)).runLoading();
                } else {
                    new Thread() {
                        public void run() {
                            try {
                                int nCount = 0;

                                while (true) {
                                    if (((CalendarApp) theAct).m_bServiceReady || (nCount > 5)) {
                                        break;
                                    }

                                    sleep(500);
                                    nCount++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (((CalendarApp) theAct).m_bServiceReady) {
                                    ((CalendarLoadWnd) application.getWindow(EventMessage.RUN_LOADWND)).runLoading();
                                } else {}
                            }
                        }
                    }.start();
                }
            }

            break;
        case EventMessage.WND_STOP :
            ((CalendarLoginWnd) application.getWindow(EventMessage.RUN_LOGINWND)).initCalendarLoginWnd();

            return EventMessage.RUN_LOGINWND;
        case EventMessage.WND_EXCP :
            break;
        }

        return 0;
    }
}

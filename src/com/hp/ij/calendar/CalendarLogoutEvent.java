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

public class CalendarLogoutEvent implements CalendarEvent {
    private static final String TAG    = "CalendarLogoutEvent";
    private Activity            theAct = null;

    public CalendarLogoutEvent(Activity actMain) {
        theAct = actMain;
    }

    public int handleEvent(int nMsg, Object objData) {
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            break;
        case EventMessage.WND_STOP :
            break;
        case EventMessage.WND_EXCP :
            break;
        case EventMessage.WND_LOGOUT_NO :
            return EventMessage.RUN_MONTHWND;
        case EventMessage.WND_LOGOUT_YES :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }
}

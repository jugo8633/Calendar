//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import com.hp.ij.calendar.CalendarWnd;
import com.hp.ij.calendar.R;

import frame.event.EventMessage;

public class CalendarHeaderControl {
    private CalendarWnd mCalendarWnd = null;

    public CalendarHeaderControl(CalendarWnd wnd) {
        mCalendarWnd = wnd;
    }

    public boolean buttonHeaderClickHandle(int nResId) {
        switch (nResId) {
        case R.id.tvCalendarHeaderLogout :
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_FINISH, null);

            return true;
        case R.id.tvCalendarHeaderBack :
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_BACK, null);

            return true;
        default :
            return false;
        }
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.handler;


import java.util.Calendar;

import android.os.Handler;

import com.hp.ij.calendar.CalendarApplication;
import com.hp.ij.calendar.CalendarDayWnd;
import com.hp.ij.calendar.CalendarMonthWnd;

import frame.event.EventMessage;

public class CalendarStateMonitor {
    private static final String TAG            = "CalendarStateMonitor";
    private Handler             mHandler       = null;
    private final long          mlcheckNextDay = 60000;
    private CalendarApplication application    = null;

    public CalendarStateMonitor(CalendarApplication app) {
        application = app;
    }

    public void initHandler(Handler handler) {
        mHandler = handler;
    }

    public void startNextDayCheck() {
        if (null != mHandler) {
            mHandler.postDelayed(rCheckNextDay, mlcheckNextDay);
        }
    }

    Runnable rCheckNextDay = new Runnable() {
        public void run() {

            // TODO Auto-generated method stub
            nextDayCheck();
            startNextDayCheck();
        }
    };

    private void nextDayCheck() {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(Calendar.YEAR) == application.getYearOfToday()) {
            if ((calendar.get(Calendar.MONTH) + 1) == application.getMonthOfToday()) {
                if (calendar.get(Calendar.DAY_OF_MONTH) == application.getDayOfToday()) {
                    return;
                }
            }
        }

        application.getToDay();

        int nRunWnd = application.getRunWnd();

        if (EventMessage.RUN_MONTHWND == nRunWnd) {
            ((CalendarMonthWnd) application.getWindow(EventMessage.RUN_MONTHWND)).reflesgMonthView();
        }

        if (EventMessage.RUN_DAYWND == nRunWnd) {
            ((CalendarDayWnd) application.getWindow(EventMessage.RUN_DAYWND)).reflesgMonthView();
        }

        application.CService.updateWidget();
    }

    public void closeHandler() {
        mHandler = null;
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import android.os.Handler;

import com.hp.ij.calendar.CalendarDateWnd;
import com.hp.ij.calendar.CalendarDayEventInfoWnd;
import com.hp.ij.calendar.CalendarDayWnd;
import com.hp.ij.calendar.CalendarLoadWnd;
import com.hp.ij.calendar.CalendarLoginWnd;
import com.hp.ij.calendar.CalendarLogoutWnd;
import com.hp.ij.calendar.CalendarMonthWnd;
import com.hp.ij.calendar.CalendarNewEventWnd;
import com.hp.ij.calendar.CalendarSettingWnd;
import com.hp.ij.calendar.CalendarSyncDlg;
import com.hp.ij.calendar.CalendarTimeWnd;
import com.hp.ij.calendar.CalendarWnd;

import frame.event.EventMessage;

public class CalendarWindowControl {
    public Map<Integer, CalendarWnd> WndData = new HashMap<Integer, CalendarWnd>();

    public CalendarWindowControl(Activity activity, Handler wndHandler) {
        if ((null != activity) && (null != wndHandler)) {
            initWnd(activity, wndHandler);
        }
    }

    private void initWnd(Activity activity, Handler wndHandler) {
        releaseWnd();
        WndData.put(EventMessage.RUN_LOADWND, new CalendarLoadWnd(activity, wndHandler, EventMessage.RUN_LOADWND));
        WndData.put(EventMessage.RUN_LOGINWND, new CalendarLoginWnd(activity, wndHandler, EventMessage.RUN_LOGINWND));
        WndData.put(EventMessage.RUN_MONTHWND, new CalendarMonthWnd(activity, wndHandler, EventMessage.RUN_MONTHWND));
        WndData.put(EventMessage.RUN_LOGOUTWND,
                    new CalendarLogoutWnd(activity, wndHandler, EventMessage.RUN_LOGOUTWND));
        WndData.put(EventMessage.RUN_DAYWND, new CalendarDayWnd(activity, wndHandler, EventMessage.RUN_DAYWND));
        WndData.put(EventMessage.RUN_EVENTINFOWND,
                    new CalendarDayEventInfoWnd(activity, wndHandler, EventMessage.RUN_EVENTINFOWND));
        WndData.put(EventMessage.RUN_NEWEVENTWND,
                    new CalendarNewEventWnd(activity, wndHandler, EventMessage.RUN_NEWEVENTWND));
        WndData.put(EventMessage.RUN_DATEWND, new CalendarDateWnd(activity, wndHandler, EventMessage.RUN_DATEWND));
        WndData.put(EventMessage.RUN_SYNCWND, new CalendarSyncDlg(activity, wndHandler, EventMessage.RUN_SYNCWND));
        WndData.put(EventMessage.RUN_SETTINGWND,
                    new CalendarSettingWnd(activity, wndHandler, EventMessage.RUN_SETTINGWND));
        WndData.put(EventMessage.RUN_EDITEVENTWND,
                    new CalendarNewEventWnd(activity, wndHandler, EventMessage.RUN_EDITEVENTWND));
        WndData.put(EventMessage.RUN_TIMEWND, new CalendarTimeWnd(activity, wndHandler, EventMessage.RUN_TIMEWND));
    }

    public void releaseWnd() {
        if (WndData != null) {
            if (WndData.size() > 0) {
                for (int i = (EventMessage.RUN_WND + 1); i < EventMessage.RUN_MAX; i++) {
                    if (WndData.get(i) != null) {
                        WndData.get(i).closeWindow();
                    }
                }

                WndData.clear();
            }
        }
    }

    public CalendarWnd getWindow(int nWndId) {
        if (WndData.containsKey(nWndId)) {
            return WndData.get(nWndId);
        }

        return null;
    }

    public void showWindow(int nWndId) {
        if (isValidWnd(nWndId)) {
            WndData.get(nWndId).showWindow(true);
        }
    }

    public boolean isValidWnd(int nWndId) {
        return WndData.containsKey(nWndId);
    }
}

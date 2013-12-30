//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.appwidget.AppWidgetManager;

import android.content.Context;
import android.content.Intent;

public class CalendarWidgetBroadcast {
    private static final String TAG             = "CalendarWidgetBroadcast";
    public final static int     CALENDAR_LOGOUT = 0x00B3;
    public final static int     CALENDAR_UPDATE = 0x00B2;
    public final static int     WIDGET_DEFAULT  = 0x00B1;
    public final static int     mnMaxEventCount = 5;
    private String              mszName0        = null;
    private String              mszName1        = null;
    private String              mszName2        = null;
    private String              mszName3        = null;
    private String              mszName4        = null;
    private String              mszTask0        = null;
    private String              mszTask1        = null;
    private String              mszTask2        = null;
    private String              mszTask3        = null;
    private String              mszTask4        = null;
    private String              mszTime0        = null;
    private String              mszTime1        = null;
    private String              mszTime2        = null;
    private String              mszTime3        = null;
    private String              mszTime4        = null;

    public CalendarWidgetBroadcast() {}

    public void addEvent(int nIndex, String szName, String szTask, String szTime) {
        switch (nIndex) {
        case 0 :
            mszName0 = szName;
            mszTask0 = szTask;
            mszTime0 = szTime;

            break;
        case 1 :
            mszName1 = szName;
            mszTask1 = szTask;
            mszTime1 = szTime;

            break;
        case 2 :
            mszName2 = szName;
            mszTask2 = szTask;
            mszTime2 = szTime;

            break;
        case 3 :
            mszName3 = szName;
            mszTask3 = szTask;
            mszTime3 = szTime;

            break;
        case 4 :
            mszName4 = szName;
            mszTask4 = szTask;
            mszTime4 = szTime;

            break;
        }
    }

    public void broadcastMsg(Context context, int nId) {
        Intent i = new Intent(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);

        i.putExtra("id", nId);
        i.putExtra("Name0", mszName0);
        i.putExtra("Name1", mszName1);
        i.putExtra("Name2", mszName2);
        i.putExtra("Name3", mszName3);
        i.putExtra("Name4", mszName4);
        i.putExtra("Task0", mszTask0);
        i.putExtra("Task1", mszTask1);
        i.putExtra("Task2", mszTask2);
        i.putExtra("Task3", mszTask3);
        i.putExtra("Task4", mszTask4);
        i.putExtra("Time0", mszTime0);
        i.putExtra("Time1", mszTime1);
        i.putExtra("Time2", mszTime2);
        i.putExtra("Time3", mszTime3);
        i.putExtra("Time4", mszTime4);
        context.sendBroadcast(i);
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.handler;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;

import com.hp.ij.calendar.CalendarApplication;

public class CalendarBroadcaster extends BroadcastReceiver {
    private static final String TAG              = "CalendarBroadcaster";
    public static final String  WIDGET_REQUEST   = "widget.request";
    public static final String  ACTION           = "ACTION";
    public static final int     WIDGET_ON_UPDATE = 0x01;
    private CalendarApplication application      = null;

    public CalendarBroadcaster(CalendarApplication app) {
        application = app;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO Auto-generated method stub
        Bundle bundle  = intent.getExtras();
        int    nAction = bundle.getInt(ACTION);

        switch (nAction) {
        case WIDGET_ON_UPDATE :
            application.CService.updateWidget();

            break;
        }
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter("com.hp.ij.calendar.handler.CalendarBroadcaster");

        intentFilter.addAction(WIDGET_REQUEST);

        if (null != application) {
            application.registerReceiver(this, intentFilter);
        }
    }

    public void unRegisterReceiver() {
        if (null != application) {
            application.unregisterReceiver(this);
        }
    }
}

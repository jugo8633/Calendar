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

import frame.view.SharedData;

public class CalendarLoginEvent implements CalendarEvent {
    private static final String TAG         = "CalendarLoginEvent";
    private CalendarApplication application = null;
    private Activity            theAct      = null;

    public CalendarLoginEvent(Activity actMain) {
        theAct      = actMain;
        application = (CalendarApplication) actMain.getApplication();
    }

    public int handleEvent(int nMsg, Object objData) {
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            break;
        case EventMessage.WND_STOP :
            break;
        case EventMessage.WND_EXCP :
            break;
        case EventMessage.WND_BTN_OK :
            String[] aszLogin = (String[]) objData;

            if (null != aszLogin) {
                String szCaption = theAct.getString(R.string.login);

                ((CalendarApp) theAct).showProgressDlg(true, szCaption, TAG + "74");

                if (application.CService != null) {
                    application.CService.doLogin(aszLogin[0], aszLogin[1]);
                } else {
                    ((CalendarApp) theAct).showProgressDlg(false, null, TAG + "79");
                }

                SharedData sdata = new SharedData(theAct, null, 0);

                sdata.initPreferences("CalendarConf");

                if (EventMessage.TRUE == aszLogin[2]) {    // save password
                    sdata.setValue("ACCOUNT", aszLogin[0]);
                    sdata.setValue("PASSWORD", aszLogin[1]);
                    sdata.setValue("SAVEACCOUNT", EventMessage.TRUE);
                } else {
                    sdata.setValue("SAVEACCOUNT", EventMessage.FALSE);
                }

                sdata.closeWindow();
                sdata = null;
            } else {
                ((CalendarApp) theAct).showAlarmDlg(true, null, "Login Fail");
            }

            break;
        case EventMessage.WND_BTN_SKIP :
            return EventMessage.RUN_MONTHWND;
        }

        return 0;
    }
}

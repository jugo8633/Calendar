//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Activity;

import android.os.Handler;

import android.widget.TextView;

import frame.event.EventMessage;

public class CalendarLogoutWnd extends CalendarWnd {
    private static final String TAG                = "CalendarLogoutWnd";
    private final int           m_nMainLayoutResId = R.layout.calendar_logout;

    public CalendarLogoutWnd(Activity active, Handler handler, int id) {
        super(active, handler, id);

        // TODO Auto-generated constructor stub
        super.setLayoutResId(m_nMainLayoutResId, HIDE_VIEW, HIDE_VIEW);
        super.setViewTouchEvent(R.id.tvLogoutBtnNo);
        super.setViewTouchEvent(R.id.tvLogoutBtnYes);
    }

    @Override
    protected void onShow() {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onClose() {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onClick(int resId) {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onTouchDown(int resId) {

        // TODO Auto-generated method stub
        TextView tv = null;

        if (isBtn(resId)) {
            tv = (TextView) super.getApp().findViewById(resId);

            if (null != tv) {
                tv.setBackgroundResource(R.drawable.ok_btn_focus);
            }
        }
    }

    @Override
    protected void onTouchUp(int resId) {

        // TODO Auto-generated method stub
        TextView tv = null;

        if (isBtn(resId)) {
            tv = (TextView) super.getApp().findViewById(resId);

            if (null != tv) {
                tv.setBackgroundResource(R.drawable.ok_btn);
            }
        }

        switch (resId) {
        case R.id.tvLogoutBtnNo :
            sendAppMsg(WND_MSG, EventMessage.WND_LOGOUT_NO, null);

            break;
        case R.id.tvLogoutBtnYes :
            sendAppMsg(WND_MSG, EventMessage.WND_LOGOUT_YES, null);

            break;
        }
    }

    private boolean isBtn(int nResId) {
        if ((nResId == R.id.tvLogoutBtnNo) || (nResId == R.id.tvLogoutBtnYes)) {
            return true;
        }

        return false;
    }
}

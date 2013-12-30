//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import android.widget.HpFooterBarButton;

import com.hp.ij.calendar.CalendarDayEventInfoWnd;
import com.hp.ij.calendar.CalendarWnd;
import com.hp.ij.calendar.R;

import frame.event.EventMessage;

public class CalendarEventFootBarControl extends CalendarButtonControl {
    private static final String TAG          = "CalendarMonthFootBarControl";
    private CalendarWnd         mCalendarWnd = null;

    public CalendarEventFootBarControl(CalendarWnd wnd) {
        super(wnd);
        mCalendarWnd = wnd;
    }

    /**
     * initial button
     */
    public void initButton(int nDefaultResId) {
        if (null == mCalendarWnd.getActivity()) {
            return;
        }

        clearButton();
        addButton(R.id.fbEdit);
        addButton(R.id.fbDelete);
        addButton(R.id.fbPrint);
        setOneSelected(nDefaultResId);
    }

    public boolean buttonClickHandle(int nResId) {
        switch (nResId) {

        /**
         * foot bar button click
         */
        case R.id.fbEdit :
        case R.id.fbDelete :
        case R.id.fbPrint :
            if (setOneSelected(nResId)) {
                buttonFootBarClickHandle(nResId);
            }

            break;
        default :
            setOneSelected(-1);

            return false;
        }

        return true;
    }

    private void buttonFootBarClickHandle(int nResId) {
        String szEventId = ((CalendarDayEventInfoWnd) mCalendarWnd).getEventId();

        switch (nResId) {
        case R.id.fbEdit :
            if (null != szEventId) {
                mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_EDIT, szEventId);
            }

            break;
        case R.id.fbDelete :
            if (null != szEventId) {
                mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_DELETE, szEventId);
            }

            break;
        case R.id.fbPrint :
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_PRINT, null);

            break;
        }
    }

    public void buttonDownHandle(int nResId) {
        HpFooterBarButton fb = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nResId);

        if (null == fb) {
            return;
        }

        setOneSelected(-1);

        switch (nResId) {
        case R.id.fbEdit :
        case R.id.fbDelete :
        case R.id.fbPrint :
            fb.setSelect(true);

            break;
        }

        fb = null;
    }

    public void buttonUpHandle(int nResId) {
        HpFooterBarButton fb = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nResId);

        if (null == fb) {
            return;
        }

        switch (nResId) {
        case R.id.fbEdit :
        case R.id.fbDelete :
        case R.id.fbPrint :
            fb.setSelect(false);

            break;
        }

        fb = null;
    }

    public void setBtnEditEnable(boolean bEnable) {
        super.setEnable(R.id.fbEdit, bEnable);
    }

    public void setBtnDeleteEnable(boolean bEnable) {
        super.setEnable(R.id.fbDelete, bEnable);
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import java.util.ArrayList;

import android.widget.HpFooterBarButton;

import com.hp.ij.calendar.CalendarWnd;

public abstract class CalendarButtonControl {
    private CalendarWnd        mCalendarWnd  = null;
    private ArrayList<Integer> maButtonResId = new ArrayList<Integer>();

    public CalendarButtonControl(CalendarWnd wnd) {
        mCalendarWnd = wnd;
    }

    protected void addButton(int nResId) {
        maButtonResId.add(nResId);
    }

    /**
     * set one button of foot bar what be selected
     * @param nSelResId : id of selected button
     */
    protected boolean setOneSelected(int nSelResId) {
        if (-1 == nSelResId) {
            clearSelected();

            return false;
        }

        HpFooterBarButton fb = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nSelResId);

        if (null != fb) {
            if (!fb.isEnable()) {
                return false;
            }
        }

        clearSelected();

        HpFooterBarButton footBtn = null;

        footBtn = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nSelResId);

        if (null != footBtn) {
            footBtn.setSelect(true);
        }

        footBtn = null;

        return true;
    }

    private void clearSelected() {
        HpFooterBarButton footBtn = null;
        int               nResId  = -1;

        if ((null == maButtonResId) || (maButtonResId.size() <= 0)) {
            return;
        }

        for (int nIndex = 0; nIndex < maButtonResId.size(); nIndex++) {
            footBtn = null;
            nResId  = maButtonResId.get(nIndex);

            if (0 >= nResId) {
                continue;
            }

            footBtn = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nResId);

            if (null != footBtn) {
                footBtn.setSelect(false);
            }
        }
    }

    protected void clearButton() {
        if ((null == maButtonResId) || (maButtonResId.size() <= 0)) {
            return;
        }

        maButtonResId.clear();
    }

    protected void setEnable(int nResId, boolean bEnable) {
        HpFooterBarButton footBtn = null;

        footBtn = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nResId);

        if (null != footBtn) {
            footBtn.setEnable(bEnable);
            footBtn = null;
        }
    }

    protected int getSelectedResourceId() {
        HpFooterBarButton footBtn = null;
        int               nResId  = -1;

        if ((null == maButtonResId) || (maButtonResId.size() <= 0)) {
            return -1;
        }

        for (int nIndex = 0; nIndex < maButtonResId.size(); nIndex++) {
            footBtn = null;
            nResId  = maButtonResId.get(nIndex);

            if (0 >= nResId) {
                continue;
            }

            footBtn = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nResId);

            if (footBtn.isSelected()) {
                return nResId;
            }
        }

        footBtn = null;

        return -1;
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;

import android.os.Handler;

import android.view.KeyEvent;

import com.hp.ij.calendar.data.CalendarNewEventData;
import com.hp.ij.calendar.dialog.DialogFactory;

import frame.event.EventMessage;

public class CalendarDateWnd extends CalendarWnd {
    private static final String  TAG                = "CalendarDateWnd";
    private CalendarApplication  application        = null;
    private DialogFactory        dialog             = null;
    private CalendarMonthGallery galleryMonth       = null;
    private Handler              mHandler           = null;
    private final int            m_nMainLayoutResId = R.layout.calendar_month_main;

    public CalendarDateWnd(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
        super.setLayoutResId(m_nMainLayoutResId, HIDE_VIEW, HIDE_VIEW);
        application = ((CalendarApp) active).application;
        mHandler    = handler;
        dialog      = new DialogFactory(getActivity(), EventMessage.RUN_DATEWND, false);
        dialog.getDialog().setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    CalendarNewEventData calendarNewEventData = new CalendarNewEventData(application.getCalendarData());

                    calendarNewEventData.setDateDialogShowStatus(0);
                    calendarNewEventData = null;
                    CalendarDateWnd.this.getActivity().removeDialog(EventMessage.RUN_DATEWND);
                }

                return true;
            }
        });
    }

    public void initDialog() {
        dialog.setCntent(m_nMainLayoutResId);
        galleryMonth = null;
        galleryMonth = new CalendarMonthGallery(dialog.getDialog(), mHandler, application);

        int nOrientation = application.getOrientation();

        if (application.ORIENTATION_LAND == nOrientation) {
            if (galleryMonth != null) {
                galleryMonth.initMonthGallery(galleryMonth.DATE_TYPE, galleryMonth.VIEW_GALLERY);
            }
        } else if (application.ORIENTATION_PORT == nOrientation) {
            if (galleryMonth != null) {
                galleryMonth.initMonthGallery(galleryMonth.DATE_TYPE, galleryMonth.VIEW_LISTVIEW);
            }
        }
    }

    public Dialog getDialog() {
        return dialog.getDialog();
    }

    @Override
    protected void onShow() {
        int nOrientation = application.getOrientation();

        if (application.ORIENTATION_LAND == nOrientation) {
            if (galleryMonth != null) {
                galleryMonth.initMonthGallery(galleryMonth.DATE_TYPE, galleryMonth.VIEW_GALLERY);
            }
        } else if (application.ORIENTATION_PORT == nOrientation) {
            if (galleryMonth != null) {
                galleryMonth.initMonthGallery(galleryMonth.DATE_TYPE, galleryMonth.VIEW_LISTVIEW);
            }
        }
    }

    @Override
    protected void onClick(int nResId) {}

    @Override
    protected void onClose() {
        if (null != galleryMonth) {
            galleryMonth = null;
        }

        if (null != dialog) {
            dialog = null;
        }
    }

    @Override
    protected void onTouchDown(int nResId) {}

    @Override
    protected void onTouchUp(int nResId) {}
}

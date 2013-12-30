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
import android.os.Message;

import android.widget.TextView;

import com.hp.ij.calendar.controler.CalendarHeaderControl;
import com.hp.ij.calendar.controler.CalendarMonthFootBarControl;

public class CalendarMonthWnd extends CalendarWnd {
    private static final String         TAG                        = "CalendarMonthWnd";
    private final int                   WND_UPDATE_FOOT_BAR_SELECT = 0;
    private CalendarMonthGallery        galleryMonth               = null;
    private CalendarMonthFootBarControl mFootbarControler          = null;
    private CalendarHeaderControl       mHeaderControler           = null;
    private final int                   m_nMainLayoutResId         = R.layout.calendar_month_main;
    private final int                   m_nHeaderLayoutResId       = R.layout.calendar_header;
    private final int                   m_nFootLayoutResId         = R.layout.calendar_footbar_month;

    /**
     * handlerMonthView is a message handler for month gallery
     */
    Handler handlerMonthView = new Handler() {
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);
        }
    };

    public CalendarMonthWnd(Activity active, Handler handler, int id) {
        super(active, handler, id);

        /**
         * set layout, inflate header, body, foot
         */
        super.setLayoutResId(m_nMainLayoutResId, m_nFootLayoutResId, m_nHeaderLayoutResId);

        /**
         * register button event
         */
        super.setViewClickEvent(R.id.fbMonthView);
        super.setViewClickEvent(R.id.fbDayView);
        super.setViewClickEvent(R.id.fbToday);
        super.setViewClickEvent(R.id.fbAdd);
        super.setViewClickEvent(R.id.fbPrint);
        super.setViewClickEvent(R.id.fbMore);
        super.setViewClickEvent(R.id.tvCalendarHeaderLogout);
        super.setViewTouchEvent(R.id.fbMonthView);
        super.setViewTouchEvent(R.id.fbDayView);
        super.setViewTouchEvent(R.id.fbToday);
        super.setViewTouchEvent(R.id.fbAdd);
        super.setViewTouchEvent(R.id.fbPrint);
        super.setViewTouchEvent(R.id.fbMore);
        super.setViewTouchEvent(R.id.rlMonthMultiCalendarMain);
        super.setViewTouchEvent(R.id.rlMonthMenuSyncMain);

        /**
         * create month gallery
         */
        galleryMonth = new CalendarMonthGallery(active, handlerMonthView);

        /**
         * create foot bar button control
         */
        mFootbarControler = new CalendarMonthFootBarControl(this, R.id.rlMonthMenuList);

        /**
         * create header bar control
         */
        mHeaderControler = new CalendarHeaderControl(this);
    }

    @Override
    protected void onShow() {
        setHeaderTitle(super.getApp().getString(R.string.FC_month_view));
        mFootbarControler.initButton(R.id.fbMonthView, R.id.rlMonthMenuList);
        initMonthGallery();
    }

    @Override
    protected void onClose() {
        if (null != galleryMonth) {
            galleryMonth.close();
            galleryMonth = null;
        }

        mFootbarControler = null;
        mHeaderControler  = null;
    }

    @Override
    protected void onClick(int resId) {
        if (!mFootbarControler.buttonClickHandle(resId)) {
            if (!mHeaderControler.buttonHeaderClickHandle(resId)) {}
        }
    }

    @Override
    protected void onTouchDown(int nResId) {
        mFootbarControler.buttonDownHandle(nResId);
    }

    @Override
    protected void onTouchUp(int resId) {
        mFootbarControler.buttonUpHandle(resId);
    }

    private void initMonthGallery() {
        int nOrientation = application.getOrientation();

        if (application.ORIENTATION_LAND == nOrientation) {
            if (galleryMonth != null) {
                if (galleryMonth.initMonthGallery(galleryMonth.MONTH_TYPE, galleryMonth.VIEW_GALLERY)) {
                    sendAppMsg(WND_MSG, WND_MONTH_VIEW, "SUCCESS");
                } else {
                    sendAppMsg(WND_MSG, WND_MONTH_VIEW, "FAIL");
                }
            }
        } else if (application.ORIENTATION_PORT == nOrientation) {
            if (galleryMonth != null) {
                if (galleryMonth.initMonthGallery(galleryMonth.MONTH_TYPE, galleryMonth.VIEW_LISTVIEW)) {
                    sendAppMsg(WND_MSG, WND_MONTH_VIEW, "SUCCESS");
                } else {
                    sendAppMsg(WND_MSG, WND_MONTH_VIEW, "FAIL");
                }
            }
        }
    }

    public void updateMonthEvent() {
        if (galleryMonth != null) {
            galleryMonth.updateMonthEvent();
        }
    }

    public void goToDay() {
        if (galleryMonth != null) {
            galleryMonth.goToDay();
        }

        if (!application.checkNetWorkEnable()) {
            setFootbarSelect(R.id.fbMonthView);

            return;
        }
        
        // +++++ add by Chance 2010-08-19 for Mantis[0007551] +++++ //
        application.setCurrYear(application.getYearOfToday());
        application.setCurrMonth(application.getMonthOfToday());
        // ----- add by Chance 2010-08-19 for Mantis[0007551] ----- //

        application.setSelYear(application.getYearOfToday());
        application.setSelMonth(application.getMonthOfToday());
        application.setSelDay(application.getDayOfToday());
        sendAppMsg(WND_MSG, WND_DAY_VIEW, null);
    }

    public void setHeaderTitle(String szTitle) {
        if (null == szTitle) {
            return;
        }

        TextView tvTmp = (TextView) super.getApp().findViewById(R.id.tvCalendarHeaderTitle);

        if (null != tvTmp) {
            tvTmp.setText(szTitle);
        }
    }

    public void reflesgMonthView() {
        if (galleryMonth != null) {
            galleryMonth.refleshMonth();
        }
    }

    public void initButton() {
        mFootbarControler.initButton(R.id.fbMonthView, R.id.rlMonthMenuList);
    }

    public void setFootbarSelect(int nResID) {
        Message msg = new Message();

        msg.what = WND_UPDATE_FOOT_BAR_SELECT;
        msg.arg1 = nResID;
        wndHandler.sendMessage(msg);
        msg = null;
    }

    Handler wndHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);

            switch (msg.what) {
            case WND_UPDATE_FOOT_BAR_SELECT :
                mFootbarControler.buttonClickHandle(msg.arg1);

                break;
            }
        }
    };
}

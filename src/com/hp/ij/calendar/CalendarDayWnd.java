//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.Calendar;

import android.app.Activity;

import android.os.Handler;
import android.os.Message;

import android.widget.TextView;

import com.hp.ij.calendar.controler.CalendarHeaderControl;
import com.hp.ij.calendar.controler.CalendarMonthFootBarControl;
import com.hp.ij.calendar.data.CalendarDayViewData;

import frame.event.EventMessage;

import frame.view.ListViewEx;

public class CalendarDayWnd extends CalendarWnd {
    private static final String         TAG                        = "CalendarDayWnd";
    private final int                   WND_UPDATE_FOOT_BAR_SELECT = 0;
    private CalendarMonthGallery        galleryMonth               = null;
    private CalendarDayListEvent        listEvent                  = null;
    private CalendarDayViewData         mCalendarDayViewData       = null;
    private CalendarMonthFootBarControl mFootbarControler          = null;
    private CalendarHeaderControl       mHeaderControler           = null;
    public Activity                     mTheActivity               = null;
    private int                         m_nMainLayoutResId         = R.layout.calendar_day_main;
    private final int                   m_nHeaderLayoutResId       = R.layout.calendar_header;
    private final int                   m_nFootLayoutResId         = R.layout.calendar_footbar_month;

    /**
     * handlerDayView is a message handler for month gallery
     * for click calendar month to show day event items
     */
    Handler handlerDayView = new Handler() {
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if (!application.checkNetWorkEnable()) {
                application.getEventData().clearEventDetail();
                initEventList();

                return;
            }

            if (!application.isEventDate(application.getSelYear(), application.getSelMonth(),
                                         application.getSelDay())) {
                ((CalendarApp) CalendarDayWnd.this.getApp()).showProgressDlg(true, null, TAG + "61");
                application.getEventData().clearEventDetail();
                initEventList();
                ((CalendarApp) CalendarDayWnd.this.getApp()).showProgressDlg(false, null, TAG + "64");

                return;
            }

            int nYearSelBefore  = mCalendarDayViewData.getCurrentEventYear();
            int nMonthSelBefore = mCalendarDayViewData.getCurrentEventMonth();
            int nDaySelBefore   = mCalendarDayViewData.getCurrentEventDay();

            if ((nYearSelBefore == application.getSelYear()) && (nMonthSelBefore == application.getSelMonth())
                    && (nDaySelBefore == application.getSelDay())) {
                return;
            }

            ((CalendarApp) mTheActivity).showProgressDlg(true, null, TAG + "78");
            application.clearEventDetailData();

            String szCurrDate = String.format("%04d-%02d-%02d", application.getSelYear(), application.getSelMonth(),
                                              application.getSelDay());

            application.setCurrDate();
            application.CService.getEvent(szCurrDate);
        }
    };

    public CalendarDayWnd(Activity active, Handler handler, int id) {
        super(active, handler, id);
        mTheActivity         = active;
        mCalendarDayViewData = new CalendarDayViewData(application.getCalendarData());

        int nOrientation = application.getOrientation();

        if (application.ORIENTATION_LAND == nOrientation) {
            m_nMainLayoutResId = R.layout.calendar_day_main;
        } else if (application.ORIENTATION_PORT == nOrientation) {
            m_nMainLayoutResId = R.layout.calendar_day_main_v;
        }

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
        galleryMonth = new CalendarMonthGallery(active, handlerDayView);
        listEvent    = new CalendarDayListEvent(active, this);

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
        setHeaderTitle(super.getApp().getString(R.string.FC_day_view));
        mFootbarControler.initButton(R.id.fbDayView, R.id.rlDayMenuList);
        initEventList();
        initMonthGallery();
    }

    public void initCalendarDayWnd() {
        int nOrientation = application.getOrientation();

        if (application.ORIENTATION_LAND == nOrientation) {
            m_nMainLayoutResId = R.layout.calendar_day_main;
        } else if (application.ORIENTATION_PORT == nOrientation) {
            m_nMainLayoutResId = R.layout.calendar_day_main_v;
        }

        /**
         * set layout, inflate header, body, foot
         */
        super.setLayoutResId(m_nMainLayoutResId, m_nFootLayoutResId, m_nHeaderLayoutResId);
    }

    private void initMonthGallery() {
        int nOrientation = application.getOrientation();

        if (application.ORIENTATION_LAND == nOrientation) {
            if (galleryMonth != null) {
                galleryMonth.initMonthGallery(galleryMonth.DAY_TYPE, galleryMonth.VIEW_LISTVIEW);
            }
        } else if (application.ORIENTATION_PORT == nOrientation) {
            if (galleryMonth != null) {
                galleryMonth.initMonthGallery(galleryMonth.DAY_TYPE, galleryMonth.VIEW_GALLERY);
            }
        }
    }

    private void initEventList() {
        ListViewEx lvEvent = null;

        lvEvent = (ListViewEx) super.getApp().findViewById(R.id.lvDayEvent);

        if (null != lvEvent) {
            if (null != listEvent) {
                CalendarMonth calendarMonth = new CalendarMonth();
                String        szMonth       = calendarMonth.getMonthName(application.getSelMonth());
                Calendar      now           = Calendar.getInstance();

                now.set(application.getSelYear(), application.getSelMonth() - 1, application.getSelDay());

                int    nDayOfWeek  = (int) (now.get(Calendar.DAY_OF_WEEK));
                String szDay       = calendarMonth.getDayOfWeekName(nDayOfWeek);
                String szTitleDate = String.format("%s, %s %d, %d", szDay, szMonth, application.getSelDay(),
                                                   application.getSelYear());
                TextView tvTmp = (TextView) super.getApp().findViewById(R.id.tvDayEventTitle);    // title

                tvTmp.setText(szTitleDate);

                int nSize = mCalendarDayViewData.setCurrentEventData(application.getSelYear(),
                                application.getSelMonth(), application.getSelDay());

                if (!listEvent.initEventList()) {}
            }
        }
    }

    @Override
    protected void onClose() {
        if (null != galleryMonth) {
            galleryMonth.close();
            galleryMonth = null;
        }

        if (null != listEvent) {
            listEvent.close();
            listEvent = null;
        }

        mCalendarDayViewData = null;
        mFootbarControler    = null;
        mHeaderControler     = null;
    }

    @Override
    protected void onClick(int resId) {
        if (!mFootbarControler.buttonClickHandle(resId)) {
            if (!mHeaderControler.buttonHeaderClickHandle(resId)) {
                if (!eventListItemSelected(resId)) {}
            }
        }

        return;
    }

    @Override
    protected void onTouchDown(int resId) {
        mFootbarControler.buttonDownHandle(resId);
    }

    @Override
    protected void onTouchUp(int resId) {
        mFootbarControler.buttonUpHandle(resId);
    }

    private boolean eventListItemSelected(int nSelResId) {
        if (null == listEvent) {
            return false;
        }

        String   szTitle = null;
        TextView tvTmp   = (TextView) super.getApp().findViewById(R.id.tvEventListTitle);

        if (null != tvTmp) {
            szTitle = tvTmp.getText().toString();
        } else {
            return false;
        }

        application.getEventData().setCurrDayEventSelected(nSelResId);
        application.getEventData().setCurrDayEventTitle(szTitle);
        sendAppMsg(WND_MSG, WND_EVENTSEL, null);

        return true;
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
            setFootbarSelect(R.id.fbDayView);

            return;
        }
        
        // +++++ add by Chance 2010-08-19 for Mantis[0007551] +++++ //
        application.setCurrYear(application.getYearOfToday());
        application.setCurrMonth(application.getMonthOfToday());
        // ----- add by Chance 2010-08-19 for Mantis[0007551] ----- //

        /**
         * Go to Today Day view
         */
        application.setSelYear(application.getYearOfToday());
        application.setSelMonth(application.getMonthOfToday());
        application.setSelDay(application.getDayOfToday());
        sendAppMsg(WND_MSG, WND_DAY_VIEW, null);

        /**
         * requery event
         */
        Message msg = new Message();

        msg.what = EventMessage.WND_MSG;            // message type
        msg.arg1 = EventMessage.WND_DATE_SELECT;    // message
        handlerDayView.sendMessage(msg);
        setFootbarSelect(R.id.fbDayView);
    }

    public void reflesgMonthView() {
        if (galleryMonth != null) {
            galleryMonth.refleshMonth();
        }
    }

    public void initButton() {
        mFootbarControler.initButton(R.id.fbDayView, R.id.rlDayMenuList);
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

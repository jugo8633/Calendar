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

import com.hp.ij.calendar.controler.CalendarEventFootBarControl;
import com.hp.ij.calendar.controler.CalendarHeaderControl;

public class CalendarDayEventInfoWnd extends CalendarWnd {
    private static final String         TAG                        = "CalendarDayEventInfoWnd";
    private final int                   WND_UPDATE_FOOT_BAR_SELECT = 0;
    private final int                   WND_INIT_FOOT_BAR          = 1;
    private CalendarEventFootBarControl mFootbarControler          = null;
    private CalendarHeaderControl       mHeaderControler           = null;
    private final int                   m_nMainLayoutResId         = R.layout.calendar_day_event_info;
    private final int                   m_nHeaderLayoutResId       = R.layout.calendar_header_contextual;
    private final int                   m_nFootLayoutResId         = R.layout.calendar_footbar_event;
    private String                      mszEventId                 = null;

    public CalendarDayEventInfoWnd(Activity active, Handler handler, int id) {
        super(active, handler, id);
        super.setLayoutResId(m_nMainLayoutResId, m_nFootLayoutResId, m_nHeaderLayoutResId);
        super.setViewTouchEvent(R.id.fbEdit);
        super.setViewTouchEvent(R.id.fbDelete);
        super.setViewTouchEvent(R.id.fbPrint);
        super.setViewClickEvent(R.id.tvCalendarHeaderBack);
        super.setViewClickEvent(R.id.tvCalendarHeaderLogout);
        super.setViewClickEvent(R.id.fbEdit);
        super.setViewClickEvent(R.id.fbDelete);
        super.setViewClickEvent(R.id.fbPrint);

        /**
         * create foot bar button control
         */
        mFootbarControler = new CalendarEventFootBarControl(this);

        /**
         * create header bar control
         */
        mHeaderControler = new CalendarHeaderControl(this);
    }

    @Override
    protected void onShow() {

        // TODO Auto-generated method stub

        /**
         * initial foot bar button
         */
        initButton();

        /**
         * initial event data on view
         */
        if (null != application.getEventData()) {
            String  szTitle    = application.getEventData().getCurrDayEventTitle();
            int     nIndex     = application.getEventData().getCurrDayEventSelected();
            String  szCalendar = application.getEventData().getDetailCalendar(nIndex);
            String  szEvent    = application.getEventData().getDetailEvent(nIndex);
            String  szTime     = application.getEventData().getDetailTime(nIndex);
            String  szReminder = application.getEventData().getDetailReminder(nIndex);
            String  szWhere    = application.getEventData().getDetailWhere(nIndex);
            String  szMessage  = application.getEventData().getDetailMessage(nIndex);
            boolean bEditAble  = application.getEventData().getDetailEditAble(nIndex);

            mszEventId = application.getEventData().getEventId(nIndex);

            Calendar CalendarStart = application.getEventData().getDetailDateStart(nIndex);
            Calendar CalendarEnd   = application.getEventData().getDetailDateEnd(nIndex);

            setHeaderTitle(szTitle);

            TextView tvTmp = null;

            /**
             * set event calendar title
             */
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvEventInfoCalendar);

            if (null != tvTmp) {
                tvTmp.setText(szCalendar);
            }

            /**
             * set event time
             */
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvEventInfoTime);

            if (null != tvTmp) {
                String SS = null,
                       EE = null;

                SS = CalendarStart.get(Calendar.MONTH) + 1 + "/" + CalendarStart.get(Calendar.DAY_OF_MONTH);
                EE = CalendarEnd.get(Calendar.MONTH) + 1 + "/" + CalendarEnd.get(Calendar.DAY_OF_MONTH);

                if (szTime == application.getString(R.string.all_day)) {
                    szTime = SS + " " + szTime;
                } else {
                    String[] sAry = szTime.split(" - ");

                    if (sAry.length >= 2) {
                        szTime = SS + " " + sAry[0] + " - " + EE + " " + sAry[1];
                    } else {
                        szTime = SS + " - " + EE;
                    }
                }

                tvTmp.setText(szTime);
            }

            /**
             * set event reminder
             */
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvEventInfoReminder);

            if (null != tvTmp) {
                int nReminder = 0;

                if ((null == szReminder) || (0 >= szReminder.length())) {
                    nReminder = 0;
                }

                try {
                    nReminder = Integer.valueOf(szReminder);
                } catch (Exception e) {
                    nReminder = 0;
                }

                szReminder = formatReminderString(nReminder);
                tvTmp.setText(szReminder);
            }

            /**
             * set event title
             */
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvEventInfoEvent);

            if (null != tvTmp) {
                tvTmp.setText(szEvent);
            }

            /**
             * set event where
             */
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvEventInfoWhere);

            if (null != tvTmp) {
                StringBuffer strBuf = new StringBuffer("Where : ");

                if ((null == szWhere) || (szWhere.length() <= 0)) {
                    strBuf.append("none");
                } else {
                    strBuf.append(szWhere);
                }

                tvTmp.setText(strBuf.toString());
            }

            /**
             * set event description
             */
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvEventInfoMessage);

            if (null != tvTmp) {
                tvTmp.setText(szMessage);
            }

            /**
             * set event edit enable
             */
            setEventEditEnable(bEditAble);
        }
    }

    @Override
    protected void onClose() {

        // TODO Auto-generated method stub
        mFootbarControler = null;
        mHeaderControler  = null;
    }

    @Override
    protected void onClick(int resId) {

        // TODO Auto-generated method stub
        if (!mFootbarControler.buttonClickHandle(resId)) {
            if (!mHeaderControler.buttonHeaderClickHandle(resId)) {}
        }
    }

    @Override
    protected void onTouchDown(int resId) {
        mFootbarControler.buttonDownHandle(resId);
    }

    @Override
    protected void onTouchUp(int resId) {
        mFootbarControler.buttonUpHandle(resId);
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

    private String formatReminderString(int nTime) {
        if (0 >= nTime) {
            return "No Reminder";
        }

        int    nCount         = 0;
        String szReminderItem = null;

        if (1440 <= nTime) {
            nCount         = nTime / 1440;
            szReminderItem = String.format("Reminder: %d days before", nCount);
        } else if ((1440 > nTime) && (60 <= nTime) && (0 == (nTime % 60))) {
            nCount         = nTime / 60;
            szReminderItem = String.format("Reminder: %d hour before", nCount);
        } else {
            szReminderItem = String.format("Reminder: %d min before", nTime);
        }

        return szReminderItem;
    }

    public String getEventId() {
        return mszEventId;
    }

    public void initButton() {
        wndHandler.sendEmptyMessage(WND_INIT_FOOT_BAR);
    }

    private void setEventEditEnable(boolean bEnable) {
        mFootbarControler.setBtnEditEnable(bEnable);
        mFootbarControler.setBtnDeleteEnable(bEnable);
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
            case WND_INIT_FOOT_BAR :
                mFootbarControler.initButton(-1);

                break;
            case WND_UPDATE_FOOT_BAR_SELECT :
                mFootbarControler.buttonClickHandle(msg.arg1);

                break;
            }
        }
    };
}

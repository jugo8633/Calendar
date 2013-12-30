//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;

import android.content.Context;

import android.graphics.drawable.ColorDrawable;

import android.os.Handler;
import android.os.Message;
import android.os.Process;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.HpVerticalGallery;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import frame.event.EventHandler;
import frame.event.EventMessage;

public class CalendarMonthGallery {
    private static final String  TAG                    = "CalendarMonthGallery";
    private final int            ANIMATION_DURATION     = 840;
    public final int             DATE_TYPE              = 2;
    public final int             DAY_TYPE               = 1;
    public final int             MONTH_TYPE             = 0;
    public final int             VIEW_GALLERY           = 0;
    public final int             VIEW_LISTVIEW          = 1;
    private CalendarApplication  application            = null;
    private CalendarMonth        calendarMonth          = null;
    private ArrayList<DayOfWeek> dayOfWeek              = null;
    private Handler              mHandler               = null;
    private final float          mUnSelAlpha            = 0.5f;
    private View                 m_converView           = null;
    private int                  mnMonthViewResId       = R.layout.calendar_month;
    private final int            m_nLayoutListViewResId = R.id.GalleryMonthV;    // R.id.lvMonth;
    private final int            m_nLayoutGalleryResId  = R.id.GalleryMonth;
    private int                  mnPadingSpace          = 25;
    private int                  mnSelectedDay          = -1;
    private int                  mnSelectedMonth        = -1;
    private int                  mnSelectedYear         = -1;
    private int                  mnTouchId              = -1;
    private int                  mnType                 = 0;
    private int                  mnViewType             = -1;
    private EventHandler         monthEvent             = null;
    private Gallery              monthGallery           = null;
    private HpVerticalGallery    monthGalleryV          = null;
    private int                  nPreSelId              = 0;
    private Thread               thdUpdateEvent         = null;
    private Activity             theAct                 = null;
    private Dialog               theDialog              = null;
    private Handler              monthHandler           = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case EventHandler.EVENT_HANDLE_ON_TOUCH :
                switch (msg.arg1) {
                case EventHandler.EVENT_HANDLE_ON_TOUCH_DOWN :
                    if ((msg.arg2 == R.id.rlCalendarMonthDateTitle) || (msg.arg2 == R.id.rlMonthWeekDayTitle)) {
                        if (DATE_TYPE == mnType) {
                            mnTouchId = -1;
                        } else {
                            application.setTouchId(-1);
                        }
                    } else {
                        if (DATE_TYPE == mnType) {
                            mnTouchId = msg.arg2;
                        } else {
                            application.setTouchId(msg.arg2);
                        }
                    }

                    break;
                case EventHandler.EVENT_HANDLE_ON_TOUCH_UP :
                    break;
                }

                break;
            case 0xBB :                                                          // update event mask
                TextView tvTmp = null;

                switch (mnViewType) {
                case VIEW_GALLERY :
                    if (null != monthGallery) {
                        tvTmp = (TextView) monthGallery.findViewById(msg.arg1);

                        if (null != tvTmp) {
                            tvTmp.setVisibility(View.VISIBLE);
                            monthGallery.invalidate();
                        }
                    }

                    break;
                case VIEW_LISTVIEW :
                    if (null != monthGalleryV) {
                        tvTmp = (TextView) monthGalleryV.findViewById(msg.arg1);

                        if (null != tvTmp) {
                            tvTmp.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                }

                break;
            }
        }
    };
    OnItemClickListener clickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View currView, int arg2, long arg3) {
            boolean bOptionBlock = application.getOptionBlock();

            if (bOptionBlock) {
                return;
            }

            int nTouchId = -1;

            if (DATE_TYPE == mnType) {
                if (0 > mnTouchId) {
                    return;
                }

                nTouchId = mnTouchId;
            } else {
                if (0 > application.getTouchId()) {
                    return;
                }

                nTouchId = application.getTouchId();
            }

            int            nId    = 0;
            int            nYear  = 0;
            int            nMonth = 0;
            int            nDay   = 0;
            RelativeLayout rltmp  = (RelativeLayout) currView.findViewById(nTouchId);

            if (null != rltmp) {
                TextView tvDay = (TextView) rltmp.findViewById(R.id.tvMonthDay);

                if (null != tvDay) {
                    String szDay = tvDay.getText().toString();

                    if ((null == szDay) || (szDay.length() <= 0)) {
                        return;
                    }

                    nDay = Integer.valueOf(szDay);

                    if (DATE_TYPE == mnType) {
                        mnSelectedDay = nDay;
                    } else {
                        application.setSelDay(nDay);
                    }
                } else {
                    rltmp = null;

                    return;
                }
            } else {}

            TextView tvTmp = (TextView) currView.findViewById(R.id.tvMonthYear);

            if (null != tvTmp) {
                String szYear;

                szYear = tvTmp.getText().toString();
                nYear  = Integer.valueOf(szYear);

                if (DATE_TYPE == mnType) {
                    mnSelectedYear = nYear;
                } else {
                    application.setSelYear(nYear);
                }
            } else {
                return;
            }

            tvTmp = (TextView) currView.findViewById(R.id.tvMonthMonth);

            if (null != tvTmp) {
                String szMonth;

                szMonth = tvTmp.getText().toString();
                nMonth  = calendarMonth.getMonthInt(szMonth);

                if (DATE_TYPE == mnType) {
                    mnSelectedMonth = nMonth;
                } else {
                    application.setSelMonth(nMonth);
                }
            } else {
                return;
            }

            RelativeLayout rlPreSelected = null;

            for (int i = 0; i < arg0.getCount(); i++) {
                if (null == arg0.getChildAt(i)) {
                    continue;
                }

                rlPreSelected = (RelativeLayout) arg0.getChildAt(i).findViewById(nPreSelId);

                TextView tvTmpSelected = (TextView) arg0.getChildAt(i).findViewById(R.id.tvMonthYear);

                if (null != tvTmpSelected) {
                    String szYear;

                    szYear = tvTmpSelected.getText().toString();
                    nYear  = Integer.valueOf(szYear);
                }

                tvTmpSelected = (TextView) arg0.getChildAt(i).findViewById(R.id.tvMonthMonth);

                if (null != tvTmpSelected) {
                    String szMonth;

                    szMonth = tvTmpSelected.getText().toString();
                    nMonth  = calendarMonth.getMonthInt(szMonth);
                }

                if (null != rlPreSelected) {
                    TextView tvDay = (TextView) rlPreSelected.findViewById(R.id.tvMonthDay);

                    if (null != tvDay) {
                        String szDaySelected = tvDay.getText().toString();

                        if ((null != szDaySelected) && (szDaySelected.length() > 0)) {
                            nDay = Integer.valueOf(tvDay.getText().toString());
                        }
                    }

                    nId = rlPreSelected.getId();

                    if ((nYear == application.getYearOfToday()) && (nMonth == application.getMonthOfToday())
                            && (nDay == application.getDayOfToday())) {
                        rlPreSelected.setBackgroundResource(R.drawable.month_today);
                    } else if (isWeek(nId)) {
                        rlPreSelected.setBackgroundColor(0xFFE6E6E6);
                    } else {
                        rlPreSelected.setBackgroundColor(0xFFFFFFFF);
                    }
                }
            }

            if (null != rltmp) {
                nPreSelId = rltmp.getId();

                if (DATE_TYPE == mnType) {
                    nYear  = mnSelectedYear;
                    nMonth = mnSelectedMonth;
                    nDay   = mnSelectedDay;
                } else {
                    nYear  = application.getSelYear();
                    nMonth = application.getSelMonth();
                    nDay   = application.getSelDay();
                }

                if ((nYear == application.getYearOfToday()) && (nMonth == application.getMonthOfToday())
                        && (nDay == application.getDayOfToday())) {
                    rltmp.setBackgroundResource(R.drawable.month_today_sel);
                } else if (isWeek(nPreSelId)) {
                    rltmp.setBackgroundResource(R.drawable.c_red_week);
                } else {
                    rltmp.setBackgroundResource(R.drawable.c_red_normal);
                }

                String[] arrSelDate = new String[3];

                arrSelDate[0] = String.valueOf(nYear);
                arrSelDate[1] = String.valueOf(nMonth);
                arrSelDate[2] = String.valueOf(nDay);

                Message msg = new Message();

                msg.what = EventMessage.WND_MSG;                // message type
                msg.arg1 = EventMessage.WND_DATE_SELECT;        // message

                if (DATE_TYPE == mnType) {
                    msg.arg2 = EventMessage.RUN_NEWEVENTWND;    // window id
                    msg.obj  = arrSelDate;
                } else {
                    application.setCurrYear(nYear);
                    application.setCurrMonth(nMonth);
                }

                mHandler.sendMessage(msg);
            }

            if (DATE_TYPE == mnType) {
                mnTouchId = -1;
            } else {
                application.setTouchId(-1);
            }
        }
    };

    public CalendarMonthGallery(Activity act, Handler handler) {
        theAct      = act;
        application = (CalendarApplication) act.getApplication();
        mHandler    = handler;
    }

    public CalendarMonthGallery(Dialog dialog, Handler handler, Application theApp) {
        theDialog   = dialog;
        application = (CalendarApplication) theApp;
        mHandler    = handler;

        if (null != theDialog) {
            ColorDrawable drawable = new ColorDrawable(0xFF000000);

            theDialog.getWindow().setBackgroundDrawable(drawable);
        }
    }

    public boolean initMonthGallery(int nType, int nViewType) {
        mnType     = nType;
        mnViewType = nViewType;

        switch (nType) {
        case MONTH_TYPE :
            mnMonthViewResId = R.layout.calendar_month;
            calendarMonth    = application.getCalendarMonth();

            switch (nViewType) {
            case VIEW_GALLERY :
                mnPadingSpace = 25;

                break;
            case VIEW_LISTVIEW :
                mnPadingSpace = 12;

                break;
            }

            break;
        case DAY_TYPE :
            mnMonthViewResId = R.layout.calendar_month_small;
            calendarMonth    = application.getCalendarMonth();

            switch (nViewType) {
            case VIEW_GALLERY :
                mnPadingSpace = 20;

                break;
            case VIEW_LISTVIEW :
                mnPadingSpace = 5;

                break;
            }

            break;
        case DATE_TYPE :
            calendarMonth    = new CalendarMonth();
            mnMonthViewResId = R.layout.calendar_month;

            switch (nViewType) {
            case VIEW_GALLERY :
                mnPadingSpace = 25;

                break;
            case VIEW_LISTVIEW :
                mnPadingSpace = 12;

                break;
            }

            break;
        }

        boolean bReturn = false;

        switch (nViewType) {
        case VIEW_GALLERY :
            bReturn       = initGallery();
            monthGalleryV = null;

            break;
        case VIEW_LISTVIEW :
            bReturn      = initListView();
            monthGallery = null;

            break;
        }

        return bReturn;
    }

    private boolean initGallery() {
        if (null != theAct) {
            monthGallery = (Gallery) theAct.findViewById(m_nLayoutGalleryResId);
        } else if (null != theDialog) {
            monthGallery = (Gallery) theDialog.findViewById(m_nLayoutGalleryResId);
        }

        if (null != monthGallery) {
            monthGallery.setVisibility(View.VISIBLE);
            monthEvent = new EventHandler(monthHandler);
            monthEvent.setEventConSume(false);
            dayOfWeek = new ArrayList<DayOfWeek>();
            monthGallery.destroyDrawingCache();
            monthGallery.setFocusable(true);
            monthGallery.setAnimationCacheEnabled(false);
            monthGallery.setAlwaysDrawnWithCacheEnabled(false);
            monthGallery.setDrawingCacheEnabled(false);
            monthGallery.setUnselectedAlpha(mUnSelAlpha);
            monthGallery.setAnimationDuration(ANIMATION_DURATION);

            if (null != theAct) {
                monthGallery.setAdapter(new calendarAdapter(theAct));
            } else if (null != theDialog) {
                monthGallery.setAdapter(new calendarAdapter(theDialog.getContext()));
            }

            int nCurrent = (application.getCalendarCount() / 2) + 1;

            monthGallery.setSelection(nCurrent);
            monthGallery.setOnItemClickListener(clickListener);

            return true;
        }

        return false;
    }

    private boolean initListView() {
        if (null != theAct) {
            monthGalleryV = (HpVerticalGallery) theAct.findViewById(m_nLayoutListViewResId);
        } else if (null != theDialog) {
            monthGalleryV = (HpVerticalGallery) theDialog.findViewById(m_nLayoutListViewResId);
        }

        if (null != monthGalleryV) {
            monthGalleryV.setVisibility(View.VISIBLE);
            monthEvent = new EventHandler(monthHandler);
            monthEvent.setEventConSume(false);
            dayOfWeek = new ArrayList<DayOfWeek>();
            monthGalleryV.destroyDrawingCache();
            monthGalleryV.setFocusable(true);
            monthGalleryV.setAnimationCacheEnabled(false);
            monthGalleryV.setAlwaysDrawnWithCacheEnabled(false);
            monthGalleryV.setDrawingCacheEnabled(false);
            monthGalleryV.setUnselectedAlpha(mUnSelAlpha);
            monthGalleryV.setAnimationDuration(ANIMATION_DURATION);
            monthGalleryV.setGravity(Gravity.CENTER_HORIZONTAL);

            if (null != theAct) {
                monthGalleryV.setAdapter(new calendarAdapter(theAct));
            } else if (null != theDialog) {
                monthGalleryV.setAdapter(new calendarAdapter(theDialog.getContext()));
            }

            int nCurrent = (application.getCalendarCount() / 2) + 1;

            monthGalleryV.setSelection(nCurrent);
            monthGalleryV.setOnItemClickListener(clickListener);

            return true;
        }

        return false;
    }

    public void refleshMonth() {
        wndHandler.sendEmptyMessage(0);
    }

    private void refleshMonthHandler() {
        int nCurrent = 0;

        if (null != monthGallery) {
            nCurrent = monthGallery.getSelectedItemPosition();

            if (null != theAct) {
                monthGallery.setAdapter(new calendarAdapter(theAct));
            } else if (null != theDialog) {
                monthGallery.setAdapter(new calendarAdapter(theDialog.getContext()));
            }

            monthGallery.setSelection(nCurrent);
        }

        if (null != monthGalleryV) {
            nCurrent = monthGalleryV.getSelectedItemPosition();

            if (null != theAct) {
                monthGalleryV.setAdapter(new calendarAdapter(theAct));
            } else if (null != theDialog) {
                monthGalleryV.setAdapter(new calendarAdapter(theDialog.getContext()));
            }

            monthGalleryV.setSelection(nCurrent);
        }
    }

    public void close() {
        if (DATE_TYPE == mnType) {
            calendarMonth = null;

            return;
        }

        if (null != thdUpdateEvent) {
            thdUpdateEvent = null;
        }

        if (null != monthGallery) {
            View vTmp = monthGallery.getSelectedView();

            if (null != vTmp) {
                TextView tvTmp = (TextView) vTmp.findViewById(R.id.tvMonthYear);

                if (null != tvTmp) {
                    if ((null != tvTmp.getText().toString()) && (tvTmp.getText().toString().length() > 0)) {
                        int nCurrYear = Integer.parseInt(tvTmp.getText().toString());

                        application.setCurrYear(nCurrYear);
                    }
                }

                tvTmp = (TextView) vTmp.findViewById(R.id.tvMonthMonth);

                if (null != tvTmp) {
                    if ((null != tvTmp.getText().toString()) && (tvTmp.getText().toString().length() > 0)) {
                        int nCurrMonth = calendarMonth.getMonthInt(tvTmp.getText().toString());

                        application.setCurrMonth(nCurrMonth);
                    }
                }
            }

            monthGallery.destroyDrawingCache();
            monthGallery.cancelLongPress();
            monthGallery.clearAnimation();
            monthGallery.clearFocus();
            monthGallery.setAdapter(null);
            monthGallery = null;
        }

        if (null != monthGalleryV) {
            View vTmp = monthGalleryV.getSelectedView();

            if (null != vTmp) {
                TextView tvTmp = (TextView) vTmp.findViewById(R.id.tvMonthYear);

                if (null != tvTmp) {
                    if ((null != tvTmp.getText().toString()) && (tvTmp.getText().toString().length() > 0)) {
                        int nCurrYear = Integer.parseInt(tvTmp.getText().toString());

                        application.setCurrYear(nCurrYear);
                    }
                }

                tvTmp = (TextView) vTmp.findViewById(R.id.tvMonthMonth);

                if (null != tvTmp) {
                    if ((null != tvTmp.getText().toString()) && (tvTmp.getText().toString().length() > 0)) {
                        int nCurrMonth = calendarMonth.getMonthInt(tvTmp.getText().toString());

                        application.setCurrMonth(nCurrMonth);
                    }
                }
            }

            monthGalleryV.destroyDrawingCache();
            monthGalleryV.cancelLongPress();
            monthGalleryV.clearAnimation();
            monthGalleryV.clearFocus();
            monthGalleryV.setAdapter(null);
            monthGalleryV = null;
        }

        application.setCurrDate();

        if (null != dayOfWeek) {
            dayOfWeek.clear();
            dayOfWeek = null;
        }
    }

    private boolean isWeek(int nId) {
        if ((nId == R.id.rlMonthDay00) || (nId == R.id.rlMonthDay10) || (nId == R.id.rlMonthDay20)
                || (nId == R.id.rlMonthDay30) || (nId == R.id.rlMonthDay40) || (nId == R.id.rlMonthDay50)
                || (nId == R.id.rlMonthDay06) || (nId == R.id.rlMonthDay16) || (nId == R.id.rlMonthDay26)
                || (nId == R.id.rlMonthDay36) || (nId == R.id.rlMonthDay46) || (nId == R.id.rlMonthDay56)) {
            return true;
        }

        return false;
    }

    public void updateMonthEvent() {
        if (DATE_TYPE != mnType) {
            startUpdateEventThd();
        }
    }

    private synchronized void startUpdateEventThd() {
        if (null != thdUpdateEvent) {
            thdUpdateEvent = null;
        }

        thdUpdateEvent = new Thread() {
            @Override
            public void run() {
                if (null != application.getEventData()) {
                    int    nYear  = 0;
                    int    nMonth = 0;
                    int    nDay   = 0;
                    int    nId    = 0;
                    String szId   = null;

                    for (int i = 0; i < application.getEventData().getEventCount(); i++) {
                        nYear  = application.getEventData().getEventYear(i);
                        nMonth = application.getEventData().getEventMonth(i);
                        nDay   = application.getEventData().getEventDay(i);
                        szId   = String.format("%d%d%d", nYear, nMonth, nDay);
                        nId    = Integer.valueOf(szId);

                        Message msg = new Message();

                        msg.what = 0xBB;
                        msg.arg1 = nId;
                        monthHandler.sendMessage(msg);

                        try {
                            sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                super.run();
            }
        };
        thdUpdateEvent.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        thdUpdateEvent.start();
    }

    public void goToDay() {
        if (null != thdUpdateEvent) {
            thdUpdateEvent = null;
        }

        calendarMonth.reset();

        int nCurrent = 0;

        switch (mnViewType) {
        case VIEW_GALLERY :
            monthGallery.setAdapter(new calendarAdapter(theAct));
            nCurrent = (application.getCalendarCount() / 2) + 1;
            monthGallery.setSelection(nCurrent);

            break;
        case VIEW_LISTVIEW :
            monthGalleryV.setAdapter(new calendarAdapter(theAct));
            nCurrent = (application.getCalendarCount() / 2) + 1;
            monthGalleryV.setSelection(nCurrent);

            break;
        }
    }

    private RelativeLayout initDay(int nResId) {
        if (null == m_converView) {
            return null;
        }

        RelativeLayout rlTmp = null;

        rlTmp = (RelativeLayout) m_converView.findViewById(nResId);

        if (null == rlTmp) {
            return null;
        }

        monthEvent.registerViewOnTouch(rlTmp);

        return rlTmp;
    }

    private void initMonthDays(View convertView) {
        if (null == dayOfWeek) {
            return;
        }

        m_converView = convertView;
        dayOfWeek.clear();

        RelativeLayout rlTmp = null;

        initDay(R.id.rlCalendarMonthDateTitle);
        initDay(R.id.rlMonthWeekDayTitle);
        rlTmp = initDay(R.id.rlMonthDay00);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay01);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay02);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay03);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay04);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay05);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay06);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay10);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay11);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay12);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay13);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay14);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay15);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay16);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay20);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay21);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay22);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay23);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay24);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay25);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay26);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay30);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay31);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay32);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay33);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay34);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay35);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay36);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay40);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay41);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay42);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay43);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay44);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay45);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay46);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay50);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay51);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay52);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay53);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay54);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay55);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
        rlTmp = initDay(R.id.rlMonthDay56);
        dayOfWeek.add(new DayOfWeek(rlTmp, (TextView) rlTmp.findViewById(R.id.tvMonthEvent)));
    }

    public class DayOfWeek {
        public RelativeLayout m_rlDay;
        public TextView       m_tvEvent;

        public DayOfWeek(RelativeLayout rlDay, TextView tvEvent) {
            m_rlDay   = rlDay;
            m_tvEvent = tvEvent;
        }
    }


    ;
    public class calendarAdapter extends BaseAdapter {
        private Context        m_context;
        private LayoutInflater m_inflater;

        public calendarAdapter(Context c) {
            m_context  = c;
            m_inflater = LayoutInflater.from(this.m_context);
        }

        public int getCount() {
            return application.getCalendarCount();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            switch (mnViewType) {
            case VIEW_GALLERY :
            case VIEW_LISTVIEW :
                int  nCurrYear  = -1;
                int  nCurrMonth = -1;
                View vtest      = null;

                if (mnViewType == VIEW_GALLERY) {
                    vtest = monthGallery.getSelectedView();
                } else if (mnViewType == VIEW_LISTVIEW) {
                    vtest = monthGalleryV.getSelectedView();
                }

                if (null != vtest) {
                    TextView tvTTT = (TextView) vtest.findViewById(R.id.tvMonthYear);

                    application.setViewCenterYear(Integer.parseInt(tvTTT.getText().toString()));
                    tvTTT = (TextView) vtest.findViewById(R.id.tvMonthMonth);
                    application.setViewCenterMonth(
                        application.getCalendarMonth().getMonthInt(tvTTT.getText().toString()));
                }

                if ((1 == position) || (position == (application.getCalendarCount() - 1))) {
                    View vTmp = null;

                    if (mnViewType == VIEW_GALLERY) {
                        vTmp = monthGallery.getSelectedView();
                    } else if (mnViewType == VIEW_LISTVIEW) {
                        vTmp = monthGalleryV.getSelectedView();
                    }

                    if (null != vTmp) {
                        TextView tvTmp = (TextView) vTmp.findViewById(R.id.tvMonthYear);

                        if (null != tvTmp) {
                            nCurrYear = Integer.parseInt(tvTmp.getText().toString());
                        }

                        tvTmp = (TextView) vTmp.findViewById(R.id.tvMonthMonth);

                        if (null != tvTmp) {
                            nCurrMonth = application.getCalendarMonth().getMonthInt(tvTmp.getText().toString());
                        }
                    }

                    if (DATE_TYPE == mnType) {
                        calendarMonth.setDate(nCurrYear, nCurrMonth);
                    } else {
                        application.setCurrYear(nCurrYear);
                        application.setCurrMonth(nCurrMonth);
                        application.setCurrDate();
                    }

                    int nCurrent = (application.getCalendarCount() / 2) + 1;

                    if (mnViewType == VIEW_GALLERY) {
                        monthGallery.setSelection(nCurrent);
                    } else if (mnViewType == VIEW_LISTVIEW) {
                        monthGalleryV.setSelection(nCurrent);
                    }

                    vTmp = null;
                }

                vtest = null;

                break;
            }

            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (null != convertView) {
                convertView = null;
            }

            convertView = m_inflater.inflate(mnMonthViewResId, null);

            RelativeLayout rlMain = (RelativeLayout) convertView.findViewById(R.id.rlCalendarMonthMain);

            if (null != rlMain) {
                switch (mnViewType) {
                case VIEW_GALLERY :
                    rlMain.setPadding(mnPadingSpace, 0, mnPadingSpace, 0);

                    break;
                case VIEW_LISTVIEW :
                    rlMain.setPadding(0, mnPadingSpace, 0, mnPadingSpace);
                    rlMain.setBackgroundColor(0xFF000000);

                    break;
                }
            }

            initMonthDays(convertView);
            setMonthDay(position, convertView);

            return convertView;
        }

        private void setMonthDay(int position, View convertView) {
            int nMiddle = 0;

            if (null == calendarMonth) {
                return;
            }

            if (null == dayOfWeek) {
                return;
            }

            if (dayOfWeek.size() != 42) {
                return;
            }

            nMiddle = (application.getCalendarCount() / 2) + 1;

            int nDay = 1;

            if (position != nMiddle) {
                int nValue;

                nValue = position - nMiddle;
                calendarMonth.addMonth(nValue);
            }

            int nSelYear  = -1;
            int nSelMonth = -1;
            int nSelDay   = -1;

            if (DATE_TYPE == mnType) {
                nSelYear  = mnSelectedYear;
                nSelMonth = mnSelectedMonth;
                nSelDay   = mnSelectedDay;
            } else {
                nSelYear  = application.getSelYear();
                nSelMonth = application.getSelMonth();
                nSelDay   = application.getSelDay();
            }

            boolean bSelMonth = false;

            if ((0 < nSelYear) || (0 < nSelMonth)) {
                if ((nSelYear == calendarMonth.getYear()) && (nSelMonth == calendarMonth.getMonth())) {
                    bSelMonth = true;
                }
            }

            for (int i = calendarMonth.getFirstDayIndex(), j = 0; j < calendarMonth.getMonthDayScore();
                    i++, j++, nDay++) {
                if (null == dayOfWeek.get(i).m_tvEvent) {
                    return;
                }

                String szId = String.format("%d%d%d", calendarMonth.getYear(), calendarMonth.getMonth(), nDay);

                dayOfWeek.get(i).m_tvEvent.setId(Integer.valueOf(szId));

                TextView tvDay = (TextView) dayOfWeek.get(i).m_rlDay.findViewById(R.id.tvMonthDay);

                if (null != tvDay) {
                    tvDay.setText(String.valueOf(nDay));

                    if (calendarMonth.getYear() == application.getYearOfToday()) {
                        if (calendarMonth.getMonth() == application.getMonthOfToday()) {
                            if (nDay == application.getDayOfToday()) {
                                dayOfWeek.get(i).m_rlDay.setBackgroundResource(R.drawable.month_today);
                            }
                        }
                    }
                }

                if ((null != dayOfWeek.get(i).m_rlDay) && bSelMonth && (nSelDay == nDay)) {
                    nPreSelId = dayOfWeek.get(i).m_rlDay.getId();

                    int nYear        = -1;
                    int nMonth       = -1;
                    int nDaySelected = -1;

                    if (DATE_TYPE == mnType) {
                        nYear        = mnSelectedYear;
                        nMonth       = mnSelectedMonth;
                        nDaySelected = mnSelectedDay;
                    } else {
                        nYear        = application.getSelYear();
                        nMonth       = application.getSelMonth();
                        nDaySelected = application.getSelDay();
                    }

                    if ((nYear == application.getYearOfToday()) && (nMonth == application.getMonthOfToday())
                            && (nDaySelected == application.getDayOfToday())) {
                        dayOfWeek.get(i).m_rlDay.setBackgroundResource(R.drawable.month_today_sel);
                    } else if (isWeek(nPreSelId)) {
                        dayOfWeek.get(i).m_rlDay.setBackgroundResource(R.drawable.c_red_week);
                    } else {
                        dayOfWeek.get(i).m_rlDay.setBackgroundResource(R.drawable.c_red_normal);
                    }
                }

                if (DATE_TYPE != mnType) {
                    if (application.isEventDate(application.getCalendarMonth().getYear(),
                                                application.getCalendarMonth().getMonth(), nDay)) {
                        dayOfWeek.get(i).m_tvEvent.setVisibility(View.VISIBLE);
                    } else {
                        dayOfWeek.get(i).m_tvEvent.setVisibility(View.INVISIBLE);
                    }
                }
            }

            TextView tvTmp = (TextView) convertView.findViewById(R.id.tvMonthYear);

            if (null != tvTmp) {
                String szYear;

                szYear = String.valueOf(calendarMonth.getYear());
                tvTmp.setText(szYear);
            }

            tvTmp = (TextView) convertView.findViewById(R.id.tvMonthMonth);

            if (null != tvTmp) {
                String szMonth;

                szMonth = calendarMonth.getMonthName(calendarMonth.getMonth());
                tvTmp.setText(szMonth);
            }

            if (position != nMiddle) {
                int nValue;

                nValue = nMiddle - position;
                calendarMonth.addMonth(nValue);
            }
        }
    }


    public void notifyMonthView() {
        if (null != monthGallery) {
            monthGallery.notify();
        }

        if (null != monthGalleryV) {
            monthGalleryV.notify();
        }
    }

    Handler wndHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);

            switch (msg.what) {
            case 0 :
                refleshMonthHandler();

                break;
            }
        }
    };
}

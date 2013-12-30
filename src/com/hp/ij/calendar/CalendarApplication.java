//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.net.MalformedURLException;

import java.sql.Date;

import java.util.Calendar;

import android.app.Application;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.hp.ij.calendar.controler.CalendarWindowControl;
import com.hp.ij.calendar.data.CalendarData;
import com.hp.ij.calendar.handler.CalendarBroadcaster;
import com.hp.ij.calendar.handler.CalendarEventHandler;
import com.hp.ij.calendar.handler.CalendarStateMonitor;
import com.hp.ij.calendar.handler.CalendarThreads;
import com.hp.ij.calendar.handler.CalendarWndEventHandler;
import com.hp.ij.common.ccphttpclient.CCPHttpClientHelper;
import com.hp.ij.common.service.client.IntentCommand;

import frame.event.EventMessage;

public class CalendarApplication extends Application {
    private static final String     TAG                     = "CalendarApplication";
    public final int                ORIENTATION_LAND        = 2;
    public final int                ORIENTATION_PORT        = 1;
    public CalendarAPIService       CService                = new CalendarAPIService();
    private CalendarEventData       eventData               = new CalendarEventData();
    private CalendarApp             mCalendarApp            = null;
    private final int               mnCalendarCount         = 201;
    private int                     mnCheckDlgResId         = -1;
    private int                     mnCurrMonth             = -1;
    private int                     mnCurrYear              = -1;
    private int                     mnDateShowFromResId     = -1;
    private int                     mnDayOfToday            = 0;
    private int                     mnMonthOfToday          = 0;
    private int                     mnOrientation           = -1;
    private int                     mnPrintRunMode          = 0;
    private int                     mnRecurrenceSelected    = 0;
    private int                     mnReminderSelected      = 0;
    private int                     mnRunWnd                = EventMessage.RUN_LOADWND;
    private int                     mnSelDay                = -1;
    private int                     mnSelMonth              = -1;
    private int                     mnSelYear               = -1;
    private int                     mnSelected              = -1;
    private int                     mnSyncWndType           = -1;
    private int                     mnTouchId               = -1;
    private int                     mnViewCenterMonth       = 0;
    private int                     mnViewCenterYear        = 0;
    private int                     mnYearOfToday           = 0;
    private String                  mszAlarm                = null;
    private String                  mszCheckCaption         = null;
    private String                  mszEventId              = null;
    private boolean                 mrunPrintView           = false;
    private boolean                 mbSavePassword          = false;
    private boolean                 mbReceiveEventFree      = false;
    private boolean                 mbOptionBlock           = false;
    private boolean                 mbMonthMenuShow         = false;
    private boolean                 mbGoHome                = false;
    private boolean                 mbDayMenuShow           = false;
    private boolean                 mbAllDayChk             = false;
    private boolean                 mbReflashMonthEvent     = true;
    private CalendarWidgetBroadcast calendarWidgetBroadcast = new CalendarWidgetBroadcast();
    private CalendarThreads         calendarThreadsLow      = new CalendarThreads(Process.THREAD_PRIORITY_BACKGROUND);
    private CalendarThreads         calendarThreadsHigh     = new CalendarThreads(Process.THREAD_PRIORITY_AUDIO);
    private CalendarMonth           calendarMonth           = new CalendarMonth();
    private CalendarData            calendarData            = new CalendarData();
    private Handler                 mwndHandler             = null;
    private CalendarWindowControl   calendarWindowControl   = null;
    private CalendarEventHandler    calendarEventHandler    = null;
    private CalendarWndEventHandler calendarWndEventHandler = null;
    private CalendarStateMonitor    calendarStateMonitor    = null;
    private CalendarBroadcaster     calendarBroadcaster     = null;

    public CalendarApplication() {
        super();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mnOrientation = newConfig.orientation;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * set process priority
         * you can set Process.THREAD_PRIORITY_FOREGROUND
         */
        Process.setThreadPriority(Process.myPid(), Process.THREAD_PRIORITY_AUDIO);
        CService.initCalendarAPIService(getApplicationContext(), this);
        getToDay();
        calendarThreadsLow.startHandler(this);
        calendarThreadsHigh.startHandler(this);

        /**
         * calendarBroadcaster for receiving widget request
         */
        calendarBroadcaster = new CalendarBroadcaster(this);
        calendarBroadcaster.registerReceiver();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        if (null != CService) {
            CService.releaseService();
            CService = null;
        }

        calendarWndEventHandler = null;
        calendarData            = null;
        calendarThreadsLow.closeHandler();
        calendarThreadsLow = null;
        calendarThreadsHigh.closeHandler();
        calendarThreadsHigh = null;
        calendarEventHandler.releaseEvent();
        calendarEventHandler = null;
        calendarWindowControl.releaseWnd();
        calendarWindowControl = null;
        calendarStateMonitor.closeHandler();
        calendarStateMonitor = null;
        calendarBroadcaster.unRegisterReceiver();
        calendarBroadcaster = null;
    }

    public void initEventHandler() {
        if (CService != null) {
            CService.initEventHandler(mwndHandler);
        }
    }

    public int getOrientation() {
        return mnOrientation;
    }

    public void setOrientation(int nOri) {
        mnOrientation = nOri;
    }

    public void setCurrentActivity(CalendarApp theActivity) {
        mCalendarApp            = theActivity;
        calendarWndEventHandler = new CalendarWndEventHandler(theActivity);

        Handler handleWnd = calendarWndEventHandler.getHandler();

        calendarWindowControl = new CalendarWindowControl(theActivity, handleWnd);
        calendarEventHandler  = new CalendarEventHandler(theActivity);
        mwndHandler           = calendarWndEventHandler.getHandler();

        /**
         * check next day then updating widget and month view today mask.
         */
        calendarStateMonitor = new CalendarStateMonitor(this);
        calendarStateMonitor.initHandler(calendarThreadsLow.getHandler());
        calendarStateMonitor.startNextDayCheck();
    }

    public Handler getWndHandler() {
        return mwndHandler;
    }

    public CalendarApp getActivity() {
        return mCalendarApp;
    }

    /**
     * event data
     *
     */
    public void initEventData() {
        if (null != eventData) {
            if (0 < eventData.getEventCount()) {
                eventData.clearEvent();
            }
        }
    }

    public void setMultiEventData(int nFYY, int nFMM, int nFDD, int n2YY, int n2MM, int n2DD, int nDayOfWeek) {
        if (null == eventData) {
            return;
        }

        Calendar Begin_cal = Calendar.getInstance(),
                 End_cal   = Calendar.getInstance();

        Begin_cal.set(nFYY, nFMM, nFDD);    // Calendar Mointh is zero based!
        End_cal.set(n2YY, n2MM, n2DD);

        if (End_cal.before(Begin_cal)) {
            return;
        }

        for (int Year = nFYY, Month = nFMM, Day = nFDD; Begin_cal.before(End_cal) | Begin_cal.equals(End_cal);
                Begin_cal.add(Calendar.DAY_OF_YEAR, 1), Year = Begin_cal.get(Calendar.YEAR),
                Month = Begin_cal.get(Calendar.MONTH), Day = Begin_cal.get(Calendar.DAY_OF_MONTH)) {
            eventData.addEventData(Year, Month + 1, Day, nDayOfWeek);
        }
    }

    public void setEventData(int nYear, int nMonth, int nDay, int nDayOfWeek) {
        if (null == eventData) {
            return;
        }

        eventData.addEventData(nYear, nMonth, nDay, nDayOfWeek);
    }

    public int setEventDetailData(String szId, String szCalendar, String szEvent, String szTime, String szWhere,
                                  String szMessage, Calendar startDate, Calendar endDate, String szReminder,
                                  int nRecurrence, boolean bAllDay, boolean bEditAble) {
        if (null == eventData) {
            return 0;
        }

        eventData.addEventDetail(szId, szCalendar, szEvent, szTime, szWhere, szMessage, startDate, endDate, szReminder,
                                 nRecurrence, bAllDay, bEditAble);

        return eventData.getEventDetailCount();
    }

    public void clearEventDetailData() {
        if (null == eventData) {
            return;
        }

        eventData.clearEventDetail();
    }

    public boolean isEventDate(int nYear, int nMonth, int nDay) {
        if (null == eventData) {
            return false;
        }

        return eventData.isEventDate(nYear, nMonth, nDay);
    }

    public CalendarEventData getEventData() {
        return eventData;
    }

    public void setRunWnd(int nRunWnd) {
        mnRunWnd = nRunWnd;
    }

    public int getRunWnd() {
        return mnRunWnd;
    }

    public void setAlarm(String szAlarm) {
        mszAlarm = szAlarm;
    }

    public String getAlarm() {
        return mszAlarm;
    }

    public CalendarMonth getCalendarMonth() {
        if (null == calendarMonth) {
            calendarMonth = new CalendarMonth();
        }

        return calendarMonth;
    }

    public void getToDay() {
        getCalendarMonth().reset();
        calendarMonth.getToday();
        mnYearOfToday  = calendarMonth.m_nYearOfToday;
        mnMonthOfToday = calendarMonth.m_nMonthOfToday;
        mnDayOfToday   = calendarMonth.m_nDayOfToday;
    }

    public int getYearOfToday() {
        return mnYearOfToday;
    }

    public int getMonthOfToday() {
        return mnMonthOfToday;
    }

    public int getDayOfToday() {
        return mnDayOfToday;
    }

    public void setCurrYear(int nYear) {
        mnCurrYear = nYear;
    }

    public int getCurrYear() {
        if (0 >= mnCurrYear) {
            mnCurrYear = mnYearOfToday;
        }

        return mnCurrYear;
    }

    public void setCurrMonth(int nMonth) {
        mnCurrMonth = nMonth;
    }

    public int getCurrMonth() {
        if (0 >= mnCurrMonth) {
            mnCurrMonth = mnMonthOfToday;
        }

        return mnCurrMonth;
    }

    public void setViewCenterYear(int nYear) {
        mnViewCenterYear = nYear;
    }

    public int getViewCenterYear() {
        return mnViewCenterYear;
    }

    public void setViewCenterMonth(int nMonth) {
        mnViewCenterMonth = nMonth;
    }

    public int getViewCenterMonth() {
        return mnViewCenterMonth;
    }

    public void setTouchId(int nId) {
        mnTouchId = nId;
    }

    public int getTouchId() {
        return mnTouchId;
    }

    public void setSelected(int nSelected) {
        mnSelected = nSelected;
    }

    public int getSelected() {
        return mnSelected;
    }

    public int getCalendarCount() {
        return mnCalendarCount;
    }

    public void setCurrDate() {
        getCalendarMonth().setDate(getCurrYear(), getCurrMonth());
    }

    public void setSelYear(int nYear) {
        mnSelYear = nYear;
    }

    public int getSelYear() {
        return mnSelYear;
    }

    public void setSelMonth(int nMonth) {
        mnSelMonth = nMonth;
    }

    public int getSelMonth() {
        return mnSelMonth;
    }

    public void setSelDay(int nDay) {
        mnSelDay = nDay;
    }

    public int getSelDay() {
        return mnSelDay;
    }

    public void initSelDay() {
        initEventData();
        getToDay();
        mnSelYear  = mnYearOfToday;
        mnSelMonth = mnMonthOfToday;
        mnSelDay   = mnDayOfToday;
        setCurrYear(mnYearOfToday);
        setCurrMonth(mnMonthOfToday);
    }

    public void setSavePWD(boolean bSave) {
        mbSavePassword = bSave;
    }

    public boolean IsSavePWD() {
        return mbSavePassword;
    }

    public void setMonthMenuShow(boolean bShow) {
        mbMonthMenuShow = bShow;
    }

    public boolean getIsMonthMenuShow() {
        return mbMonthMenuShow;
    }

    public void setDayMenuShow(boolean bShow) {
        mbDayMenuShow = bShow;
    }

    public boolean getIsDayMenuShow() {
        return mbDayMenuShow;
    }

    public void setReceiveEventFree(boolean bReceived) {
        mbReceiveEventFree = bReceived;
    }

    public boolean getIsReceiveEventFree() {
        return mbReceiveEventFree;
    }

    public void setAllDayCheck(boolean bCheck) {
        mbAllDayChk = bCheck;
    }

    public boolean getIsAllDayCheck() {
        return mbAllDayChk;
    }

    public void setRecurrenceSelected(int nSelected) {
        mnRecurrenceSelected = nSelected;
    }

    public int getRecurrenceSelected() {
        return mnRecurrenceSelected;
    }

    public void setReminderSelected(int nSelected) {
        mnReminderSelected = nSelected;
    }

    public int getReminderSelected() {
        return mnReminderSelected;
    }

    public void setDateShowFromResId(int nResId) {
        mnDateShowFromResId = nResId;
    }

    public int getDateShowFromResId() {
        return mnDateShowFromResId;
    }

    public void setSyncWndType(int nType) {
        mnSyncWndType = nType;
    }

    public int getSyncWndType() {
        return mnSyncWndType;
    }

    public void setCheckCaption(String szCaption, int nResId) {
        mszCheckCaption = szCaption;
        mnCheckDlgResId = nResId;
    }

    public String getCheckCaption() {
        return mszCheckCaption;
    }

    public int getCheckDlgResId() {
        return mnCheckDlgResId;
    }

    public void setEventId(String szEventId) {
        mszEventId = szEventId;
    }

    public String getEventId() {
        return mszEventId;
    }

    public int getMonthDay(int Year, int Month) {
        int      mDay = 0;
        Date     date = new Date(Year, Month, 1);
        Calendar mCld = Calendar.getInstance();

        mCld.setTime(date);

        if ((1 <= Month) && (Month <= 12)) {
            return mCld.get(Calendar.DAY_OF_MONTH);
        }

        return mDay;
    }

    public int getlastMonthDayScore(int year_log, int month_log) {
        month_log = month_log - 1;

        if (month_log == 0) {
            month_log = 12;
        }

        Date     date = new Date(year_log, month_log, 1);    // now
        Calendar cal  = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);

        int month_day_score = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return month_day_score;
    }

    public int getMonthDayScore(int year_log, int month_log) {
        Date     date = new Date(year_log, month_log, 1);    // now
        Calendar cal  = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);

        int month_day_score = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return month_day_score;
    }

    public int getFirstDayIndex(int year, int month_num) {
        int first_day_num = use(year, month_num);

        first_day_num = first_day_num - 1;

        return first_day_num;
    }

    public int use(int reyear, int remonth) {
        int      week_num = 0;
        Calendar cal      = Calendar.getInstance();

        cal.set(reyear, remonth - 1, 1);
        week_num = (int) (cal.get(Calendar.DAY_OF_WEEK));

        return week_num;
    }

    public void setRunPrintView(boolean bRun) {
        mrunPrintView = bRun;
    }

    public boolean getRunPrintView() {
        return mrunPrintView;
    }

    public void setOptionBlock(boolean bBlock) {
        mbOptionBlock = bBlock;
    }

    public boolean getOptionBlock() {
        return mbOptionBlock;
    }

    public CalendarData getCalendarData() {
        return calendarData;
    }

    public void setGoHome(boolean bGo) {
        mbGoHome = bGo;
    }

    public boolean getIsGoHome() {
        return mbGoHome;
    }

    public Handler getHandler() {
        return mwndHandler;
    }

    public void setPrintRunMode(int nMode) {
        mnPrintRunMode = nMode;
    }

    public int getPrintRunMode() {
        return mnPrintRunMode;
    }

    public Handler getThreadHandler() {
        return calendarThreadsLow.getHandler();
    }

    public Looper getThreadHandlerLooper(int nPriority) {
        if (0 > nPriority) {
            if (null != calendarThreadsHigh) {
                return calendarThreadsHigh.getLooper();
            }
        } else {
            if (null != calendarThreadsLow) {
                return calendarThreadsLow.getLooper();
            }
        }

        return null;
    }

    public void showProgressBar(boolean bShow) {
        if (null != mwndHandler) {
            Message msg = new Message();

            msg.what = EventMessage.SHOW_PROGRESS_DLG;

            if (!bShow) {
                msg.arg1 = EventMessage.WND_STOP;
            } else {
                msg.arg1 = EventMessage.WND_SHOW;
            }

            mwndHandler.sendMessage(msg);
        }
    }

    public void boardcastToWidget(Context context, int nId) {
        calendarWidgetBroadcast.broadcastMsg(context, nId);
    }

    public void notifyClockLogout(Context context) {
        Bundle bundleSend = new Bundle();

        bundleSend.putBoolean("Data", true);

        Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);

        intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_LOGOUT);
        intentSend.putExtras(bundleSend);
        context.sendBroadcast(intentSend);
    }

    public boolean checkNetWorkEnable() {
        long lResult = 0;

        try {
            lResult = CCPHttpClientHelper.pingServer("http://www.google.com", this.mCalendarApp);
        } catch (MalformedURLException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (0 < lResult) {
            return true;
        }

        mCalendarApp.showProgressDlg(false, null, TAG + "619");

        String szCaption = mCalendarApp.getString(R.string.Unable_2_connect_2_Internet);

        mCalendarApp.showAlarmDlg(true, null, szCaption);

        return false;
    }

    public void showWindow(int nWndId) {
        if (null != calendarWindowControl) {
            calendarWindowControl.showWindow(nWndId);
        }
    }

    public boolean isValidWnd(int nWndId) {
        if (null != calendarWindowControl) {
            return calendarWindowControl.isValidWnd(nWndId);
        }

        return false;
    }

    public CalendarWnd getWindow(int nWndId) {
        return calendarWindowControl.getWindow(nWndId);
    }

    public boolean isValidEvent(int nEventId) {
        if (null != calendarEventHandler) {
            return calendarEventHandler.isValidWnd(nEventId);
        }

        return false;
    }

    public CalendarEvent getEvent(int nEventId) {
        return calendarEventHandler.getEvent(nEventId);
    }

    public void setReflashMonthEnable(boolean bEnable) {
        mbReflashMonthEvent = bEnable;
    }

    public boolean getReflashMonthEnable() {
        return mbReflashMonthEvent;
    }
}

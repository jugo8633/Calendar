//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Dialog;

import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.KeyEvent;

import android.widget.HpActivity;

import com.hp.ij.calendar.data.CalendarMultiCalendarData;
import com.hp.ij.calendar.data.CalendarNewEventData;
import com.hp.ij.printing.common.data.Data;
import com.hp.ij.printing.common.data.DispatchType;
import com.hp.ij.printing.imageprocess.HtmlProcess;
import com.hp.ij.utils.printerhost.PrinterHost;
import com.hp.print.apdk.PAPER_SIZE;

import frame.event.EventMessage;

import frame.view.AlarmDialog;
import frame.view.CheckDialog;

public class CalendarApp extends HpActivity {
    private static final String TAG             = "CalendarApp";
    public static final int     COUNTRY_SIZE    = 12;
    public CalendarApplication  application     = null;
    public boolean              m_bServiceReady = true;

    /**
     * print variables
     */
    protected static final int[] DayEvent_XPosition           = {
        47, 134, 225, 314, 404, 495, 585
    };
    protected static final int[] DayEvent_YPosition           = {
        156, 210, 264, 319, 374, 428
    };
    private static final String  PRINTING_DATA_RASTER_SERVICE =
        "com.hp.ij.printing.service.CALENDAR_DATA_RASTER_SERVICE";
    private static final String PRINTING_PHOTO_RASTER_SERVICE =
        "com.hp.ij.printing.service.CALENDAR_PHOTO_RASTER_SERVICE";
    private static final String PRINTING_PREVIEW_ACTIVITY_INTENT =
        "com.hp.ij.printpreview.CalendarPrintPreviewActivity";
    protected static final int[] PrintTable_XPosition = {
        43, 179, 314, 449, 583, 718, 852
    };
    protected static final int[] PrintTable_YPosition = {
        154, 235, 316, 398, 479, 561
    };
    private CalendarPrintReport
        mPrtReport_M                                  = null,
        mPrtReport_D                                  = null;
    private HtmlProcess mHtmlProcessor_M              = null,
                        mHtmlProcessor_D              = null;
    private Intent      intentPrintPreview            = new Intent(PRINTING_PREVIEW_ACTIVITY_INTENT);
    private Data        data_M                        = null,
                        data_D                        = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        startRunWnd();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        application = (CalendarApplication) this.getApplication();
        application.setCurrentActivity(this);

        // setTheme(android.R.style.Theme_HP);
        super.onCreate(savedInstanceState);

        // application.setReflashMonthEnable(true);
        startRunWnd();
        application.initEventHandler();

        /**
         * initial message bar
         */
        initMessageBar();

        /**
         * initial printing function
         */
        initPrinting();
    }

    @Override
    protected void onStop() {

        // TODO Auto-generated method stub
        if (0 == application.getSyncWndType()) {
            application.setSyncWndType(-1);
            application.setReflashMonthEnable(false);
            this.showAlarmDlg(false, null, null);
        }

        super.onStop();
    }

    @Override
    protected void onRestart() {

        // TODO Auto-generated method stub
        boolean bRunPrint = application.getRunPrintView();

        application.setReflashMonthEnable(true);

        if (bRunPrint) {
            int nPrintRunMode = application.getPrintRunMode();

            // LaunchPrintPreview(nPrintRunMode);
            application.setRunPrintView(true);

            Message msg = new Message();

            msg.what = nPrintRunMode;
            application.getWndHandler().sendMessageDelayed(msg, 0);
        } else {
            showProgressDlg(false, null, TAG + "265");
        }

        if (!application.getReflashMonthEnable()) {
            this.showAlarmDlg(false, null, null);
        }

        super.onRestart();
    }

    private void startRunWnd() {
        getOrientation();

        try {
            int nRunWnd = application.getRunWnd();

            if (application.getIsGoHome()) {
                application.setGoHome(false);

                if (EventMessage.RUN_LOGINWND == nRunWnd) {
                    ((CalendarLoginWnd) application.getWindow(EventMessage.RUN_LOGINWND)).initCalendarLoginWnd();
                    showWnd(EventMessage.RUN_LOGINWND, true);
                } else {
                    showWnd(EventMessage.RUN_MONTHWND, true);
                }
            } else {
                showWnd(nRunWnd, true);

                if (EventMessage.RUN_LOADWND == nRunWnd) {
                    ((CalendarLoadWnd) application.getWindow(EventMessage.RUN_LOADWND)).initProgressBar();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * initial month view or day view footbar button
     */
    private void initFootbarBtn() {
        wndHandler.sendEmptyMessage(6000);
    }

    private void initFootbarBtnHandler() {
        int nRunWnd = application.getRunWnd();

        switch (nRunWnd) {
        case EventMessage.RUN_MONTHWND :
            ((CalendarMonthWnd) application.getWindow(EventMessage.RUN_MONTHWND)).setFootbarSelect(R.id.fbMonthView);

            break;
        case EventMessage.RUN_DAYWND :
            ((CalendarDayWnd) application.getWindow(EventMessage.RUN_DAYWND)).setFootbarSelect(R.id.fbDayView);

            break;
        case EventMessage.RUN_EVENTINFOWND :
            ((CalendarDayEventInfoWnd) application.getWindow(EventMessage.RUN_EVENTINFOWND)).setFootbarSelect(-1);

            break;
        }
    }

    private void initPrinting() {
        data_M = new Data();

        if (data_M != null) {

//          data_M.mTopResId  = R.drawable.print_size_top;
//          data_M.mLeftResId = R.drawable.print_size_left;
        }

        data_D = new Data();

        if (data_D != null) {
            data_D.mTopResId  = R.drawable.print_size_top_85;
            data_D.mLeftResId = R.drawable.print_size_left_11;
        }

        mHtmlProcessor_M = new HtmlProcess(application.getApplicationContext(), data_M, DispatchType.PRINTING_DATA,
                                           PRINTING_DATA_RASTER_SERVICE);
        mHtmlProcessor_D = new HtmlProcess(application.getApplicationContext(), data_D, DispatchType.PRINTING_DATA,
                                           PRINTING_DATA_RASTER_SERVICE);

//      mPrtReport = new CalendarPrintReport(this, data);
    }

    private void getOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            application.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            application.setOrientation(Configuration.ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        application.setOptionBlock(false);

        Intent intent = new Intent();

        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK :
            int nRunWnd = application.getRunWnd();

            switch (nRunWnd) {
            case EventMessage.RUN_LOADWND :
                break;
            case EventMessage.RUN_LOGINWND :
                ((CalendarLoginWnd) application.getWindow(EventMessage.RUN_LOGINWND)).clearEditSelected();
            case EventMessage.RUN_MONTHWND :
                application.setGoHome(true);
                finish();

                return true;
            case EventMessage.RUN_DAYWND :
                showWnd(EventMessage.RUN_MONTHWND, true);

                return true;
            case EventMessage.RUN_ALARMWND :
                break;
            case EventMessage.RUN_PROGWND :
                break;
            case EventMessage.RUN_EVENTINFOWND :
                showWnd(EventMessage.RUN_DAYWND, true);

                return true;
            case EventMessage.RUN_NEWEVENTWND :
                ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).hideKeyboard();

                int nWndResId =
                    ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND)).getWindowIdFrom();

                showWnd(nWndResId, true);

                return true;
            case EventMessage.RUN_SYNCWND :
                break;
            case EventMessage.RUN_SETTINGWND :
                CalendarMultiCalendarData calendarMultiCalendarData =
                    new CalendarMultiCalendarData(application.getCalendarData());
                int nPreRunWnd = calendarMultiCalendarData.getPreWindowId();

                calendarMultiCalendarData = null;
                showWnd(nPreRunWnd, true);

                return true;
            case EventMessage.RUN_EDITEVENTWND :
                showWnd(EventMessage.RUN_EVENTINFOWND, true);

                return true;
            case EventMessage.RUN_PRINTPREVIEW_M :
                break;
            case EventMessage.RUN_PRINTPREVIEW_D :
                break;
            case EventMessage.RUN_PRINTPREVIEW_E :
                break;
            }

            break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void addMainView(int nViewId, boolean bShowHeader, boolean bShowFoot) {
        attachPrimaryView(nViewId, bShowHeader, bShowFoot);
    }

    public void addFootView(int nViewId) {
        attachFooterBar(nViewId, HpActivity.FOOTER_1);
    }

    public void addHeaderView(int nViewId) {
        attachHeaderBar(nViewId, HpActivity.TITLE);
    }

    public void showWnd(int nRunWnd, boolean bSaveState) {
        Message msg = new Message();

        msg.what = EventMessage.RUN_WND;
        msg.arg1 = nRunWnd;

        if (bSaveState) {
            msg.arg2 = 1;
        } else {
            msg.arg2 = 0;
        }

        wndHandler.sendMessage(msg);
        msg = null;
    }

    private void showWndHandler(int nRunWnd, boolean bSaveState) {
        if (nRunWnd == EventMessage.RUN_DATEWND) {
            this.removeDialog(EventMessage.RUN_DATEWND);
            this.showDialog(EventMessage.RUN_DATEWND);

            CalendarNewEventData calendarNewEventData = new CalendarNewEventData(application.getCalendarData());

            calendarNewEventData.setDateDialogShowStatus(1);
            calendarNewEventData = null;

            return;
        }

        if (nRunWnd == EventMessage.RUN_TIMEWND) {
            this.showTimeDlg(true);

            return;
        }

        if (nRunWnd == EventMessage.RUN_SYNCWND) {
            this.showDialog(EventMessage.RUN_SYNCWND);

            return;
        }

        if ((nRunWnd == EventMessage.RUN_PRINTPREVIEW_M) || (nRunWnd == EventMessage.RUN_PRINTPREVIEW_D)
                || (nRunWnd == EventMessage.RUN_PRINTPREVIEW_E)) {
            application.setPrintRunMode(nRunWnd);

            // LaunchPrintPreview(nRunWnd);
            // runPrintPreview(nRunWnd);
            application.setRunPrintView(true);

            Message msg = new Message();

            msg.what = nRunWnd;
            application.getWndHandler().sendMessageDelayed(msg, 3000);

            return;
        }

        if (application.isValidWnd(nRunWnd)) {
            if (bSaveState) {
                application.setRunWnd(nRunWnd);
            }

            /**
             * reset layout when rotate
             */
            switch (nRunWnd) {
            case EventMessage.RUN_DAYWND :
                ((CalendarDayWnd) application.getWindow(EventMessage.RUN_DAYWND)).initCalendarDayWnd();

                break;
            case EventMessage.RUN_NEWEVENTWND :
            case EventMessage.RUN_EDITEVENTWND :
                ((CalendarNewEventWnd) application.getWindow(nRunWnd)).initCalendarNewEventWnd();

                CalendarNewEventData calendarNewEventData = new CalendarNewEventData(application.getCalendarData());
                int                  nStatus              = calendarNewEventData.getDateDialogShowStatus();

                if (1 == nStatus) {
                    this.removeDialog(EventMessage.RUN_DATEWND);
                    this.showDialog(EventMessage.RUN_DATEWND);
                }

                calendarNewEventData = null;

                break;
            }

            application.showWindow(nRunWnd);
        } else {}
    }

    public int getCurrentYear() {
        CalendarMonth calendarMonth = new CalendarMonth();

        return calendarMonth.getYear();
    }

    public int getCurrentMonth() {
        CalendarMonth calendarMonth = new CalendarMonth();

        return calendarMonth.getMonth();
    }

    public void updateMonthEvent() {
        int nRunWnd = application.getRunWnd();

        if (nRunWnd == EventMessage.RUN_MONTHWND) {
            if (application.isValidWnd(EventMessage.RUN_MONTHWND)) {
                ((CalendarMonthWnd) application.getWindow(EventMessage.RUN_MONTHWND)).updateMonthEvent();
            }
        }

        if (nRunWnd == EventMessage.RUN_DAYWND) {
            if (application.isValidWnd(EventMessage.RUN_DAYWND)) {
                ((CalendarDayWnd) application.getWindow(EventMessage.RUN_DAYWND)).updateMonthEvent();
            }
        }
    }

    public void getMonthEvent(int nYear, int nMonth) {
        if (null == application.CService) {
            return;
        }

        application.CService.getFreeEvent(nYear, nMonth);
    }

    public void showAlarmDlg(boolean bShow, String szTitle, String szAlarm) {
        this.removeDialog(EventMessage.RUN_SYNCWND);

        if (bShow) {
            application.setAlarm(szAlarm);
            this.showDialog(EventMessage.SHOW_ALARM_DLG);
        } else {
            this.removeDialog(EventMessage.SHOW_ALARM_DLG);
        }
    }

    public synchronized void showProgressDlg(boolean bShow, String strCaption, String szInfo) {
        Message msg = new Message();

        msg.what = EventMessage.SHOW_PROGRESS_DLG;

        if (bShow) {
            msg.arg1 = 1;
        } else {
            msg.arg1 = 0;
        }

        msg.obj = strCaption;
        wndHandler.sendMessage(msg);
        msg = null;
    }

    private void showProgressDlgHandler(boolean bShow, String strCaption) {
        String szCaption = null;

        if (null != strCaption) {
            szCaption = strCaption;
        } else {
            szCaption = this.getString(R.string.downloading);
        }

        if (bShow) {
            application.setOptionBlock(true);
            showMessageBar(szCaption, bShow);
            holdRun(3000000);
        } else {
            timeToHideProgress(-1);
            showMessageBar(szCaption, bShow);
            application.setOptionBlock(false);
        }
    }

    private void timeToHideProgress(int nMillis) {
        Handler handler = application.getThreadHandler();

        if (null != handler) {
            handler.removeMessages(EventMessage.SHOW_PROGRESS_DLG);

            if (0 < nMillis) {
                Message msg = new Message();

                msg.what = EventMessage.SHOW_PROGRESS_DLG;
                msg.arg1 = EventMessage.WND_STOP;
                handler.sendMessageDelayed(msg, nMillis);
            }
        }

        handler = null;
    }

    public void showCheckDlg(boolean bShow, String szCaption, int nWndID) {
        application.setCheckCaption(szCaption, nWndID);

        if (bShow) {
            this.showDialog(EventMessage.SHOW_CHECK_DLG);
        } else {
            this.removeDialog(EventMessage.SHOW_CHECK_DLG);
        }
    }

    public void showSyncCheckDlg(boolean bShow) {
        if (bShow) {
            this.showDialog(EventMessage.SHOW_SYNC_ALARM_DLG);
        } else {
            this.removeDialog(EventMessage.SHOW_SYNC_ALARM_DLG);
        }
    }

    public void showTimeDlg(boolean bShow) {
        if (bShow) {
            this.showDialog(EventMessage.RUN_TIMEWND);
        } else {
            this.removeDialog(EventMessage.RUN_TIMEWND);
        }
    }

    private synchronized void holdRun(int nMillis) {
        try {
            if (null != this.application.CService.getService()) {
                this.application.CService.wait(nMillis);
            }
        } catch (Exception e) {}
    }

    /**
     * Dialog handle
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case EventMessage.SHOW_ALARM_DLG :
            CalendarApp.this.removeDialog(EventMessage.RUN_SYNCWND);

            int nSyncRunType =
                application
                    .getSyncWndType();    // ((CalendarSyncDlg) application.getWindow(EventMessage.RUN_SYNCWND)).getRunType();

            if (0 == nSyncRunType) {    // sync running
                CheckDialog syncChkDlg = new CheckDialog(this, application.getWndHandler(),
                                             EventMessage.SHOW_SYNC_ALARM_DLG);
                String szCaption = this.getString(R.string.unable_to_synchronize_with_google_calendar);

                syncChkDlg.setCheckCaption(szCaption);
                syncChkDlg.showWindow(true);

                return syncChkDlg.getDialog();
            }

            AlarmDialog alarmDlg = new AlarmDialog(this, application.getWndHandler(), EventMessage.SHOW_ALARM_DLG);

            alarmDlg.setAlarmInfo(application.getAlarm());
            alarmDlg.showWindow(true);

            return alarmDlg.getDialog();
        case EventMessage.RUN_DATEWND :
            ((CalendarDateWnd) application.getWindow(EventMessage.RUN_DATEWND)).initDialog();

            Dialog dialog = ((CalendarDateWnd) application.getWindow(EventMessage.RUN_DATEWND)).getDialog();

            if (null == dialog) {
                return null;
            }

            return dialog;
        case EventMessage.RUN_SYNCWND :
            int nSyncWndType = application.getSyncWndType();

            ((CalendarSyncDlg) application.getWindow(EventMessage.RUN_SYNCWND)).initDialog(nSyncWndType);

            Dialog syncDialog = ((CalendarSyncDlg) application.getWindow(EventMessage.RUN_SYNCWND)).getDialog();

            return syncDialog;
        case EventMessage.SHOW_CHECK_DLG :
            int         nResId = application.getCheckDlgResId();
            CheckDialog chkDlg = new CheckDialog(this, application.getWndHandler(), nResId);

            chkDlg.setCheckCaption(application.getCheckCaption());
            chkDlg.showWindow(true);

            return chkDlg.getDialog();
        case EventMessage.SHOW_SYNC_ALARM_DLG :
            CheckDialog syncChkDlg = new CheckDialog(this, application.getWndHandler(),
                                         EventMessage.SHOW_SYNC_ALARM_DLG);
            String szCaption = this.getString(R.string.unable_to_synchronize_with_google_calendar);

            syncChkDlg.setCheckCaption(szCaption);
            syncChkDlg.showWindow(true);

            return syncChkDlg.getDialog();
        case EventMessage.RUN_TIMEWND :

            // ((CalendarTimeWnd) WndData.get(EventMessage.RUN_TIMEWND)).initDialog();
            Dialog dlgTime = ((CalendarTimeWnd) application.getWindow(EventMessage.RUN_TIMEWND)).getDialog();

            if (null == dlgTime) {
                return null;
            }

            return dlgTime;
        }

        return super.onCreateDialog(id);
    }

    // for printing function
    public void LaunchPrintPreview(int ViewType) {
        int printDegree = 90;

        // Day and Event view
        if ((null == data_M) || (null == data_D)) {
            return;
        }

        data_M.mRenderWidth  = 512;
        data_M.mRenderHeight = 716;
        data_D.mRenderWidth  = 512;
        data_D.mRenderHeight = 716;

        // application.setRunPrintView(true);
        switch (ViewType) {
        case EventMessage.RUN_PRINTPREVIEW_M :
            data_M.mRenderWidth  = 680;
            data_M.mRenderHeight = 470;

//          data_M.mTopResId            = R.drawable.print_size_top;
//          data_M.mLeftResId           = R.drawable.print_size_left;
            data_M.mPrintOutRotateAngle = 90;
            mPrtReport_M                = new CalendarPrintReport(this, data_M);
            mPrtReport_M.setapplication(application);
            mPrtReport_M.buildHTMLFormat();

            if (mHtmlProcessor_M != null) {
                mHtmlProcessor_M.setHostIP(PrinterHost.get(this));
                mHtmlProcessor_M.setPreviewGalleryPortrait(false);
                mHtmlProcessor_M.setPapersize(PAPER_SIZE.LETTER);

                Intent intentPrintPreview = new Intent(PRINTING_PREVIEW_ACTIVITY_INTENT);

                if (intentPrintPreview != null) {
                    mHtmlProcessor_M.savePreviewImage(true);
                    mHtmlProcessor_M.showPreview(intentPrintPreview, getApplication());
                }
            }

            break;
        case EventMessage.RUN_PRINTPREVIEW_D :
            data_M.mRenderWidth  = 512;
            data_M.mRenderHeight = 716;

//          data_M.mTopResId            = R.drawable.print_size_top_85;
//          data_M.mLeftResId           = R.drawable.print_size_left_11;
            data_M.mPrintOutRotateAngle = 0;
            mPrtReport_M                = new CalendarPrintReport(this, data_M);
            mPrtReport_M.setapplication(application);
            mPrtReport_M.buildHTMLFormat();

            if (mHtmlProcessor_M != null) {
                mHtmlProcessor_M.setHostIP(PrinterHost.get(this));
                mHtmlProcessor_M.setPreviewGalleryPortrait(true);
                mHtmlProcessor_M.setPapersize(PAPER_SIZE.LETTER);

                Intent intentPrintPreview = new Intent(PRINTING_PREVIEW_ACTIVITY_INTENT);

                if (intentPrintPreview != null) {
                    mHtmlProcessor_M.savePreviewImage(true);
                    mHtmlProcessor_M.showPreview(intentPrintPreview, getApplication());
                }
            }

            break;
        case EventMessage.RUN_PRINTPREVIEW_E :
            data_M.mRenderWidth  = 512;
            data_M.mRenderHeight = 716;

//          data_M.mTopResId            = R.drawable.print_size_top_85;
//          data_M.mLeftResId           = R.drawable.print_size_left_11;
            data_M.mPrintOutRotateAngle = 0;
            mPrtReport_D                = new CalendarPrintReport(this, data_M);
            mPrtReport_D.setapplication(application);
            mPrtReport_D.buildHTMLFormat();

            if (mHtmlProcessor_M != null) {
                mHtmlProcessor_M.setHostIP(PrinterHost.get(this));
                mHtmlProcessor_M.setPreviewGalleryPortrait(true);
                mHtmlProcessor_M.setPapersize(PAPER_SIZE.LETTER);

                Intent intentPrintPreview = new Intent(PRINTING_PREVIEW_ACTIVITY_INTENT);

                if (intentPrintPreview != null) {

                    // mHtmlProcessor.savePreviewImage(true);
                    mHtmlProcessor_M.showPreview(intentPrintPreview, getApplication());
                }
            }

            break;
        default :

//          data_M.mTopResId            = R.drawable.print_size_top;
//          data_M.mLeftResId           = R.drawable.print_size_left;
            data_M.mPrintOutRotateAngle = 90;
            mPrtReport_M                = new CalendarPrintReport(this, data_M);
            mPrtReport_M.setapplication(application);
            mPrtReport_M.buildHTMLFormat();

            if (mHtmlProcessor_M != null) {
                mHtmlProcessor_M.setHostIP(PrinterHost.get(this));

                Intent intentPrintPreview = new Intent(PRINTING_PREVIEW_ACTIVITY_INTENT);

                if (intentPrintPreview != null) {

                    // mHtmlProcessor.savePreviewImage(true);
                    mHtmlProcessor_M.showPreview(intentPrintPreview, getApplication());
                }
            }

            break;
        }

        initFootbarBtn();
        showProgressDlg(false, null, TAG);
    }

    private void initMessageBar() {
        super.setMessageBarShowProgress(true);
    }

    private void showMessageBar(String szCaption, boolean bShow) {
        if (bShow && (null != szCaption)) {
            super.setMessageBarText(szCaption);
        }

        super.showMagBar(bShow);
    }

    Handler wndHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);

            boolean bSave = false;
            boolean bShow = true;

            switch (msg.what) {
            case EventMessage.RUN_WND :
                if (0 == msg.arg2) {
                    bSave = false;
                } else {
                    bSave = true;
                }

                showWndHandler(msg.arg1, bSave);

                break;
            case EventMessage.SHOW_PROGRESS_DLG :
                if (0 == msg.arg1) {
                    bShow = false;
                } else {
                    bShow = true;
                }

                String szCaption = (String) msg.obj;

                showProgressDlgHandler(bShow, szCaption);

                break;
            case 6000 :
                initFootbarBtnHandler();

                break;
            }
        }
    };
}

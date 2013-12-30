//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import android.os.Handler;
import android.os.Message;

import android.text.TextPaint;

import android.util.Log;

import android.view.View;

import android.widget.TextView;

import com.hp.ij.printing.common.data.Data;

import frame.event.EventMessage;

public class CalendarPrintReport {
    public static final int      COUNTRY_SIZE       = 12;
    private static final String  DEFALUT_HTML       = ".html";
    private static final String  DEFALUT_PRINT      = "print";
    private static String        DEFALUT_PRINT_HTML = "";
    protected static final int[] DayEvent_XPosition = {
        47, 134, 225, 314, 404, 495, 585
    };
    protected static final int[] DayEvent_YPosition = {
        156, 210, 264, 319, 374, 428
    };

    // printing setup
    private static int EventStatus = 0;

    // calendar layout id setting
    public static final int      ID_CALENDAR_BASEID        = 1000000000;
    public static final int      ID_CALENDAR_IDX           = 1000;
    private static final int     MAX_FILESIZE_PRINT_REPORT = 20480;
    private static final int     MSG_Report_Finished       = 1;
    protected static final int[] PrintTable_XPosition      = {
        43, 179, 314, 449, 583, 718, 852
    };
    protected static final int[] PrintTable_YPosition      = {
        154, 235, 316, 398, 479, 561
    };
    public static final String   TAG                       = "CalendarPrintReport";
    private static int           TodayVisible              = 0;
    private static int           TodayXSet                 = 0;
    private static int           TodayYSet                 = 0;
    private static int           findEventId               = 0;
    private static int           findTodayId               = 0;
    private static int           todayX                    = 0;
    private static int           todayY                    = 0;
    private static int           IsTodaySet                = 0;
    public int                   baseId                    = 0;
    public int                   FirstPageWidth            = 7488;
    public int                   OtherPageWidth            = 19968;
    public CalendarApplication   application               = null;
    private ArrayList<String>    default_print_html_file   = new ArrayList<String>();

    /**
     *  Message loop
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_Report_Finished :
                if (msg.arg1 < 0) {
                    iCallback.onFinished(false);
                } else {
                    iCallback.onFinished(true);
                }

                break;
            }

            super.handleMessage(msg);
        }
    };
    private ICalendarPrintReportCallback iCallback;
    private Data                         mData;
    private Activity                     mParent;

//  private HashMap<String, Integer> hmWeatherCond = 
//      new HashMap<String, Integer>();
    public CalendarPrintReport(Activity act, Data data) {

//      CalendarHashMapData.initHashMapCond(hmWeatherCond);    
        mParent = act;
        mData   = data;
    }

    public void buildHTMLFormat() {
        int           lastMonthDay    = 0;
        int           MonthDay        = 0;
        int           FirstDayIndex   = 0;
        int           mMonthYearDay[] = new int[3];
        int           mMonthInfo[]    = new int[3];
        CalendarMonth calendarMonth   = null;

        if (mData.mHtmlURIList.isEmpty() != true) {
            mData.mHtmlURIList.clear();
        }

        // build HTML file.
        int index = 0;

        if ((index >= 0) && (index < COUNTRY_SIZE)) {
            calendarMonth = application.getCalendarMonth();

//          // get last month day                                                                           
            mMonthYearDay[0] = calendarMonth.getMonth();
            mMonthYearDay[1] = calendarMonth.getYear();
            mMonthYearDay[2] = calendarMonth.getDay();
            mMonthInfo[0]    = calendarMonth.getlastMonthDayScore();
            mMonthInfo[1]    = calendarMonth.getMonthDayScore();
            mMonthInfo[2]    = calendarMonth.getFirstDayIndex();

            int nWnd = application.getRunWnd();

            switch (nWnd) {
            case EventMessage.RUN_MONTHWND :
                int yeartemp  = application.getViewCenterYear();
                int monthtemp = application.getViewCenterMonth();

                mMonthYearDay[0] = monthtemp;
                mMonthYearDay[1] = yeartemp;
                mMonthYearDay[2] = application.getMonthDay(yeartemp, monthtemp);
                mMonthInfo[0]    = application.getlastMonthDayScore(yeartemp, monthtemp);
                mMonthInfo[1]    = application.getMonthDayScore(yeartemp, monthtemp);
                mMonthInfo[2]    = application.getFirstDayIndex(yeartemp, monthtemp);
                createHTML(ID_CALENDAR_BASEID + ID_CALENDAR_IDX * index, 0, mMonthYearDay, mMonthInfo);

                break;
            case EventMessage.RUN_DAYWND :
                createHTML(ID_CALENDAR_BASEID + ID_CALENDAR_IDX * index, 1, mMonthYearDay, mMonthInfo);

                break;
            case EventMessage.RUN_EVENTINFOWND :
                createHTML(ID_CALENDAR_BASEID + ID_CALENDAR_IDX * index, 2, mMonthYearDay, mMonthInfo);

                break;
            default :
                createHTML(ID_CALENDAR_BASEID + ID_CALENDAR_IDX * index, 0, mMonthYearDay, mMonthInfo);

                break;
            }

            createHTMLImage();
        }

//      // Add file URI to list.
//      String filePath = String.format("file://%s/%s", mParent.getFilesDir(), DEFALUT_PRINT_HTML);
//
//      default_print_html_file.add(filePath);
        for (String s : default_print_html_file) {
            mData.mHtmlURIList.add(s);
        }
    }

    public void buildHTMLFormatAsync() {
        Log.d(TAG, "buildHTMLFormat entry...");

        if (mData.mHtmlURIList.isEmpty() == false) {
            mData.mHtmlURIList.clear();
        }

        new Thread() {
            public void run() {
                Log.d(TAG, "buildHTMLFormat thread entry...");

                Message message = new Message();

                message.what = MSG_Report_Finished;

                try {
                    buildHTMLFormat();
                    message.arg1 = 0;
                    Log.d(TAG, "buildHTMLFormat successful...");
                } catch (Exception e) {
                    e.printStackTrace();
                    message.arg1 = -1;
                    Log.d(TAG, "buildHTMLFormat fail...");
                }

                Log.d(TAG, "buildHTMLFormat exit...");

                if (handler != null) {
                    handler.sendMessage(message);
                }
            }
        }.start();
        Log.d(TAG, "buildHTMLFormat exit...");
    }

    public void setOnHandleCallback(ICalendarPrintReportCallback i) {
        iCallback = i;
    }

    private String getTextById(int id) {
        String str;
        View   v = mParent.findViewById(id);

        if (v != null) {
            str = ((TextView) v).getText().toString();
        } else {
            str = "";
        }

        return str;
    }

    private void captureImagetoFile(int rid, String fname) {
        Bitmap bmp = BitmapFactory.decodeResource(mParent.getResources(), rid);

        if (bmp != null) {
            saveBMPtoFile(bmp, fname);
            bmp.recycle();
            bmp = null;
        }
    }

    private void captureImagetoFile(View gv, String fname) {
        if (gv != null) {
            Bitmap bmp = Bitmap.createBitmap(gv.getWidth(), gv.getHeight(), Bitmap.Config.RGB_565);

            if (bmp != null) {
                gv.draw(new Canvas(bmp));
                saveBMPtoFile(bmp, fname);
                bmp.recycle();
                bmp = null;
            }
        }
    }

    private boolean saveBMPtoFile(Bitmap bmp, String fname) {

        // File f = new File(PRINTOUT_PATH + filename);
        // f.delete();
        FileOutputStream fOut = null;

        try {
            fOut = mParent.openFileOutput(fname, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

            if ((fOut != null) && (bmp != null)) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            }

            fOut.flush();
            fOut.close();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Bitmap resizePicture(Bitmap bitmapOrg, int new_width, int new_height) {
        Bitmap bitmapTemp = null;

        if (bitmapOrg != null) {
            int width     = bitmapOrg.getWidth();
            int height    = bitmapOrg.getHeight();
            int newWidth  = new_width;
            int newHeight = new_height;

            // calculate the scale - in this case = 0.4f
            float scaleWidth  = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            // create matrix for the manipulation
            Matrix matrix = new Matrix();

            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);

            // recreate the new Bitmap
            bitmapTemp = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
        }

        return bitmapTemp;
    }

    // Create HTML file
    private void createHTML(int baseid, int catg, int MonthYearDay[], int MonthInfo[]) {
        Log.d(TAG,
              "createHTML()" + MonthYearDay[0] + "," + MonthYearDay[1] + "," + MonthYearDay[2] + ":" + MonthInfo[0]
              + "," + MonthInfo[1] + "," + MonthInfo[2]);

        String todayPrediction = "";
        String mMonthYear      = "";
        int    mMonth, mYear, mDay;
        int    latMDS, MDS, FDI;
        String szTmp = "";

        if (MonthYearDay.length != 3) {
            Log.d(TAG, "MonthYearDay parm error!");
            mMonth = 1;
            mYear  = 2010;
            mDay   = 1;
        } else {
            mMonth = MonthYearDay[0];
            mYear  = MonthYearDay[1];
            mDay   = MonthYearDay[2];
        }

        if (MonthInfo.length != 3) {
            Log.d(TAG, "MonthInfo parm error!");
            latMDS = 31;
            MDS    = 31;
            FDI    = 5;
        } else {
            latMDS = MonthInfo[0];
            MDS    = MonthInfo[1];
            FDI    = MonthInfo[2];
        }

        mMonthYear = application.getCalendarMonth().getMonthName(mMonth) + " " + mYear;

        InputStream                 in     = null;
        FileOutputStream            out    = null;
        ArrayList<FileOutputStream> output = new ArrayList<FileOutputStream>();

        // init in/out stream
        try {
            switch (catg) {
            case 0 :
                in = mParent.getResources().openRawResource(R.raw.calendar_month);

                break;
            case 1 :
                in = mParent.getResources().openRawResource(R.raw.calendar_day);

                break;
            case 2 :
                in = mParent.getResources().openRawResource(R.raw.calendar_event);

                break;
            default :
                in = mParent.getResources().openRawResource(R.raw.calendar_month);

                break;
            }

            DEFALUT_PRINT_HTML = DEFALUT_PRINT + "1" + DEFALUT_HTML;
            out                = mParent.openFileOutput(DEFALUT_PRINT_HTML,
                    Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
            output.add(out);
        } catch (FileNotFoundException e2) {

            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        String         inputLine;

        // Init parameters
        TodayVisible = 0;
        TodayXSet    = 0;
        TodayYSet    = 0;
        findEventId  = 0;
        findTodayId  = 0;
        todayX       = 0;
        todayY       = 0;
        IsTodaySet   = 0;

//      byte[] newline = new byte[2];
//      newline[0] = 0x0d;
//      newline[1] = 0x0a;
        try {
            while ((inputLine = bfr.readLine()) != null) {

                // Process each line.
                inputLine = replaceMonthYear(catg, inputLine, mMonthYear, latMDS, MDS, FDI);
                inputLine = setEventIcon(catg, inputLine, mMonth, mYear, latMDS, MDS, FDI);

                // Log.d(TAG, inputLine);
                out.write(inputLine.getBytes());

                // out.write(newline);
            }
        } catch (IOException e1) {

            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (catg == 0) {

            // Add file URI to list.
            String filePath = String.format("file://%s/%s", mParent.getFilesDir(), DEFALUT_PRINT_HTML);

            default_print_html_file.add(filePath);
        } else if (catg == 1) {              // Day view
            int    Totaleventcount  = application.getEventData().getEventDetailCount();
            int    eventcount       = 0;
            int    nextPageBegin    = 0;
            double nextEventCount   = 0;
            int    DayViewPageCount = 1;

            Log.d(TAG, "Total Event Count:" + Totaleventcount);

            if (Totaleventcount > 6) {       // More then one page, first page contains 6 events
                nextEventCount = Totaleventcount - 6;
                eventcount     = 6;
            } else {
                eventcount = Totaleventcount;
            }

            for (DayViewPageCount = 1; DayViewPageCount <= (Math.ceil(nextEventCount / 13) + 1);
                    DayViewPageCount++) {    // 13 events per page
                if (DayViewPageCount == 1) {
                    for (int i = 0; i < eventcount; i++) {
                        szTmp = application.getEventData().getDetailCalendar(i);
                        Log.d(TAG, "getDetailCalendar:" + szTmp);
                        inputLine = getTodayHTMLCalendar(i, szTmp);

                        // inputLine = inputLine + newline;
                        szTmp = application.getEventData().getDetailTime(i);
                        Log.d(TAG, "getDetailTime:" + szTmp);
                        inputLine = inputLine + getTodayHTMLTime(i, szTmp);

                        // inputLine = inputLine + newline;
                        szTmp = application.getEventData().getDetailEvent(i);

                        // if event too long,
                        int EventWidth = CaluateStringWidth(szTmp);

                        if (EventWidth > 370) {
                            szTmp = ReArrString(szTmp, 370);
                        }

                        // Log.d(TAG, "getDetailEvent:" + szTmp);
                        inputLine = inputLine + getTodayHTMLEvent(i, szTmp);

                        // inputLine = inputLine + newline;
                        try {
                            out.write(inputLine.getBytes());

                            // out.write(newline);
                        } catch (IOException e1) {

                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }

                    try {
                        inputLine = "</body>";

                        // inputLine = inputLine + newline;
                        inputLine = inputLine + "</html>";
                        out.write(inputLine.getBytes());

                        // out.write(newline);
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        out.flush();
                        out.close();
                        in.close();
                    } catch (IOException e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (DayViewPageCount == (Math.ceil(nextEventCount / 13) + 1)) {
                    eventcount = Totaleventcount - 6 - (DayViewPageCount - 2) * 13;

                    // Open calendar_day_02.html
                    try {
                        in                 = mParent.getResources().openRawResource(R.raw.calendar_day_02);
                        DEFALUT_PRINT_HTML = DEFALUT_PRINT + DayViewPageCount + DEFALUT_HTML;
                        out                = mParent.openFileOutput(DEFALUT_PRINT_HTML,
                                Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
                        output.add(out);
                    } catch (FileNotFoundException e2) {

                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                    try {
                        bfr.close();
                    } catch (IOException e2) {

                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                    bfr = new BufferedReader(new InputStreamReader(in));

                    try {
                        while ((inputLine = bfr.readLine()) != null) {

                            // Process each line.
                            inputLine = replaceMonthYear(catg, inputLine, mMonthYear, latMDS, MDS, FDI);

                            // inputLine = setEventIcon(catg, inputLine, mMonth, mYear, latMDS, MDS, FDI);
                            // Log.d(TAG, inputLine);
                            out.write(inputLine.getBytes());

                            // out.write(newline);
                        }
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    for (int i = 0; i < eventcount; i++) {
                        int EventIndex = 6 + (DayViewPageCount - 2) * 13 + i;

                        szTmp = application.getEventData().getDetailCalendar(EventIndex);
                        Log.d(TAG, "getDetailCalendar:" + szTmp);
                        inputLine = getTodayHTMLCalendar(i, szTmp);

                        // inputLine = inputLine + newline;
                        szTmp = application.getEventData().getDetailTime(EventIndex);
                        Log.d(TAG, "getDetailTime:" + szTmp);
                        inputLine = inputLine + getTodayHTMLTime(i, szTmp);

                        // inputLine = inputLine + newline;
                        szTmp = application.getEventData().getDetailEvent(EventIndex);
                        Log.d(TAG, "getDetailEvent:" + szTmp);
                        inputLine = inputLine + getTodayHTMLEvent(i, szTmp);

                        // inputLine = inputLine + newline;
                        try {
                            out.write(inputLine.getBytes());

                            // out.write(newline);
                        } catch (IOException e1) {

                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }

                    try {
                        inputLine = "</body>";

                        // inputLine = inputLine + newline;
                        inputLine = inputLine + "</html>";
                        out.write(inputLine.getBytes());

                        // out.write(newline);
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        out.flush();
                        out.close();
                        in.close();
                    } catch (IOException e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    eventcount = 13;
                    Log.d(TAG, "This page Event Count:" + eventcount);

                    // Open calendar_day_02.html
                    try {
                        in                 = mParent.getResources().openRawResource(R.raw.calendar_day_02);
                        DEFALUT_PRINT_HTML = DEFALUT_PRINT + DayViewPageCount + DEFALUT_HTML;
                        out                = mParent.openFileOutput(DEFALUT_PRINT_HTML,
                                Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
                        output.add(out);
                    } catch (FileNotFoundException e2) {

                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                    try {
                        bfr.close();
                    } catch (IOException e2) {

                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                    bfr = new BufferedReader(new InputStreamReader(in));

                    try {
                        while ((inputLine = bfr.readLine()) != null) {

                            // Process each line.
                            inputLine = replaceMonthYear(catg, inputLine, mMonthYear, latMDS, MDS, FDI);

                            // inputLine = setEventIcon(catg, inputLine, mMonth, mYear, latMDS, MDS, FDI);
                            // Log.d(TAG, inputLine);
                            out.write(inputLine.getBytes());

                            // out.write(newline);
                        }
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    for (int i = 0; i < eventcount; i++) {
                        int EventIndex = 6 + (DayViewPageCount - 2) * 13 + i;

                        szTmp = application.getEventData().getDetailCalendar(EventIndex);
                        Log.d(TAG, "getDetailCalendar:" + szTmp);
                        inputLine = getTodayHTMLCalendar(i, szTmp);

                        // inputLine = inputLine + newline;
                        szTmp = application.getEventData().getDetailTime(EventIndex);
                        Log.d(TAG, "getDetailTime:" + szTmp);
                        inputLine = inputLine + getTodayHTMLTime(i, szTmp);

                        // inputLine = inputLine + newline;
                        szTmp = application.getEventData().getDetailEvent(EventIndex);
                        Log.d(TAG, "getDetailEvent:" + szTmp);
                        inputLine = inputLine + getTodayHTMLEvent(i, szTmp);

                        // inputLine = inputLine + newline;
                        try {
                            out.write(inputLine.getBytes());

                            // out.write(newline);
                        } catch (IOException e1) {

                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }

                    try {
                        inputLine = "</body>";

                        // inputLine = inputLine + newline;
                        inputLine = inputLine + "</html>";
                        out.write(inputLine.getBytes());

                        // out.write(newline);
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        out.flush();
                        out.close();
                        in.close();
                    } catch (IOException e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // Add file URI to list.
                String filePath = String.format("file://%s/%s", mParent.getFilesDir(), DEFALUT_PRINT_HTML);

                default_print_html_file.add(filePath);
            }
        } else if (catg == 2) {    // Event view
            int    nIndex     = application.getEventData().getCurrDayEventSelected();
            String szCalendar = application.getEventData().getDetailCalendar(nIndex);
            String szTime     = application.getEventData().getDetailTime(nIndex);
            String szEvent    = application.getEventData().getDetailEvent(nIndex);
            String szWhere    = application.getEventData().getWhere(nIndex);
            String szMessage  = application.getEventData().getDetailMessage(nIndex);

            // caculate message area
            int EventPageCount = 1;
            int MesgWidth      = CaluateStringWidth(szMessage);

            Log.d(TAG, "Test, get Msg w:" + MesgWidth);

            int     PageBoundary  = FirstPageWidth;
            long    WordCount     = wordcount(szMessage);
            int     MsgLength     = szMessage.length();
            long    tmpWordCount  = 0;
            long    PageWordCount = 0;
            int     tmpLenght     = 0;
            int     tmpWidth      = 0;
            int     StrPosition   = -1;
            long    PrevWordCount = 0;
            int     PositionBegin = 0,
                    PositionEnd   = 0;
            int     AdjustWitdth  = 0;
            String  PreString     = "";
            double  TotalPage     = 0;
            boolean isLastPage    = false;

            if (MsgLength > 0) {
                PositionEnd = MsgLength;
            }

            if (MesgWidth <= FirstPageWidth) {
                TotalPage = 1;
            } else {
                TotalPage = (Math.ceil((double) (MesgWidth - FirstPageWidth) / OtherPageWidth) + 1);
            }

            Log.d(TAG, "TotalPage : " + TotalPage);

            // modify as while!! total page is dynamic!
            // for (EventPageCount = 1; EventPageCount <= TotalPage; EventPageCount++) {
            while (StrPosition < PositionEnd) {
                Log.d(TAG, "EventPageCount: " + EventPageCount);

                if (EventPageCount == 1) {
                    PageBoundary = FirstPageWidth;

                    if (CaluateStringWidth(szMessage.substring(PositionBegin, PositionEnd)) < PageBoundary) {
                        isLastPage = true;
                        Log.d(TAG, "isLastPage!");
                    }

                    if (MsgLength > 0) {
                        if (isLastPage == true) {
                            StrPosition = PositionEnd;
                        } else {
                            for (int w = PositionBegin; w < PositionEnd; w = w + 10) {
                                PreString = szMessage.substring(PositionBegin, w);
                                tmpWidth  = CaluateStringWidth(PreString);

                                if (((FirstPageWidth - 150) < tmpWidth) && (tmpWidth < FirstPageWidth)) {

                                    // find whitespace backward
                                    int idx = w;

                                    while (idx > (w - 25)) {
                                        char c = szMessage.charAt(idx);

                                        if (Character.isWhitespace(c)) {
                                            break;
                                        }

                                        idx--;
                                    }

                                    StrPosition = idx;
                                }
                            }
                        }
                    } else {                        // Msg is empty
                        StrPosition = 0;
                    }

                    String EventMessage = szMessage.substring(PositionBegin, StrPosition);

                    PositionBegin = StrPosition;    // for next page
                    szWhere       = application.getString(R.string.where) + " " + szWhere;

                    // if event too long,
                    int StringWidth = CaluateStringWidth(szEvent);

                    if (StringWidth > 370) {
                        szEvent = ReArrString(szEvent, 370);
                    }

                    StringWidth = CaluateStringWidth(szWhere);

                    if (StringWidth > 370) {
                        szWhere = ReArrString(szWhere, 370);
                    }

                    // Log.d(TAG,"CurrDayEvent:"+ szCalendar+" E:"+szEvent+" T:"+szTime+" M:"+szMessage);
                    inputLine = getEventHTML(szCalendar, szTime, szEvent, szWhere, EventMessage, EventPageCount);
                    EventPageCount++;

                    // inputLine = inputLine + newline;
                    try {
                        out.write(inputLine.getBytes());

                        // out.write(newline);
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        inputLine = "</body>";

                        // inputLine = inputLine + newline;
                        inputLine = inputLine + "</html>";
                        out.write(inputLine.getBytes());

                        // out.write(newline);
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        out.flush();
                        out.close();
                        in.close();
                    } catch (IOException e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    PageBoundary = OtherPageWidth;

                    if (CaluateStringWidth(szMessage.substring(PositionBegin, PositionEnd)) < PageBoundary) {
                        isLastPage = true;
                        Log.d(TAG, "isLastPage!");
                    }

                    Log.d(TAG, "Event print preview page count:" + EventPageCount);

                    // Open calendar_event_02.html
                    try {
                        in                 = mParent.getResources().openRawResource(R.raw.calendar_event_02);
                        DEFALUT_PRINT_HTML = DEFALUT_PRINT + EventPageCount + DEFALUT_HTML;
                        out                = mParent.openFileOutput(DEFALUT_PRINT_HTML,
                                Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
                        output.add(out);
                    } catch (FileNotFoundException e2) {

                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                    try {
                        bfr.close();
                    } catch (IOException e2) {

                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }

                    bfr = new BufferedReader(new InputStreamReader(in));

                    try {
                        while ((inputLine = bfr.readLine()) != null) {

                            // Process each line.
                            inputLine = replaceMonthYear(catg, inputLine, mMonthYear, latMDS, MDS, FDI);

                            // inputLine = setEventIcon(catg, inputLine, mMonth, mYear, latMDS, MDS, FDI);
                            // Log.d(TAG, inputLine);
                            out.write(inputLine.getBytes());

                            // out.write(newline);
                        }
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    if (MsgLength > 0) {
                        if (isLastPage == true) {
                            StrPosition = PositionEnd;
                        } else {
                            for (int w = PositionBegin; w < PositionEnd; w = w + 10) {
                                PreString = szMessage.substring(PositionBegin, w);
                                tmpWidth  = CaluateStringWidth(PreString);

                                if (((OtherPageWidth - 150) < tmpWidth) && (tmpWidth < OtherPageWidth)) {

                                    // find whitespace backward
                                    int idx = w;

                                    while (idx > (w - 25)) {
                                        char c = szMessage.charAt(idx);

                                        if (Character.isWhitespace(c)) {
                                            break;
                                        }

                                        idx--;
                                    }

                                    StrPosition = idx;
                                }
                            }
                        }
                    } else {                        // Msg is empty
                        StrPosition = 0;
                    }

                    String EventMessage = szMessage.substring(PositionBegin, StrPosition);

                    PositionBegin = StrPosition;    // for next page
                    inputLine     = getEventHTML(szCalendar, szTime, szEvent, szWhere, EventMessage, EventPageCount);
                    EventPageCount++;

                    // inputLine = inputLine + newline;
                    try {
                        out.write(inputLine.getBytes());

                        // out.write(newline);
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        inputLine = "</body>";

                        // inputLine = inputLine + newline;
                        inputLine = inputLine + "</html>";
                        out.write(inputLine.getBytes());

                        // out.write(newline);
                    } catch (IOException e1) {

                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        out.flush();
                        out.close();
                        in.close();
                    } catch (IOException e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // Add file URI to list.
                String filePath = String.format("file://%s/%s", mParent.getFilesDir(), DEFALUT_PRINT_HTML);

                default_print_html_file.add(filePath);
            }
        }
    }

    private void createHTMLImage() {
        Log.d(TAG, "createHTMLImage()");
        captureImagetoFile(R.drawable.calendar_h, "calendar_h.jpg");
        captureImagetoFile(R.drawable.calendar_v, "calendar_v.jpg");
        captureImagetoFile(R.drawable.calendar_v1, "calendar_v1.jpg");
        captureImagetoFile(R.drawable.calendar_event, "calendar_event.jpg");
        captureImagetoFile(R.drawable.print_calendar_event, "print_calendar_event.png");
        captureImagetoFile(R.drawable.print_calendar_event_s, "print_calendar_event_s.png");
        captureImagetoFile(R.drawable.calendar_today, "calendar_today.jpg");
        captureImagetoFile(R.drawable.calendar_today_s, "calendar_today_s.jpg");

        Bitmap bmp = Bitmap.createBitmap(720, 1024, Bitmap.Config.ARGB_8888);

        // Bitmap bmp = BitmapFactory.decodeResource(application.getResources(), R.drawable.calendar_v1);
        bmp.eraseColor(0);
        saveBMPtoFile(bmp, "calendar_v2.jpg");
    }

    public void setapplication(CalendarApplication ap) {
        application = ap;
    }

    private String getTodayHTMLCalendar(int i, String szTmp) {
        String ret   = "";
        int    index = i * 3 + 1;

        ret = "<div id=\"content" + index + "\"><div align=\"left\" class=\"style25\">" + szTmp + "</div></div>";
        Log.d(TAG, "getTodayHTMLCalendar:" + ret);

        return ret;
    }

    private String getTodayHTMLTime(int i, String szTmp) {
        String ret   = "";
        int    index = i * 3 + 2;

        ret = "<div id=\"content" + index + "\"><div align=\"right\" class=\"style23\">" + szTmp + "</div></div>";
        Log.d(TAG, "getTodayHTMLTime:" + ret);

        return ret;
    }

    private String getTodayHTMLEvent(int i, String szTmp) {
        String ret   = "";
        int    index = i * 3 + 3;

        ret = "<div id=\"content" + index + "\"><div align=\"left\" class=\"style19\">" + szTmp + "</div></div>";
        Log.d(TAG, "getTodayHTMLEvent:" + ret);

        return ret;
    }

    private String getEventHTML(String szCalendar, String szTime, String szEvent, String szWhere, String szMessage,
                                int PageNo) {
        Log.d(TAG, "getEventHTML begin,");

        String ret     = "";
        String CR      = "";
        byte[] newline = new byte[2];

        newline[0] = 0x0d;
        newline[1] = 0x0a;
        CR         = newline.toString();

        // Handle message format
//      if (szMessage.indexOf(CR)!=-1) {
//              Log.d(TAG,"find CR!!");
//              szMessage.replace(CR, "<br/>");                 
//      }
//      if (szMessage.indexOf("\r\n")!=-1) {
//              Log.d(TAG,"find rn!!");
//              szMessage.replace(CR, "<br/>");                 
//      }
//      if (szMessage.contains("\r")) {
//              Log.d(TAG,"find CR!!r");                        
//      }
        if (szMessage.contains("\n")) {
            Log.d(TAG, "find CR!!n");
            szMessage = szMessage.replace("\n", "<br/>");
        }

        if (PageNo == 1) {
            ret = "<div id=\"content1\"><div align=\"left\" class=\"style25\">" + szCalendar + "</div></div>"
                  + "<div id=\"content2\"><div align=\"right\" class=\"style23\">" + szTime + "</div></div>"
                  + "<div id=\"content3\"><div align=\"left\" class=\"style27\">" + szEvent + "</div></div>"
                  + "<div id=\"content4\"><div align=\"left\" class=\"style27\">" + szWhere + "</div></div>"
                  + "<div id=\"content5\"><div align=\"left\" class=\"style26\">" + szMessage + "</div></div>";
        } else {
            ret = "<div id=\"content4\"><div align=\"left\" class=\"style26\">" + szMessage + "</div></div>";
        }

        // Log.d(TAG, "getEventHTML end,");
        return ret;
    }

    private String replaceMonthYear(int catg, String line, String MY, int latMDS, int MDS, int FDI) {
        String        line1         = line;
        String        imagePath     = mParent.getFilesDir() + "/";
        String        Strtmp        = "";
        int           DayCount      = 1;
        int           DaytoFill     = 0;
        int           id            = 0;
        int           thisMonth     = 1;
        int           nextMonth     = 0;
        int           YY            = application.getSelYear();
        CalendarMonth calendarMonth = new CalendarMonth();
        String        szMonth       = calendarMonth.getMonthName(application.getSelMonth());
        Calendar      caldr         = Calendar.getInstance();

        caldr.set(application.getSelYear(), application.getSelMonth() - 1, application.getSelDay());

        int    nDayOfWeek = (int) (caldr.get(Calendar.DAY_OF_WEEK));
        String szWeekDay  = calendarMonth.getDayOfWeekName(nDayOfWeek);

        szMonth = szMonth + " " + Integer.toString(application.getSelDay());
        line1   = line1.replace("@img:calendar_h@", imagePath + "calendar_h.jpg");
        line1   = line1.replace("@img:calendar_v@", imagePath + "calendar_v.jpg");
        line1   = line1.replace("@img:calendar_v1@", imagePath + "calendar_v1.jpg");
        line1   = line1.replace("@img:calendar_v2@", imagePath + "calendar_v2.jpg");
        line1   = line1.replace("@img:calendar_event@", imagePath + "calendar_event.jpg");
        line1   = line1.replace("@img:print_calendar_event@", imagePath + "print_calendar_event.png");
        line1   = line1.replace("@img:print_calendar_event_s@", imagePath + "print_calendar_event_s.png");
        line1   = line1.replace("@img:calendar_today@", imagePath + "calendar_today.jpg");
        line1   = line1.replace("@img:calendar_today_s@", imagePath + "calendar_today_s.jpg");
        line1   = line1.replace("@id:MonthYear@", MY);
        line1   = line1.replace("@We:ek:day@", szWeekDay);
        line1   = line1.replace("@Sel:MM:DD@", szMonth);
        line1   = line1.replace("@Sel:YY:YY@", Integer.toString(YY));

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Strtmp = "@" + i + ":" + j + "@";    // replace string  @i:j@

                if (i == 0) {                        // first line , handle last month day
                    if (j < FDI) {
                        DaytoFill = latMDS - (FDI - 1) + j;
                        thisMonth = 0;
                    } else {
                        DaytoFill = DayCount;
                        DayCount++;
                        thisMonth = 1;
                    }
                } else {                             // other line
                    if (DayCount <= MDS) {
                        DaytoFill = DayCount;
                        DayCount++;
                        thisMonth = 1;
                    } else {
                        DayCount  = 1;               // next month day
                        DaytoFill = DayCount;
                        DayCount++;
                        nextMonth = 1;
                    }
                }

                if (line1.contains(Strtmp)) {
                    line1 = line1.replace(Strtmp, Integer.toString(DaytoFill));

                    if (catg == 0) {                 // Month view
                        if ((thisMonth == 0) || (nextMonth == 1)) {
                            line1 = line1.replace("class=\"style@:@\"", "class=\"style16\"");
                        } else {
                            line1 = line1.replace("class=\"style@:@\"", "class=\"style11\"");
                        }
                    } else {                         // Day/Event view
                        if ((thisMonth == 0) || (nextMonth == 1)) {
                            line1 = line1.replace("class=\"style@:@\"", "class=\"style20\"");
                        } else {
                            line1 = line1.replace("class=\"style@:@\"", "class=\"style19\"");
                        }
                    }
                }
            }
        }

        return line1;
    }

    private String setEventIcon(int catg, String inputLine, int mMonth, int mYear, int latMDS, int MDS, int FDI) {
        String outline     = inputLine;
        String Strtmp      = "";
        int    DayCount    = 1;
        int    YeartoFill  = mYear;
        int    MonthtoFill = mMonth;
        int    DaytoFill   = 0;
        int    lastMonth   = 0;
        int    nextMonth   = 0;
        int    showToday   = 0;

        // Today
        showToday = todayInMonthView(mMonth, mYear);

        // Log.d(TAG, "showToday:" + showToday + " findTodayId:" + findTodayId);

        if (findTodayId == 1) {

            // Log.d(TAG, outline);
            // update X position
            Strtmp = "left:";

            if (outline.contains(Strtmp) && (TodayXSet == 0)) {
                if (catg == 0) {    // Month view
                    outline = ("    left: " + PrintTable_XPosition[todayX] + "px;");

                    // outline = ("  left:145 px;");
                    // Log.d(TAG, "Month view, set X:" + todayX + "," + outline);
                } else {            // Day and Event view
                    outline = ("    left: " + DayEvent_XPosition[todayX] + "px;");

                    // Log.d(TAG, "Day/Event view, set X:" + todayX + "," + outline);
                }

                TodayXSet = 1;
            }

            // update Y position
            Strtmp = "top:";

            if (outline.contains(Strtmp) && (TodayYSet == 0)) {
                if (catg == 0) {    // Month view
                    outline = ("    top: " + PrintTable_YPosition[todayY] + "px;");

                    // Log.d(TAG, "Month view, set Y:" + todayY + "," + outline);
                } else {            // Day and Event view
                    outline = ("    top: " + DayEvent_YPosition[todayY] + "px;");

                    // Log.d(TAG, "Day/Event view, set Y:" + todayY + "," + outline);
                }

                TodayYSet = 1;
            }

            // update Visible
            if ((showToday > 0) && (IsTodaySet == 1)) {

                // Log.d(TAG, "showTaday: " + showToday);
                // Log.d(TAG, "set as visible: " + outline);
                Strtmp = "hidden";

                if (outline.contains(Strtmp)) {
                    outline      = outline.replace(Strtmp, "visible");
                    TodayVisible = 1;
                    showToday    = 0;

                    // Log.d(TAG, "set as visible: "+outline);
                } else {
                    TodayVisible = 0;
                    showToday    = 0;
                }
            } else {
                Strtmp = "visible";

                if (outline.contains(Strtmp)) {
                    outline      = outline.replace(Strtmp, "hidden");
                    TodayVisible = 0;
                    showToday    = 0;

                    // Log.d(TAG, "set as hidden");
                } else {
                    TodayVisible = 0;
                    showToday    = 0;
                }
            }

            // Log.d(TAG, "TodayXSet:" + TodayXSet + " TodayYSet:" + TodayYSet + " TodayVisible:" + TodayVisible);
            // reset findTodayId
            if ((TodayXSet == 1) && (TodayYSet == 1) && (TodayVisible == 1)) {
                findTodayId = 0;
            }
        }

        if (outline.compareTo("#today {") == 0) {
            Log.d(TAG, " Find today tag!!");
            findTodayId = 1;
        }

        // Event
        // Log.d(TAG, "findEventId:" + findEventId + " EventStatus:" + EventStatus);
        if ((findEventId == 1) && (EventStatus == 1)) {

            // Log.d(TAG, outline);
            Strtmp = "hidden";

            if (outline.contains(Strtmp)) {
                outline     = outline.replace(Strtmp, "visible");
                EventStatus = 0;
                findEventId = 0;
            }
        } else if ((findEventId == 1) && (EventStatus == 0)) {
            Strtmp = "visible";

            if (outline.contains(Strtmp)) {
                outline     = outline.replace(Strtmp, "hidden");
                EventStatus = 0;
                findEventId = 0;
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Strtmp = "#Event" + i + j + " {";    // #Event00 {

                if (i == 0) {                        // first line , handle last month day
                    if (j < FDI) {                   // last month
                        if (mMonth == 1) {
                            MonthtoFill = 12;
                            YeartoFill  = mYear - 1;;
                        } else {
                            MonthtoFill = mMonth - 1;
                            YeartoFill  = mYear;
                        }

                        DaytoFill = latMDS - (FDI - 1) + j;
                    } else {                         // this month
                        YeartoFill  = mYear;
                        MonthtoFill = mMonth;
                        DaytoFill   = DayCount;
                        DayCount++;
                    }
                } else {                             // other line
                    if (DayCount <= MDS) {
                        if (nextMonth == 1) {        // nextmonth
                            if (mMonth == 12) {
                                MonthtoFill = 1;
                                YeartoFill  = mYear + 1;
                            } else {

                                // Log.d(TAG,"Month ++!!");
                                MonthtoFill = mMonth + 1;
                                YeartoFill  = mYear;
                            }

                            DaytoFill = DayCount;
                            DayCount++;
                        } else {    // this month
                            YeartoFill  = mYear;
                            MonthtoFill = mMonth;
                            DaytoFill   = DayCount;
                            DayCount++;
                        }
                    } else {
                        if (mMonth == 12) {
                            MonthtoFill = 1;
                            YeartoFill  = mYear + 1;
                        } else {

                            // Log.d(TAG,"Month ++!!");
                            MonthtoFill = mMonth + 1;
                            YeartoFill  = mYear;
                        }

                        nextMonth = 1;
                        DayCount  = 1;    // next month day
                        DaytoFill = DayCount;
                        DayCount++;
                    }
                }

                if (outline.compareTo(Strtmp) == 0) {
                    findEventId = 1;

                    // Log.d(TAG, "findEventId-> " + outline + " M: " + mMonth + " M2F: " + MonthtoFill);

                    if (checkEventStatus(YeartoFill, MonthtoFill, DaytoFill)) {
                        EventStatus = 1;

                        // Log.d(TAG, "EventStatus-> " + outline);
                    }
                }

                if (IsToday(YeartoFill, MonthtoFill, DaytoFill)) {
                    todayX     = j;
                    todayY     = i;
                    IsTodaySet = 1;

                    // Log.d(TAG, "todayX: " + todayX + " todayY: " + todayY);
                }
            }
        }

        return outline;
    }

    private int todayInMonthView(int mMonth, int mYear) {
        int thisYear  = application.getYearOfToday();
        int thisMonth = application.getMonthOfToday();

        // Log.d(TAG,"todayInMonthView, Y:"+mYear+"M:"+mMonth+"tY:"+thisYear+"tM:"+thisMonth);
        if ((mMonth == 12) && (thisMonth == 1) && (thisYear == (mYear + 1))) {
            return 1;
        } else if ((mMonth == 1) && (thisMonth == 12) && (thisYear == (mYear - 1))) {
            return 1;
        } else if (((mMonth - 1) <= thisMonth) && (thisMonth <= (mMonth + 1)) && (thisYear == mYear)) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean checkEventStatus(int Year, int Month, int Day) {
        if (application.isEventDate(Year, Month, Day)) {

            // Log.d(TAG, "Y: " + Year + "M: " + Month + "D: " + Day);
            return true;
        } else {
            return false;
        }
    }

    private boolean IsToday(int YeartoFill, int MonthtoFill, int DaytoFill) {
        if ((application.getYearOfToday() == YeartoFill) && (application.getMonthOfToday() == MonthtoFill)
                && (application.getDayOfToday() == DaytoFill)) {
            return true;
        } else {
            return false;
        }
    }

    private int CaluateStringWidth(String szMessage) {
        if (szMessage.length() == 0) {
            return 0;
        }

        TextPaint tp   = new TextPaint();
        Rect      rect = new Rect();

        tp.getTextBounds(szMessage, 0, szMessage.length(), rect);

        return rect.width();
    }

    private static long wordcount(String line) {
        long    numWords       = 0;
        int     index          = 0;
        boolean prevWhiteSpace = true;

        while (index < line.length()) {
            char    c              = line.charAt(index++);
            boolean currWhiteSpace = Character.isWhitespace(c);

            if (prevWhiteSpace &&!currWhiteSpace) {
                numWords++;
            }

            prevWhiteSpace = currWhiteSpace;
        }

        return numWords;
    }

    private int getWordCountPosition(String Text, long WordCount) {
        int    lentgth   = Text.length();
        String tmpString = "";

        if (WordCount >= wordcount(Text)) {
            return lentgth;
        }

        for (int w = lentgth; w > 0; w--) {
            tmpString = Text.substring(0, w);

            if (WordCount == wordcount(tmpString)) {
                return w;
            }
        }

        return 0;
    }

    private String ReArrString(String data, int dim) {
        int    length = data.length();
        String tmp    = "";
        int    index  = length;

        if ((dim < 0) || (dim >= CaluateStringWidth(data))) {
            return data;
        } else {
            while (index >= 0) {
                tmp = data.substring(0, index);

                if (CaluateStringWidth(tmp) < dim) {
                    tmp = tmp + "...";

                    return tmp;
                } else {
                    index = index - 10;
                }
            }

            return tmp;
        }
    }
}

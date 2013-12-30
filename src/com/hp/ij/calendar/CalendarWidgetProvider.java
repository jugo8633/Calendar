//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.PendingIntent;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;

import android.content.Context;
import android.content.Intent;

import android.graphics.Rect;

import android.os.Bundle;

import android.text.TextPaint;

import android.view.View;
import android.view.WindowManager;

import android.widget.RemoteViews;

import com.hp.ij.calendar.handler.CalendarBroadcaster;

public class CalendarWidgetProvider extends AppWidgetProvider {
    private static String[]     EventName            = null;
    private static String[]     EventTask            = null;
    private static String[]     EventTime            = null;
    private static final String TAG                  = "CalendarWidgetProvider";
    private final int           ORIENTATION_LAND     = 0;
    private final int           ORIENTATION_PORT     = 3;
    private final int           TEXT_VIEW_WIDTH_LAND = 400;
    private final int           TEXT_VIEW_WIDTH_PORT = 400;
    private int                 mOrientation         = -1;
    private int                 mTextViewWidth       = 0;
    private RemoteViews         views                = null;
    private String              szDeafultCaption     = null;;

    /**
     * widget view id
     */
    private final int[] landItem1 = { R.id.tvEventTask0, R.id.tvEventTask1, R.id.tvEventTask2, R.id.tvEventTask3,
                                      R.id.tvEventTask4 };
    private final int[] landItem2 = { R.id.tvEventTask0a, R.id.tvEventTask1a, R.id.tvEventTask2a, R.id.tvEventTask3a,
                                      R.id.tvEventTask4a };
    private final int[] portItem1 = { R.id.tvEventTaskV0, R.id.tvEventTaskV1, R.id.tvEventTaskV2, R.id.tvEventTaskV3,
                                      R.id.tvEventTaskV4 };
    private final int[] portItem2 = { R.id.tvEventTaskV0a, R.id.tvEventTaskV1a, R.id.tvEventTaskV2a,
                                      R.id.tvEventTaskV3a, R.id.tvEventTaskV4a };
    private final int[] portItem3 = { R.id.tvEventTaskV0b, R.id.tvEventTaskV1b, R.id.tvEventTaskV2b,
                                      R.id.tvEventTaskV3b, R.id.tvEventTaskV4b };

    /**
     * call back when widget create, rotate or reinstall application will not call
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i], CalendarWidgetBroadcast.WIDGET_DEFAULT);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
        broadcastRequest(context, CalendarBroadcaster.WIDGET_ON_UPDATE);
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, int nMode) {
        views = new RemoteViews(context.getPackageName(), R.layout.calendar_widget_layout);

        if ((null == views) || (null == appWidgetManager)) {
            return;
        }

        szDeafultCaption = context.getString(R.string.no_more_events_today);
        clearWidgetView(views);

        /**
         * set widget to launch CalendarApp
         */
        Intent        intApp       = new Intent(context, CalendarApp.class);
        PendingIntent launchIntent = PendingIntent.getActivity(context, 0, intApp, 0);

        views.setOnClickPendingIntent(R.id.llWidgetMain, launchIntent);
        setCalendarEventTitle(views);

        /**
         *  update widget view
         */
        switch (nMode) {
        case CalendarWidgetBroadcast.CALENDAR_UPDATE :
            views.setViewVisibility(R.id.llWidgetDefaultMain, View.GONE);
            views.setViewVisibility(R.id.rlWidgetCalendarMain, View.VISIBLE);
            setEventData(views);

            break;
        default :
            EventName = null;
            EventTask = null;
            EventTime = null;
            views.setViewVisibility(R.id.rlWidgetCalendarMain, View.GONE);
            views.setViewVisibility(R.id.llWidgetDefaultMain, View.VISIBLE);

            break;
        }

        /**
         * reflash widget view
         */
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * when widget create, onReceive will receive ACTION_APPWIDGET_UPDATE
     * ( intent.getAction() = ACTION_APPWIDGET_UPDATE)
     * and call back onUpdate function
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        /**
         * calendar service broadcast EXTRA_CUSTOM_EXTRAS to update widget
         */
        if (AppWidgetManager.EXTRA_CUSTOM_EXTRAS.equals(action)) {
            Bundle bunde = intent.getExtras();

            if (0 < bunde.getInt("id")) {
                switch (bunde.getInt("id")) {
                case CalendarWidgetBroadcast.WIDGET_DEFAULT :
                case CalendarWidgetBroadcast.CALENDAR_LOGOUT :
                    updateWidget(context, CalendarWidgetBroadcast.WIDGET_DEFAULT);

                    break;
                case CalendarWidgetBroadcast.CALENDAR_UPDATE :
                    getExtraData(bunde);
                    updateWidget(context, CalendarWidgetBroadcast.CALENDAR_UPDATE);

                    break;
                }
            }
        }

        super.onReceive(context, intent);
    }

    private void updateWidget(Context context, int nMode) {
        int[] listIds = getWidgetIds(context, ".CalendarWidgetProvider");

        if (listIds != null) {
            for (int i = 0; i < listIds.length; i++) {
                updateAppWidget(context, AppWidgetManager.getInstance(context), listIds[i], nMode);
            }
        }
    }

    private void getExtraData(Bundle bunde) {
        EventName = new String[5];
        EventTask = new String[5];
        EventTime = new String[5];

        for (int i = 0; i < 5; i++) {
            if (null == bunde.getString("Name" + i)) {
                break;
            }

            String[] tmpAry = null;

            EventName[i] = bunde.getString("Name" + i);
            EventTask[i] = bunde.getString("Task" + i);
            tmpAry       = bunde.getString("Time" + i).split("-");

            if ((null != tmpAry) && (tmpAry.length >= 1)) {
                if (tmpAry[0].equals("0 AM")) {
                    EventTime[i] = "12 AM";
                } else if (tmpAry[0].equals("0:15 AM")) {
                    EventTime[i] = "12:15 AM";
                } else if (tmpAry[0].equals("0:30 AM")) {
                    EventTime[i] = "12:30 AM";
                } else if (tmpAry[0].equals("0:45 AM")) {
                    EventTime[i] = "12:45 AM";
                } else if (tmpAry[0].equals("0 PM")) {
                    EventTime[i] = "12 PM";
                } else if(tmpAry[0].equals("0:15 PM")){
                	EventTime[i] = "12:15 PM";
                } else if (tmpAry[0].equals("0:30 PM")) {
                    EventTime[i] = "12:30 PM";
                } else if(tmpAry[0].equals("0:45 PM")){
                	EventTime[i] = "12:45 PM";
                }else {
                    EventTime[i] = tmpAry[0];
                }
            }
        }

        sortEventDetail();
    }

    private void sortEventDetail() {
        String szTmp1    = null;
        String szTmp2    = null;
        int    nMinutes1 = 0;
        int    nMinutes2 = 0;

        for (int i = 0; i < CalendarWidgetBroadcast.mnMaxEventCount; i++) {
            if (null == EventName[i]) {
                break;
            }

            for (int j = 0; j < CalendarWidgetBroadcast.mnMaxEventCount; j++) {
                szTmp1 = EventTime[i];
                szTmp2 = EventTime[j];

                if ((null == szTmp1) || (null == szTmp2)) {
                    break;
                }

                nMinutes1 = getMinutes(szTmp1);
                nMinutes2 = getMinutes(szTmp2);

                if (nMinutes1 < nMinutes2) {
                    switchData(i, j);
                }
            }
        }
    }

    private int getMinutes(String szTime) {
        if (null == szTime) {
            return 0;
        }

        String szTimeOri = szTime;
        int    nMinutes  = 0;
        int    nHours    = 0;

        szTimeOri.trim();

        String[] tmpAry = null;

        tmpAry = szTimeOri.split(" ");

        if ((null != tmpAry[0]) && (null != tmpAry[1])) {
            if (tmpAry[1].equals("PM")) {
                nMinutes += 720;          // 12 * 60 = 720
            }

            String   szTimeOfDay = tmpAry[0];
            String[] tmpAry2     = szTimeOfDay.split(":");

            if (tmpAry2.length == 2) {    // have minutes
                int nTmp = Integer.valueOf(tmpAry2[1]);

                nMinutes += nTmp;
            }

            nHours = Integer.valueOf(tmpAry2[0]);

            if (12 == nHours) {
                nHours = 0;
            }

            nMinutes = nMinutes + (nHours * 60);
        }

        return nMinutes;
    }

    private void switchData(int nIndex1, int nIndex2) {
        String EventNameTmp = null;
        String EventTaskTmp = null;
        String EventTimeTmp = null;

        EventNameTmp       = EventName[nIndex1];
        EventTaskTmp       = EventTask[nIndex1];
        EventTimeTmp       = EventTime[nIndex1];
        EventName[nIndex1] = EventName[nIndex2];
        EventTask[nIndex1] = EventTask[nIndex2];
        EventTime[nIndex1] = EventTime[nIndex2];
        EventName[nIndex2] = EventNameTmp;
        EventTask[nIndex2] = EventTaskTmp;
        EventTime[nIndex2] = EventTimeTmp;
        EventNameTmp       = null;
        EventTaskTmp       = null;
        EventTimeTmp       = null;
    }

    private String getMappingMonth(int month) {
        switch (month) {
        case Calendar.JANUARY :
            return "Jan";
        case Calendar.FEBRUARY :
            return "Feb";
        case Calendar.MARCH :
            return "Mar";
        case Calendar.APRIL :
            return "Apr";
        case Calendar.MAY :
            return "May";
        case Calendar.JUNE :
            return "Jun";
        case Calendar.JULY :
            return "Jul";
        case Calendar.AUGUST :
            return "Aug";
        case Calendar.SEPTEMBER :
            return "Sep";
        case Calendar.OCTOBER :
            return "Oct";
        case Calendar.NOVEMBER :
            return "Nov";
        case Calendar.DECEMBER :
            return "Dec";
        default :
            return "Err";
        }
    }

    private String getMappingWeek(int Week) {
        switch (Week) {
        case Calendar.SUNDAY :
            return "Sun";
        case Calendar.MONDAY :
            return "Mon";
        case Calendar.TUESDAY :
            return "Tue";
        case Calendar.WEDNESDAY :
            return "Wed";
        case Calendar.THURSDAY :
            return "Thu";
        case Calendar.FRIDAY :
            return "Fri";
        case Calendar.SATURDAY :
            return "Sat";
        default :
            return "Err";
        }
    }

    private void setEventData(RemoteViews views) {
        if ((null == views) || (null == EventName)) {
            return;
        }

        setCalendarEventTitle(views);

        if (null != EventName[0]) {
            views.setTextViewText(R.id.tvEventName0, EventName[0]);
            views.setTextViewText(R.id.tvEventTime0, EventTime[0]);
            setLandEventData(0);
            setPortEventData(0);
        }

        if (null != EventName[1]) {
            views.setTextViewText(R.id.tvEventName1, EventName[1]);
            views.setTextViewText(R.id.tvEventTime1, EventTime[1]);
            setLandEventData(1);
            setPortEventData(1);
        }

        if (null != EventName[2]) {
            views.setTextViewText(R.id.tvEventName2, EventName[2]);
            views.setTextViewText(R.id.tvEventTime2, EventTime[2]);
            setLandEventData(2);
            setPortEventData(2);
        }

        if (null != EventName[3]) {
            views.setTextViewText(R.id.tvEventName3, EventName[3]);
            views.setTextViewText(R.id.tvEventTime3, EventTime[3]);
            setLandEventData(3);
            setPortEventData(3);
        }

        if (null != EventName[4]) {
            views.setTextViewText(R.id.tvEventName4, EventName[4]);
            views.setTextViewText(R.id.tvEventTime4, EventTime[4]);
            setLandEventData(4);
            setPortEventData(4);
        }
    }

    private void setLandEventData(int nIndex) {
        ArrayList<String> arrStr = new ArrayList<String>();

        arrStr = separateString(190, 2, EventTask[nIndex]);

        if ((null != arrStr) && (0 < arrStr.size())) {
            if (null != arrStr.get(0)) {
                views.setTextViewText(landItem1[nIndex], arrStr.get(0));
            }

            if ((2 <= arrStr.size()) && (null != arrStr.get(1))) {
                views.setTextViewText(landItem2[nIndex], arrStr.get(1));
            }
        }

        arrStr = null;
    }

    private void setPortEventData(int nIndex) {
        ArrayList<String> arrStr = new ArrayList<String>();

        arrStr = separateString(130, 3, EventTask[nIndex]);

        if ((null != arrStr) && (0 < arrStr.size())) {
            if (null != arrStr.get(0)) {
                views.setTextViewText(portItem1[nIndex], arrStr.get(0));
            }

            if ((2 <= arrStr.size()) && (null != arrStr.get(1))) {
                views.setTextViewText(portItem2[nIndex], arrStr.get(1));
            }

            if ((3 <= arrStr.size()) && (null != arrStr.get(2))) {
                views.setTextViewText(portItem3[nIndex], arrStr.get(2));
            }
        }

        arrStr = null;
    }

    private void setCalendarEventTitle(RemoteViews views) {
        StringBuilder currentDate = new StringBuilder();
        Calendar      rightNow    = Calendar.getInstance();

        currentDate.append(getMappingWeek(rightNow.get(Calendar.DAY_OF_WEEK)));
        currentDate.append(", ");
        currentDate.append(getMappingMonth(rightNow.get(Calendar.MONTH)));
        currentDate.append(" ");
        currentDate.append(rightNow.get(Calendar.DAY_OF_MONTH));
        currentDate.append(", ");
        currentDate.append(rightNow.get(Calendar.YEAR));
        views.setViewVisibility(R.id.tvCalendarEventTitle, View.VISIBLE);
        views.setTextViewText(R.id.tvCalendarEventTitle, currentDate);
        currentDate = null;
        rightNow    = null;
    }

    private int[] getWidgetIds(Context context, String szClassName) {
        AppWidgetManager gm = AppWidgetManager.getInstance(context);

        if (null != gm) {
            List<AppWidgetProviderInfo> listg = gm.getInstalledProviders();

            for (int k = 0; k < listg.size(); k++) {
                AppWidgetProviderInfo providerInfo = listg.get(k);

                if (providerInfo.provider.getShortClassName().equals(".CalendarWidgetProvider")) {
                    return gm.getAppWidgetIds(providerInfo.provider);
                }
            }
        }

        return null;
    }

    private int getOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mOrientation = -1;

        if (null != windowManager) {
            mOrientation = windowManager.getDefaultDisplay().getOrientation();

            switch (mOrientation) {
            case ORIENTATION_LAND :
                mTextViewWidth = TEXT_VIEW_WIDTH_LAND;

                break;
            case ORIENTATION_PORT :
                mTextViewWidth = TEXT_VIEW_WIDTH_PORT;

                break;
            }
        }

        return mOrientation;
    }

    /**
     *     calculate the width of the text in pixel
     *
     *     @return the width in pixels
     */
    private int calculateStrWidth(String szText) {
        if (null == szText) {
            return -1;
        }

        TextPaint tp   = new TextPaint();
        Rect      rect = new Rect();

        tp.getTextBounds(szText, 0, szText.length(), rect);

        int textWidth = rect.width();

        rect = null;
        tp   = null;

        return textWidth;
    }

    /**
     * format string that add ellipsis
     * @param szText
     * @param nMaxWidth
     * @return string that add ellipsis at end
     */
    private String addEllipsis(String szText, int nMaxWidth) {
        if ((null == szText) || (0 == szText.length()) || (nMaxWidth == 0)) {
            return szText;
        }

        int nTextWidth = 0;

        /**
         * "..." pixel width is 9
         */
        int nMaxTextWidth = nMaxWidth - 9;

        nTextWidth = calculateStrWidth(szText);

        if (nMaxWidth >= nTextWidth) {
            return szText;
        }

        int nTextLength = 0;

        nTextLength = szText.length();

        String szReSizeText = null;

        for (int nLength = (nTextLength - 1); nLength > 0; nLength--) {
            szReSizeText = szText.substring(0, nLength);
            nTextWidth   = calculateStrWidth(szReSizeText);

            if (nMaxTextWidth >= nTextWidth) {
                break;
            }
        }

        StringBuffer strBuf = null;

        if (null != szReSizeText) {
            strBuf = new StringBuffer(szReSizeText);
            strBuf.append("...");
        }

        if (null != strBuf) {
            szReSizeText = strBuf.toString();
        }

        return szReSizeText;
    }

    private ArrayList<String> separateString(int nWidth, int nLine, String szStr) {
        ArrayList<String> arrStr = new ArrayList<String>();

        try {
            if (null == szStr) {
                return null;
            }

            int    nPixLen      = calculateStrWidth(szStr);
            String szReSizeText = null;

            if (nWidth >= nPixLen) {
                arrStr.add(szStr);
            } else {
                int nStrLen = 0;

                nStrLen = getAdjustLength(nWidth, szStr);

                if (2 == nLine) {
                    szReSizeText = szStr.substring(0, nStrLen);
                    arrStr.add(szReSizeText);    // 1st line
                    szReSizeText = szStr.substring(nStrLen);
                    arrStr.add(szReSizeText);    // 2st line

                    return arrStr;
                }

                if (3 == nLine) {
                    szReSizeText = szStr.substring(0, nStrLen);
                    arrStr.add(szReSizeText);    // 1st line
                    szReSizeText = szStr.substring(nStrLen);
                    nPixLen      = calculateStrWidth(szReSizeText);

                    if (nWidth >= nPixLen) {
                        arrStr.add(szStr);       // 2st line

                        return arrStr;
                    } else {
                        nStrLen = getAdjustLength(nWidth, szReSizeText);

                        String szTmp = szReSizeText.substring(0, nStrLen);

                        arrStr.add(szTmp);       // 2st line
                        szTmp = szReSizeText.substring(nStrLen);
                        arrStr.add(szTmp);       // 3st line

                        return arrStr;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrStr;
    }

    private int getAdjustLength(int nPixWidth, String szStr) {
        if ((null == szStr) || (0 == nPixWidth)) {
            return 0;
        }

        int nPixLen = calculateStrWidth(szStr);

        if (nPixWidth >= nPixLen) {
            return szStr.length();
        }

        String szReSizeText = null;

        for (int nLength = (szStr.length() - 1); nLength > 0; nLength--) {
            szReSizeText = szStr.substring(0, nLength);
            nPixLen      = calculateStrWidth(szReSizeText);

            if (nPixWidth >= nPixLen) {
                return nLength;
            }
        }

        return 0;
    }

    private void clearWidgetView(RemoteViews views) {
        if (null == views) {
            return;
        }

        views.setTextViewText(R.id.tvEventName0, null);
        views.setTextViewText(R.id.tvEventTime0, null);
        views.setTextViewText(R.id.tvEventName1, null);
        views.setTextViewText(R.id.tvEventTime1, null);
        views.setTextViewText(R.id.tvEventName2, null);
        views.setTextViewText(R.id.tvEventTime2, null);
        views.setTextViewText(R.id.tvEventName3, null);
        views.setTextViewText(R.id.tvEventTime3, null);
        views.setTextViewText(R.id.tvEventName4, null);
        views.setTextViewText(R.id.tvEventTime4, null);

        for (int i = 0; i < landItem1.length; i++) {
            views.setTextViewText(landItem1[i], szDeafultCaption);
            views.setTextViewText(landItem2[i], null);
            views.setTextViewText(portItem1[i], szDeafultCaption);
            views.setTextViewText(portItem2[i], null);
            views.setTextViewText(portItem3[i], null);
        }
    }

    private void broadcastRequest(Context context, int nAction) {
        Intent i = new Intent(CalendarBroadcaster.WIDGET_REQUEST);

        i.putExtra(CalendarBroadcaster.ACTION, nAction);
        context.sendBroadcast(i);
        i = null;
    }
}

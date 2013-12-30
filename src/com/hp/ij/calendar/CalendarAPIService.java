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
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import androidx.gdata.Recurrence;
import androidx.gdata.Reminder;

import com.hp.ij.common.ccphttpclient.CCPHttpClient;
import com.hp.ij.common.service.client.GeneralCommand;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.client.ServiceAdapter;
import com.hp.ij.common.service.gdata.client.calendar.AllEvent;
import com.hp.ij.common.service.gdata.client.calendar.BundleDeleteReturn;
import com.hp.ij.common.service.gdata.client.calendar.BundleLogin;
import com.hp.ij.common.service.gdata.client.calendar.CalendarFeed;
import com.hp.ij.common.service.gdata.client.calendar.Event;
import com.hp.ij.common.service.gdata.client.calendar.SingleDayEvent;

import frame.event.EventMessage;

public class CalendarAPIService {
    private static final String TAG = "CalendarAPIService";
    private static int          intToken;
    private CalendarApplication application                  = null;
    private Calendar            calendarEnd                  = null;
    private Calendar            calendarStart                = null;
    private Handler             eventHandler                 = null;
    private GeneralCommand      generalCommand               = null;
    private Context             mContext                     = null;
    AllEvent                    mallEvent                    = null;
    private String              mszDate                      = null;
    private ServiceAdapter      serviceAdapter               = null;
    private Thread              thdRequest                   = null;
    private ArrayList<String>   tempAryHeaderName            = new ArrayList<String>();
    private ArrayList<String>   tempAryContent               = new ArrayList<String>();
    private BroadcastReceiver   BroadcastReceiverFromService = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            String szTemp = null;

            if (intent.getAction().compareTo(IntentCommand.INTENT_ACTION) != 0) {
                return;
            }

            switch (intent.getFlags()) {
            case IntentCommand.GOOGLE_CALENDAR_LOG_AUTH :
                BundleLogin bundleLogin = (BundleLogin) intent.getSerializableExtra("Data");

                if (bundleLogin.getLogin()) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_LOGIN, "SUCCESS");
                } else {
                    switch (bundleLogin.getIntResultCode()) {
                    case CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT :    // -25
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_LOGIN, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_4XX :            // -35
                        szTemp = mContext.getString(R.string.No_Google_Calendar_Content_Found);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_LOGIN, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_5XX :            // -36
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Google_Calendar);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_LOGIN, szTemp);

                        break;
                    case -37 :                                         // can't recognize in CCPHttpClient, so we define another error code
                        szTemp = mContext.getString(R.string.Unable_2_recognize_username_password);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_LOGIN, szTemp);

                        break;
                    default :
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_LOGIN, szTemp);

                        break;
                    }
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_RETRIEVE_CALENDAR :
                CalendarFeed calendarFeed = (CalendarFeed) intent.getSerializableExtra("Data");

                if (calendarFeed.ismBoolValid()) {
                    String szTitle = null;
                    String szMid   = null;

                    application.getEventData().clearCalendar();

                    for (int i = 0; i < calendarFeed.getmCalendars().length; i++) {
                        szTitle = calendarFeed.getmCalendars()[i].getmTitle();
                        szMid   = calendarFeed.getmCalendars()[i].getmId();
                        application.getEventData().addCalendar(szTitle, szMid, true);
                    }

                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_CALENDAR,
                               String.valueOf(calendarFeed.getmCalendars().length));
                } else {
                    switch (calendarFeed.getmIntSC()) {
                    case CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT :    // -25
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_CALENDAR, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_4XX :            // -35
                        szTemp = mContext.getString(R.string.No_Google_Calendar_Content_Found);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_CALENDAR, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_5XX :            // -36
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Google_Calendar);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_CALENDAR, szTemp);

                        break;
                    default :
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_CALENDAR, szTemp);

                        break;
                    }
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT :
                SingleDayEvent singleDayEvent = (SingleDayEvent) intent.getSerializableExtra("Data");

                if ((null == singleDayEvent) || (null == singleDayEvent.getmEventFeeds())) {
                    szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT, szTemp);

                    return;
                }

                if (singleDayEvent.ismBoolValid()) {
                    int nErrorCode = singleDayEvent.getmIntSC();

                    switch (nErrorCode) {
                    case CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT :    // -25
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_4XX :            // -35
                        szTemp = mContext.getString(R.string.No_Google_Calendar_Content_Found);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_5XX :            // -36
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Google_Calendar);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT, szTemp);

                        break;
                    default :
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT, szTemp);

                        break;
                    }

                    return;
                }

                application.clearEventDetailData();

                String     szId              = null,
                           szCalendar        = null,
                           szEvent           = null,
                           szTime            = null,
                           szWhere           = null,
                           szReminder        = "",
                           szMessage         = null;
                int        nEventDetailCount = 0;
                Reminder[] remider           = null;
                int        adjTimeZone       = 0;
                boolean    bIsAllDay         = false;
                boolean    bIsAllDayTime     = false;
                boolean    bIsEditAble       = true;

                for (int i = 0; i < singleDayEvent.getmEventFeeds().length; i++) {
                    for (int j = 0; j < singleDayEvent.getmEventFeeds()[i].getEvents().length; j++) {
                        remider = null;

                        try {
                            szId        = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getId();
                            szCalendar  = singleDayEvent.getmEventFeeds()[i].getTitle();
                            szEvent     = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getTitle();
                            bIsEditAble = singleDayEvent.getmEventFeeds()[i].getEvents()[j].isBoolIsEdited();

                            String firstTime = null,
                                   endTime   = null;

                            /**
                             * time format:
                             * all day: yyyy-mm-dd
                             * not all day: yyyy-mm-ddTHH:MM:SS.000+00:00
                             */
                            firstTime = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhen().getStartTime();
                            endTime   = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhen().getEndTime();

                            if ((firstTime.length() <= 10) && (endTime.length() <= 10)) {
                                bIsAllDay = true;
                            } else {
                                bIsAllDay = false;
                            }

                            String[] tmpAry = null;

                            tmpAry      = firstTime.split("T");
                            adjTimeZone = adjustTimeZone(tmpAry);

                            if (tmpAry.length >= 2) {
                                firstTime = tmpAry[1];
                                tmpAry    = firstTime.split(".000");    // Split +08:00

                                if (tmpAry.length >= 2) {
                                    firstTime = tmpAry[0];

                                    /**
                                     * if start date time format is not yyyy-mm-dd, check time
                                     */
                                    if (!bIsAllDay) {
                                        if (firstTime.equals("00:00:00")) {
                                            bIsAllDayTime = true;
                                        } else {
                                            bIsAllDayTime = false;
                                        }
                                    }

                                    firstTime = firstTime.substring(0, 5);
                                } else {
                                    firstTime = "";                     // assign as All day later
                                }
                            } else {
                                firstTime = "";                         // assign as All day later
                            }

                            int adjustStartDate = adjustDate_TimeZone(firstTime, adjTimeZone);

                            firstTime = adjust24HTimeZone(firstTime, adjTimeZone);
                            tmpAry    = endTime.split("T");

                            if (tmpAry.length >= 2) {
                                endTime = tmpAry[1];
                                tmpAry  = endTime.split(".000");        // Split +08:00

                                if (tmpAry.length >= 2) {
                                    endTime = tmpAry[0];

                                    if (!bIsAllDay) {
                                        if (endTime.equals("23:59:59") && bIsAllDayTime) {
                                            bIsAllDay = true;
                                        }
                                    }

                                    endTime = endTime.substring(0, 5);
                                } else {
                                    endTime = "";                       // assign as All day later
                                }
                            } else {
                                endTime = "";                           // assign as All day later
                            }

                            int adjustEndDate = adjustDate_TimeZone(endTime, adjTimeZone);;

                            endTime = adjust24HTimeZone(endTime, adjTimeZone);

                            if (bIsAllDay) {
                                szTime = mContext.getString(R.string.all_day);
                            } else {
                                szTime = switchAMPM(firstTime) + " - " + switchAMPM(endTime);
                            }

                            szWhere   = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhere().getValueString();
                            szMessage = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getContent();
                            remider   = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhen().getReminders();

                            Date startDate = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhen().getDateStart();
                            Date endDate   = singleDayEvent.getmEventFeeds()[i].getEvents()[j].getWhen().getDateEnd();

                            calendarStart = Calendar.getInstance();
                            calendarEnd   = Calendar.getInstance();
                            calendarStart.setTime(startDate);
                            calendarEnd.setTime(endDate);
                            calendarStart.add(Calendar.DAY_OF_YEAR, adjustStartDate);
                            calendarEnd.add(Calendar.DAY_OF_YEAR, adjustEndDate);

                            String szTmp = null;

                            if (!((firstTime == null) || (firstTime == ""))) {
                                try {
                                    szTmp = firstTime.substring(0, 2);
                                    calendarStart.set(Calendar.HOUR_OF_DAY, Integer.valueOf(szTmp));
                                    szTmp = firstTime.substring(3);
                                    calendarStart.set(Calendar.MINUTE, Integer.valueOf(szTmp));
                                } catch (IndexOutOfBoundsException e) {}
                            }

                            if (!((endTime == null) || (endTime == ""))) {
                                try {
                                    szTmp = endTime.substring(0, 2);
                                    calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(szTmp));
                                    szTmp = endTime.substring(3);
                                    calendarEnd.set(Calendar.MINUTE, Integer.valueOf(szTmp));
                                } catch (IndexOutOfBoundsException e) {}
                            }

                            if (remider != null) {
                                if (remider[0] != null) {
                                    if (remider[0].isHasReminder()) {

                                        /**
                                         * reminder type: email , alert
                                         */
                                        String tempstr = "alert";
                                        int    nCount  = remider.length;

                                        for (int index = 0; index < nCount; index++) {
                                            if (remider[index].getMethod().equals(tempstr)) {
                                                szReminder = remider[index].getMinutes();
                                            }
                                        }
                                    } else {
                                        szReminder = "";
                                    }
                                } else {
                                    szReminder = "";
                                }
                            } else {
                                szReminder = "";
                            }
                        } catch (Exception e) {
                            szReminder = "";
                            e.printStackTrace();
                        }

                        if (bIsAllDay) {
                            calendarEnd.add(Calendar.DAY_OF_MONTH, -1);
                        }

                        nEventDetailCount = application.setEventDetailData(szId, szCalendar, szEvent, szTime, szWhere,
                                szMessage, calendarStart, calendarEnd, szReminder, 0, bIsAllDay, bIsEditAble);
                    }
                }

                sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT,
                           String.valueOf(nEventDetailCount));

                break;
            case IntentCommand.GOOGLE_CALENDAR_RETRIEVE_EVENT_FREE :
            	if(!application.getReflashMonthEnable()){
            		return;
            	}
                AllEvent allEvent = (AllEvent) intent.getSerializableExtra("Data");

                if (allEvent.ismBoolValid()) {
                    int intEventFreeCount = 0;

                    for (int i = 0; i < allEvent.getmEventFeedFrees().length; i++) {
                        intEventFreeCount += allEvent.getmEventFeedFrees()[i].getEvents().length;

                        for (int j = 0; j < allEvent.getmEventFeedFrees()[i].getEvents().length; j++) {
                            String foo[] =
                                allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getStartTime().split("T");

                            if (foo.length == 1) {                     // All day event
                            }
                        }
                    }

                    receiveEventFree(allEvent);
                } else {
                    switch (allEvent.getmIntSC()) {
                    case CCPHttpClient.ERROR_EXECUTE_SOCKETIMEOUT :    // -25
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT_FREE, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_4XX :            // -35
                        szTemp = mContext.getString(R.string.No_Google_Calendar_Content_Found);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT_FREE, szTemp);

                        break;
                    case CCPHttpClient.ERROR_RESPONSE_5XX :            // -36
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Google_Calendar);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT_FREE, szTemp);

                        break;
                    default :
                        szTemp = mContext.getString(R.string.Unable_2_connect_2_Internet);
                        sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT_FREE, szTemp);

                        break;
                    }
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_RETRIEVE_SINGLE_EVENT :
                Event event = (Event) intent.getSerializableExtra("Data");

                if (event != null) {
                    Recurrence recurrence = event.getRecurrence();
                    String     szEventId  = event.getId();

                    if ((null != szEventId) && (null != recurrence)) {
                        setRecurrence(szEventId, recurrence);
                    }
                } else {}

                sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_SINGLE_EVENT, null);

                break;
            case IntentCommand.GOOGLE_CALENDAR_ADD_EVENT :
                Event eventNew = (Event) intent.getSerializableExtra("Data");

                if ((null != eventNew) && eventNew.isBoolIsValid()) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.WND_NEW_EVENT, EventMessage.TRUE);
                    updateWidget();
                } else {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.WND_NEW_EVENT, EventMessage.FALSE);
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_ADD_RECURRENCE_EVENT :
                Event eventNewRecurrence = (Event) intent.getSerializableExtra("Data");

                if ((null != eventNewRecurrence) && eventNewRecurrence.isBoolIsValid()) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.WND_NEW_EVENT, "SUCCESS");
                } else {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.WND_NEW_EVENT, "FAIL");
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_SET_CALENDAR :
                CalendarFeed calendarFeedNew = (CalendarFeed) intent.getSerializableExtra("Data");

                if ((null != calendarFeedNew) && calendarFeedNew.ismBoolValid()) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_SET_CALENDAR, EventMessage.TRUE);
                } else {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_SET_CALENDAR, EventMessage.FALSE);
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_DELETE_EVENT :
                BundleDeleteReturn bundleDelete = (BundleDeleteReturn) intent.getSerializableExtra("Data");

                if (null == bundleDelete) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_DELETE_EVENT, EventMessage.FALSE);
                }

                if (bundleDelete.isBoolDeleteSuccess()) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_DELETE_EVENT, EventMessage.TRUE);
                    updateWidget();
                } else {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_DELETE_EVENT, EventMessage.FALSE);
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_UPDATE_EVENT :
                Event eventUpdated = (Event) intent.getSerializableExtra("Data");

                if ((null != eventUpdated) && eventUpdated.isBoolIsValid()) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_UPDATE_EVENT, EventMessage.TRUE);
                    updateWidget();
                } else {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_UPDATE_EVENT, EventMessage.FALSE);
                }

                break;
            case IntentCommand.GOOGLE_CALENDAR_LOGOUT :
                boolean boolLogout = intent.getBooleanExtra("Data", true);

                sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_LOGOUT, null);

                break;
            case IntentCommand.GOOGLE_CALENDAR_REMINDER :
                Bundle bundle = intent.getExtras();

                if (bundle.getStringArrayList("b_Content") != null) {
                    tempAryHeaderName = bundle.getStringArrayList("b_Header");
                    tempAryContent    = bundle.getStringArrayList("b_Content");
                }

                String tempreminder = null,
                       tempfoo      = null,
                       tempbar      = null,
                       tempPlace    = null,
                       timezone     = null;
                String yy           = null,
                       mm           = null,
                       dd           = null;
                int    parseErr     = 0;

                try {

                    // rem1 is starting at 2010-05-04T12:30:00.000+08:00 in rem1
                    // dottest is starting at 2010-05-08 in test
                    String Ary[] = null;

                    Ary = tempAryContent.get(0).split("is starting at ");

                    if (Ary.length >= 2) {
                        tempreminder = Ary[0];
                        tempfoo      = Ary[1];

                        if (tempreminder.trim().length() == 0) {
                            tempreminder = "(" + mContext.getString(R.string.NoTitle) + ") is starting at ";
                        } else {
                            tempreminder = tempreminder + "is starting at ";    // rem1 is starting at
                        }

                        Ary = tempfoo.split(".000");

                        if (Ary.length >= 2) {
                            tempPlace = Ary[1];
                            timezone  = tempPlace.substring(0, 6);
                            tempPlace = tempPlace.substring(6);                 // in rem1

                            String Tmp[] = tempPlace.split("in");

                            if (Tmp.length == 2) {
                                if (Tmp[1].trim().length() == 0) {
                                    tempPlace = "";
                                }
                            } else if (Tmp.length < 2) {
                                tempPlace = "";
                            }

                            tempfoo = Ary[0];
                            Ary     = tempfoo.split("T");

                            if (Ary.length >= 2) {
                                tempbar      = Ary[1];                          // 12:30:00
                                tempfoo      = Ary[0];                          // 2010-05-04
                                yy           = tempfoo.substring(0, 4);
                                mm           = tempfoo.substring(5, 7);
                                dd           = tempfoo.substring(8, 10);
                                tempbar      = adjustTimeZone(tempbar, timezone);
                                tempreminder = tempreminder + switchAMPM(tempbar) + " of ";
                                tempreminder = tempreminder + mm + "/" + dd + "/" + yy + tempPlace;
                            } else {
                                parseErr = 1;
                            }
                        } else if (Ary.length == 1) {                           // All day event

                            // sample:2010-05-08 in test
                            tempPlace    = Ary[0];
                            tempPlace    = tempPlace.substring(10);    // in test
                            tempfoo      = Ary[0].substring(0, 10);
                            yy           = tempfoo.substring(0, 4);
                            mm           = tempfoo.substring(5, 7);
                            dd           = tempfoo.substring(8, 10);
                            tempreminder = tempreminder + switchAMPM("") + " of ";
                            tempreminder = tempreminder + mm + "/" + dd + "/" + yy + tempPlace;
                        } else {
                            parseErr = 2;
                        }
                    } else {
                        parseErr = 3;
                    }
                } catch (Exception e) {
                    tempreminder = tempAryContent.get(0);
                }

                if (parseErr != 0) {
                    tempreminder = tempAryContent.get(0);
                }

                sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SHOW_ALARM_DLG, tempreminder);

                break;
            }
        }
    };

    public void initCalendarAPIService(Context context, CalendarApplication PApplication) {
        application = PApplication;
        mContext    = context;

        if (null == serviceAdapter) {
            serviceAdapter = new ServiceAdapter(context);
            context.registerReceiver(BroadcastReceiverFromService, new IntentFilter(IntentCommand.INTENT_ACTION));
        }
    }

    public void initEventHandler(Handler handler) {
        eventHandler = handler;
    }

    public void releaseService() {
        if (null != serviceAdapter) {
            mContext.unregisterReceiver(BroadcastReceiverFromService);
            serviceAdapter.releaseService();
        }
    }

    public void doLogin(String szAccount, String szPassword) {

        /*
         * Need ServiceAdapter to get connection first.
         */
        serviceAdapter.getConnect();

        /*
         * Parameter sequence username, password.
         */
        String[] strArguments = { szAccount, szPassword };

        generalCommand = new GeneralCommand("Login", strArguments);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void logout() {
        String[] strArgumentLogouts = { "Logout" };

        generalCommand = new GeneralCommand("Logout", strArgumentLogouts);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void getAllCalendar() {

        /*
         *  Parameters sequences Calendar, Visibility, Projection.
         */
        String[] strArgumentAllCalendars = { "default", "allcalendars", "FULL" };

        generalCommand = new GeneralCommand("Retrieve_Calendar", strArgumentAllCalendars);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void getFreeEvent(int nYear, int nMonth) {

        /*
         * Retrieve events with reduced information during specified date range.
         */
        application.setReceiveEventFree(false);

        String szDate;

        szDate = String.format("%04d-%02d", nYear, nMonth);
        getEventFree(szDate);
    }

    private void getEventFree(String szDate) {
        mszDate = szDate;

        if (null != thdRequest) {
            thdRequest = null;
        }

        thdRequest = new Thread() {
            public void run() {
                String[] strArgumentStartEndFrees = { mszDate, mszDate, "default", "private", "FREE_BUSY" };

                generalCommand = new GeneralCommand("Retrieve_Event_Free", strArgumentStartEndFrees);
                intToken       = serviceAdapter.exeCommand(generalCommand);
            }
        };
        thdRequest.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
        thdRequest.start();
    }

    public void getEvent(int nYear, int nMonth, int nDay) {
        if (null != thdRequest) {
            thdRequest = null;
        }

        String szDate;

        szDate = String.format("%04d-%02d-%02d", nYear, nMonth, nDay);

        /*
         * Parameters sequences StrDateStart, StrDateEnd, Calendar, Visibility, Projection
         */

        // String[] strArgumentStartEnds = {strDateStart, strDateEnd, "default", "[private, public]", "[FULL, FREE_BUSY]"};
        String[] strArgumentStartEnds = { szDate, szDate, "default", "private", "FULL" };

        generalCommand = new GeneralCommand("Retrieve_Event", strArgumentStartEnds);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void getEvent(String szDate) {
        if (null == szDate) {
            return;
        }

        /*
         * Parameters sequences StrDateStart, StrDateEnd, Calendar, Visibility, Projection
         */
        String[] strArgumentStartEnds = { szDate, szDate, "default", "private", "FULL" };

        generalCommand = new GeneralCommand("Retrieve_Event", strArgumentStartEnds);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public synchronized void getSingleEvent(String szEventId) {
        if (null == szEventId) {
            return;
        }

        String[] strArgumentSingleEvent = { "default", "private", "FULL", szEventId };

        generalCommand = new GeneralCommand("Retrieve_Single_Event", strArgumentSingleEvent);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void addEvent(String szTitle, String szContent, String szLocation, String szStartTime, String szEndTime,
                         String szReminderMinute, String szReminderType, String szReminderAble) {

        /*
         * Parameters to initialize a non recurrence event, include
         * Event(title, content), Where(location), When(start time, end time), Reminder(minute, [email, alert], [true, false])
         */

        // String[] strArgumentAddEvent = {"Tennis with Beth", "Meet for a quick lesson.", "Rolling Lawn Courts", "2009-10-21T09:00:00.000Z", "2009-10-21T10:00:00.000Z", "30", "alert", "true"};
        String[] strArgumentAddEvent = {
            szTitle, szContent, szLocation, szStartTime, szEndTime, szReminderMinute, szReminderType, szReminderAble
        };

        generalCommand = new GeneralCommand("Add_Event", strArgumentAddEvent);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void addRecurrenceEvent(String szTitle, String szContent, String szLocation, String szStartTime,
                                   String szEndTime, String szRecurrence, String szRecurrence2, String szDate,
                                   String szReminderMinute, String szReminderType, String szReminderAble) {

        /**
         * Create a recurrence event.
         */

        /*
         * Parameters to initialize a non recurrence event, include
         * Event(title, content), Where(location), When(start time, end time), Reminder(minute, [email, alert], [true, false])
         */

        // String[] strArgumentAddRecurrenceEvent = {"szTitle", "szContent",
        // "szLocation", "szStartTime(20091021T090000)", "szEndTime(20091021T100000)",
        // "szRecurrence[DAILY, WEEKLY, MONTHLY, YEARLY]", "szRecurrence2[SU, MO, TU, WE, TH, FR, SA]",
        // "20091111", "30", "[email, alert]", "[true, false]"};
        String[] strArgumentAddRecurrenceEvent = {
            szTitle, szContent, szLocation, szStartTime, szEndTime, szRecurrence, szRecurrence2, szDate,
            szReminderMinute, szReminderType, szReminderAble
        };

        generalCommand = new GeneralCommand("Add_Recurrence_Event", strArgumentAddRecurrenceEvent);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void setCalendar(String[] strArgumentSetCalendar) {
        if ((null == strArgumentSetCalendar) || (0 >= strArgumentSetCalendar.length)) {
            return;
        }

        /**
         * Set Calendars, users wanna see it.
         */
        generalCommand = new GeneralCommand("Set_Calendar", strArgumentSetCalendar);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void deleteEvent(String szEventId) {
        if (null == szEventId) {
            return;
        }

        String[] strArgumentDeleteEvent = { "default", "private", "FULL", szEventId };

        generalCommand = new GeneralCommand("Delete_Event", strArgumentDeleteEvent);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void updateEvent(String szEventId, String szTitle, String szContent, String szLocation, String szStartTime,
                            String szEndTime, String szReminderMinute, String szReminderType, String szReminderAble) {
        String[] strArgumentUpdateEvent = {
            "default", szEventId, szTitle, szContent, szLocation, szStartTime, szEndTime, "WEEKLY", "TU,TH", "20091231",
            "false", szReminderMinute, szReminderType, szReminderAble
        };

        generalCommand = new GeneralCommand("Update_Event", strArgumentUpdateEvent);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    public void updateRecurrenceEvent(String szEventId, String szTitle, String szContent, String szLocation,
                                      String szStartTime, String szEndTime, String szRecurrence, String szRecurrence2,
                                      String szDate, String szReminderMinute, String szReminderType,
                                      String szReminderAble) {
        String[] strArgumentUpdateEvent = {
            "default", szEventId, szTitle, szContent, szLocation, szStartTime, szEndTime, szRecurrence, szRecurrence2,
            szDate, "true", szReminderMinute, szReminderType, szReminderAble
        };

        generalCommand = new GeneralCommand("Update_Event", strArgumentUpdateEvent);
        intToken       = serviceAdapter.exeCommand(generalCommand);
    }

    /**
     * set recurrence array index
     * @param szEventId
     * @param recurrence
     * "No Recurrence" = 0
     * "Daily" = 1
     * "Every Weekday" = 2
     *               "Every Mon. Wed. and Fri" = 3
     *               "Every Tues. and Thurs." = 4
     *               "Weekly" = 5
     *               "Monthly" = 6
     *               "Yearly" = 7
     */
    private void setRecurrence(String szEventId, Recurrence recurrence) {
        if ((null == szEventId) || (null == recurrence)) {
            return;
        }

        if (recurrence.isHasRecurrence()) {
            String szFreq = recurrence.getFreq();

            // String szUntil = recurrence.getUntil();
            String szByDay = recurrence.getByDay();

            if ((null == szFreq) || (null == szByDay)) {
                return;
            }

            int nIndex = -1;

            if (szFreq.equals("DAILY")) {
                nIndex = 1;
            } else if (szFreq.equals("WEEKLY")) {
                nIndex = checkWeekly(szByDay);
            } else if (szFreq.equals("MONTHLY")) {
                nIndex = 6;
            } else if (szFreq.equals("YEARLY")) {
                nIndex = 7;
            }

            if (1 <= nIndex) {
                int nEventIndex = application.getEventData().getCurrDayEventSelected();

                application.getEventData().setDetailRecurrence(nEventIndex, nIndex);
            }
        }
    }

    private int checkWeekly(String szByDay) {
        if (szByDay.contains("MO,TU,WE,TH,FR")) {
            return 2;
        } else if (szByDay.contains("MO,WE,FR")) {
            return 3;
        } else if (szByDay.contains("TU,TH")) {
            return 4;
        }

        return 5;
    }

    private String adjustTimeZone(String time, String zone) {

        // adjust time zone, including daylight saving....
        return time;
    }

    private String switchAMPM(String time24) {
        String ret  = null;
        String hh   = null,
               min  = null;
        String AMPM = null;
        int    h    = 0;

        if ((time24 == null) || (time24 == "")) {
            ret = mContext.getString(R.string.AM1200);

            return ret;
        }

        hh  = time24.substring(0, 2);
        min = time24.substring(3, 5);

        try {
            h = Integer.parseInt(hh);
        } catch (NumberFormatException e) {
            h = 0;
        }

        if (h >= 12) {
            AMPM = " PM";
        } else {
            AMPM = " AM";
        }

        if (h == 0) {
            h = 12;
        } else if (h > 12) {
            h = h - 12;
        }

        ret = h + ":" + min + AMPM;

        return ret;
    }

    private void sendAppMsg(int nWhat, int nArg1, Object objData) {
        if (eventHandler == null) {
            return;
        }

        Message msg = new Message();

        msg.what = nWhat;                       // message type
        msg.arg1 = nArg1;                       // message
        msg.arg2 = EventMessage.RUN_SERVICE;    // window id
        msg.obj  = objData;
        eventHandler.sendMessage(msg);
    }

    private synchronized void receiveEventFree(AllEvent rfallEvent) {
        mallEvent = rfallEvent;

        if (null == mallEvent) {
            return;
        }

        Thread thdEventFree = new Thread() {
            @Override
            public void run() {
                int      intEventFreeCount = 0;
                AllEvent allEvent          = mallEvent;

                for (int i = 0; i < allEvent.getmEventFeedFrees().length; i++) {
                    intEventFreeCount += allEvent.getmEventFeedFrees()[i].getEvents().length;

                    for (int j = 0; j < allEvent.getmEventFeedFrees()[i].getEvents().length; j++) {
                        int nYear = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getGregorianStart().get(
                                        Calendar.YEAR);
                        int nMonth = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getGregorianStart().get(
                                         Calendar.MONTH);
                        int nDay = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getGregorianStart().get(
                                       Calendar.DAY_OF_MONTH);
                        int nEndYear = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getGregorianEnd().get(
                                           Calendar.YEAR);
                        int nEndMonth = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getGregorianEnd().get(
                                            Calendar.MONTH);
                        int nEndDay = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getGregorianEnd().get(
                                          Calendar.DAY_OF_MONTH);
                        String foo[] =
                            allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getStartTime().split("T");

                        if (foo.length == 1) {    // All day event

                            // Handle All day event
                            Calendar End_Cal = Calendar.getInstance();

                            End_Cal.set(nEndYear, nEndMonth, nEndDay);
                            End_Cal.add(Calendar.DAY_OF_YEAR, -1);
                            nEndYear  = End_Cal.get(Calendar.YEAR);
                            nEndMonth = End_Cal.get(Calendar.MONTH);
                            nEndDay   = End_Cal.get(Calendar.DAY_OF_MONTH);
                            application.setMultiEventData(nYear, nMonth, nDay, nEndYear, nEndMonth, nEndDay, 0);
                        } else {

                            // Handle TimeZone
                            // ex:   2010-05-13T14:30:00.000+08:00
                            String event_time = null,
                                   startTime  = null,
                                   endTime    = null;

                            event_time = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getStartTime();
                            startTime  = getStart_End_Time(event_time);
                            event_time = allEvent.getmEventFeedFrees()[i].getEvents()[j].getWhen().getEndTime();
                            endTime    = getStart_End_Time(event_time);

                            int      StartDayadjust = adjustDate_TimeZone(startTime, adjustTimeZone(foo));
                            int      EndDayadjust   = adjustDate_TimeZone(endTime, adjustTimeZone(foo));
                            Calendar StartCal       = Calendar.getInstance();

                            StartCal.set(nYear, nMonth, nDay);           // Calendar Month is zero based !
                            StartCal.add(Calendar.DAY_OF_YEAR, StartDayadjust);
                            nYear  = StartCal.get(Calendar.YEAR);
                            nMonth = StartCal.get(Calendar.MONTH);
                            nDay   = StartCal.get(Calendar.DAY_OF_MONTH);

                            Calendar EndCal = Calendar.getInstance();

                            EndCal.set(nEndYear, nEndMonth, nEndDay);    // Calendar Month is zero based !
                            EndCal.add(Calendar.DAY_OF_YEAR, EndDayadjust);
                            nEndYear  = EndCal.get(Calendar.YEAR);
                            nEndMonth = EndCal.get(Calendar.MONTH);
                            nEndDay   = EndCal.get(Calendar.DAY_OF_MONTH);
                            application.setMultiEventData(nYear, nMonth, nDay, nEndYear, nEndMonth, nEndDay, 0);
                        }
                    }
                }

                if (0 <= intEventFreeCount) {
                    sendAppMsg(EventMessage.SERVICE_MSG, EventMessage.SERVICE_RETRIEVE_EVENT_FREE,
                               String.valueOf(intEventFreeCount));
                }

                super.run();
            }
        };

        thdEventFree.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thdEventFree.start();
    }

    private int adjustTimeZone(String foo[]) {
        int ret = 0;

        // ////////////////  parse the time zone string as miliseconds
        Calendar cal            = Calendar.getInstance();
        int      DeviceTimeZone = (cal.getTimeZone().getOffset(System.currentTimeMillis()));    // In mili Seconds
        int      EventTimeZone  = 0;
        String   tmp            = null,
                 tmp_1          = null,
                 tmp_0          = null;

        if (foo.length >= 2) {
            tmp = foo[1];
            foo = tmp.split(".000");    // Split +08:00

            if (foo.length >= 2) {
                tmp = foo[1];

                try {
                    tmp   = tmp.substring(0, 6);
                    tmp_0 = tmp.substring(0, 1);
                    tmp_1 = tmp.substring(1, 3);
                    tmp   = tmp.substring(4, 6);
                } catch (IndexOutOfBoundsException e) {
                    tmp_0 = "";
                    tmp_1 = "";
                    tmp   = "";
                }

                try {
                    EventTimeZone = Integer.parseInt(tmp);
                    EventTimeZone = EventTimeZone * 60000 + Integer.parseInt(tmp_1) * 3600000;

                    if (tmp_0.equals("-")) {
                        EventTimeZone = 0 - EventTimeZone;
                    }
                } catch (NumberFormatException e) {
                    EventTimeZone = 0;
                }
            } else {
                tmp = "";
            }
        }

        ret = DeviceTimeZone - EventTimeZone;

        return ret;
    }

    private String adjust24HTimeZone(String Time_24H, int adjust) {
        String ret = Time_24H;
        String tmp = "";

        if ((Time_24H == null) || (Time_24H == "")) {    // all day event
            return ret;
        }

        int h        = 0,
            m        = 0,
            negative = 0,
            hr       = 0,
            min      = 0;;

        if (adjust < 0) {
            negative = 1;
        }

        adjust = Math.abs(adjust) / 60000;
        h      = adjust / 60;
        m      = adjust % 60;

        try {
            hr  = Integer.parseInt(Time_24H.substring(0, 2));
            min = Integer.parseInt(Time_24H.substring(3, 5));
        } catch (NumberFormatException e) {
            hr  = 0;
            min = 0;
        }

        if (negative == 0) {
            hr  = hr + h;
            min = min + m;

            if (min >= 60) {
                hr++;
                min = min - 60;
            }
        } else {
            hr  = hr - h;
            min = min - m;

            if (min < 0) {
                hr--;
                min = min + 60;
            }
        }

        // cross day
        if (hr >= 24) {
            hr = hr - 24;
        } else if (hr < 0) {
            hr = hr + 24;
        }

        if (hr / 10 < 1) {
            tmp = "0" + Integer.toString(hr) + ":";
        } else {
            tmp = Integer.toString(hr) + ":";
        }

        if (min / 10 < 1) {
            tmp = tmp + "0" + Integer.toString(min);
        } else {
            tmp = tmp + Integer.toString(min);
        }

        ret = tmp;

        return ret;
    }

    private int adjustDate_TimeZone(String Time_24H, int adjust) {
        int ret = 0;

        if ((Time_24H == null) || (Time_24H == "")) {
            return ret;
        }

        int h        = 0,
            m        = 0,
            negative = 0,
            hr       = 0,
            min      = 0;;

        if (adjust < 0) {
            negative = 1;
        }

        adjust = Math.abs(adjust) / 60000;
        h      = adjust / 60;
        m      = adjust % 60;

        try {
            hr  = Integer.parseInt(Time_24H.substring(0, 2));
            min = Integer.parseInt(Time_24H.substring(3, 5));
        } catch (NumberFormatException e) {
            hr  = 0;
            min = 0;
        }

        if (negative == 0) {
            hr  = hr + h;
            min = min + m;

            if (min >= 60) {
                hr++;
                min = min - 60;
            }
        } else {
            hr  = hr - h;
            min = min - m;

            if (min < 0) {
                hr--;
                min = min + 60;
            }
        }

        // cross day
        if (hr >= 24) {
            ret = 1;
        } else if (hr < 0) {
            ret = -1;
        }

        return ret;
    }

    private String getStart_End_Time(String timeStr) {
        String ret = "";

        // ex:   2010-05-13T14:30:00.000+08:00
        String event_time = null,
               tempTime   = null;

        event_time = timeStr;

        String[] tmpAry = null;

        tmpAry = event_time.split("T");

        if (tmpAry.length >= 2) {
            tempTime = tmpAry[1];
            tmpAry   = tempTime.split(".000");    // Split +08:00

            if (tmpAry.length >= 2) {
                tempTime = tmpAry[0];

                try {
                    tempTime = tempTime.substring(0, 5);
                } catch (IndexOutOfBoundsException e) {
                    return ret;
                }
            } else {
                tempTime = "";                    // All day event
            }
        } else {
            tempTime = "";                        // All day event
        }

        ret = tempTime;

        return ret;
    }

    public ServiceAdapter getService() {
        return serviceAdapter;
    }

    public void updateWidget() {
        if ((null != serviceAdapter) && (null != serviceAdapter.getBaseService())) {
            Calendar calendar = Calendar.getInstance();
            String   szNow    = String.format("%d-%02d-%02dT%02d:%02d:00", calendar.get(Calendar.YEAR),
                                              calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                                              calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            String[] strArgumentToWidget = { szNow };

            generalCommand = new GeneralCommand("Application_Request_Widget", strArgumentToWidget);
            intToken       = serviceAdapter.exeCommand(generalCommand);
        }
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.event;


public class EventMessage {

    /**
     * define TRUE & FALSE
     */
    public static final String TRUE  = "1";
    public static final String FALSE = "0";

    /**
     * event message
     */
    public static final int EVENT_HANDLE                  = 3000;
    public static final int EVENT_HANDLE_ON_TOUCH_UP      = EVENT_HANDLE + 6;
    public static final int EVENT_HANDLE_ON_TOUCH_OUTSIDE = EVENT_HANDLE + 9;
    public static final int EVENT_HANDLE_ON_TOUCH_MOVE    = EVENT_HANDLE + 7;
    public static final int EVENT_HANDLE_ON_TOUCH_DOWN    = EVENT_HANDLE + 5;
    public static final int EVENT_HANDLE_ON_TOUCH_CANCEL  = EVENT_HANDLE + 8;
    public static final int EVENT_HANDLE_ON_TOUCH         = EVENT_HANDLE + 4;
    public static final int EVENT_HANDLE_ON_PRESS         = EVENT_HANDLE + 3;
    public static final int EVENT_HANDLE_ON_CLICK         = EVENT_HANDLE + 2;
    public static final int EVENT_HANDLE_CREATED          = EVENT_HANDLE + 1;

    /**
     * calendar service event message
     */
    public static final int SERVICE_MSG                   = 4000;
    public static final int SERVICE_UPDATE_EVENT          = SERVICE_MSG + 7;
    public static final int SERVICE_SET_CALENDAR          = SERVICE_MSG + 8;
    public static final int SERVICE_RETRIEVE_SINGLE_EVENT = SERVICE_MSG + 9;
    public static final int SERVICE_RETRIEVE_EVENT_FREE   = SERVICE_MSG + 4;
    public static final int SERVICE_RETRIEVE_EVENT        = SERVICE_MSG + 3;
    public static final int SERVICE_RETRIEVE_CALENDAR     = SERVICE_MSG + 2;
    public static final int SERVICE_LOGOUT                = SERVICE_MSG + 5;
    public static final int SERVICE_LOGIN                 = SERVICE_MSG + 1;
    public static final int SERVICE_DELETE_EVENT          = SERVICE_MSG + 6;

    /**
     * define window run
     */
    public static final int RUN_WND            = 1000;
    public static final int RUN_LOADWND        = RUN_WND + 1;
    public static final int RUN_LOGINWND       = RUN_WND + 2;
    public static final int RUN_MONTHWND       = RUN_WND + 3;
    public static final int RUN_LOGOUTWND      = RUN_WND + 5;
    public static final int RUN_DAYWND         = RUN_WND + 6;
    public static final int RUN_ALARMWND       = RUN_WND + 7;
    public static final int RUN_PROGWND        = RUN_WND + 8;
    public static final int RUN_EVENTINFOWND   = RUN_WND + 9;
    public static final int RUN_NEWEVENTWND    = RUN_WND + 10;
    public static final int RUN_DATEWND        = RUN_WND + 11;
    public static final int RUN_SYNCWND        = RUN_WND + 12;
    public static final int RUN_SETTINGWND     = RUN_WND + 13;
    public static final int RUN_EDITEVENTWND   = RUN_WND + 14;
    public static final int RUN_PRINTPREVIEW_M = RUN_WND + 15;
    public static final int RUN_PRINTPREVIEW_D = RUN_WND + 16;
    public static final int RUN_PRINTPREVIEW_E = RUN_WND + 17;
    public static final int RUN_TIMEWND        = RUN_WND + 18;
    public static final int RUN_MAX            = RUN_WND + 19;
    public static final int RUN_SERVICE        = SERVICE_MSG + 99;

    /**
     * window event message
     */
    public static final int WND_MSG             = 2000;
    public static final int WND_SHOW            = WND_MSG + 1;
    public static final int WND_STOP            = WND_MSG + 2;
    public static final int WND_ITEM_CLICK      = WND_MSG + 4;
    public static final int WND_FINISH          = WND_MSG + 5;
    public static final int WND_EXCP            = WND_MSG + 3;
    public static final int WND_DAY_VIEW        = WND_MSG + 6;
    public static final int WND_CLOSE           = WND_MSG + 2;
    public static final int WND_BTN_SKIP        = WND_MSG + 8;
    public static final int WND_BTN_OK          = WND_MSG + 7;
    public static final int WND_BACK            = WND_MSG + 2;
    public static final int WND_TO_DAY          = WND_MSG + 9;
    public static final int WND_SYNC            = WND_MSG + 25;
    public static final int WND_SHOW_DATE       = WND_MSG + 22;
    public static final int WND_REMINDER        = WND_MSG + 19;
    public static final int WND_PRINT           = WND_MSG + 12;
    public static final int WND_NEW_EVENT       = WND_MSG + 11;
    public static final int WND_MULTI           = WND_MSG + 26;
    public static final int WND_MORE            = WND_MSG + 13;
    public static final int WND_MONTH_VIEW      = WND_MSG + 17;
    public static final int WND_MENU_LOGOUT     = WND_MSG + 14;
    public static final int WND_LOGOUT_YES      = WND_MSG + 16;
    public static final int WND_LOGOUT_NO       = WND_MSG + 15;
    public static final int WND_EVENTSEL        = WND_MSG + 18;
    public static final int WND_EDIT_EVENT      = WND_MSG + 29;
    public static final int WND_EDIT            = WND_MSG + 20;
    public static final int WND_DELETE_EVENT    = WND_MSG + 27;
    public static final int WND_DELETE          = WND_MSG + 21;
    public static final int WND_DATE_SELECT     = WND_MSG + 24;
    public static final int WND_CLOSE_DATE      = WND_MSG + 23;
    public static final int WND_CHK_YES         = WND_MSG + 28;
    public static final int SHOW_PROGRESS_DLG   = RUN_WND + 103;
    public static final int SHOW_CHECK_DLG      = RUN_WND + 105;
    public static final int SHOW_ALARM_DLG      = RUN_WND + 104;
    public static final int SHOW_SYNC_ALARM_DLG = RUN_WND + 106;

    public EventMessage() {}
}

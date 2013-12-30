//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.data;


public class CalendarDayViewData extends CalendarDataOption {
    public CalendarDayViewData(CalendarData cData) {
        super(cData);
    }

    public int setCurrentEventData(int nYear, int nMonth, int nDay) {
        setIntData(CalendarDataId.ID_DAY_VIEW_EVENT_CURR_YEAR, nYear);
        setIntData(CalendarDataId.ID_DAY_VIEW_EVENT_CURR_MONTH, nMonth);

        return setIntData(CalendarDataId.ID_DAY_VIEW_EVENT_CURR_DAY, nDay);
    }

    public int getCurrentEventYear() {
        return getIntData(CalendarDataId.ID_DAY_VIEW_EVENT_CURR_YEAR);
    }

    public int getCurrentEventMonth() {
        return getIntData(CalendarDataId.ID_DAY_VIEW_EVENT_CURR_MONTH);
    }

    public int getCurrentEventDay() {
        return getIntData(CalendarDataId.ID_DAY_VIEW_EVENT_CURR_DAY);
    }
}

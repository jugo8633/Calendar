//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.data;


public class CalendarMultiCalendarData extends CalendarDataOption {
    public CalendarMultiCalendarData(CalendarData cData) {
        super(cData);

        // TODO Auto-generated constructor stub
    }

    public int setMultiCalendarEnable(int nPosition, int bEnable) {
        return setIntData(CalendarDataId.ID_MULTI_CALENDAR_ITEM + nPosition, bEnable);
    }

    public int getMultiCalendarEnable(int nPosition) {
        int nIndex = CalendarDataId.ID_MULTI_CALENDAR_ITEM + nPosition;

        return getIntData(nIndex);
    }

    public int setPreWindowId(int nWndId) {
        return setIntData(CalendarDataId.ID_MULTI_CALENDAR_PRE_WND, nWndId);
    }

    public int getPreWindowId() {
        return getIntData(CalendarDataId.ID_MULTI_CALENDAR_PRE_WND);
    }
}

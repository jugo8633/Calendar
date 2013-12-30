//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.data;


public class CalendarNewEventData extends CalendarDataOption {
    public CalendarNewEventData(CalendarData cData) {
        super(cData);

        // TODO Auto-generated constructor stub
    }

    public int setPreWindowId(int nWndId) {
        return setIntData(CalendarDataId.ID_NEW_EVENT_PRE_WND, nWndId);
    }

    public int getPreWindowId() {
        return getIntData(CalendarDataId.ID_NEW_EVENT_PRE_WND);
    }

    /**
     * set date dialog show status
     * @param nShow: 0 = not show, 1 = show
     * @return
     */
    public int setDateDialogShowStatus(int nShow) {
        return setIntData(CalendarDataId.ID_DATE_DIALOG, nShow);
    }

    public int getDateDialogShowStatus() {
        return getIntData(CalendarDataId.ID_DATE_DIALOG);
    }
}

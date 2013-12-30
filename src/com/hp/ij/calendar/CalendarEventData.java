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

public class CalendarEventData {
    private static final String TAG                          = "CalendarEventData";
    private List<Calendars>     calendars                    = null;
    private List<EventData>     eventData                    = null;
    private List<EventDetail>   eventDetail                  = null;
    private int                 mnCurrDayEventSelected       = -1;
    private String              mszCurrDayDay                = null;
    private String              mszCurrDayMonth              = null;
    private String              mszCurrDayYear               = null;
    private String              mszCurrSelectedDayEventTitle = null;
    private Calendar            newEventCalendarStart        = Calendar.getInstance();
    private Calendar            newEventCalendarEnd          = Calendar.getInstance();

    public CalendarEventData() {
        calendars   = new ArrayList<Calendars>();
        eventData   = new ArrayList<EventData>();
        eventDetail = new ArrayList<EventDetail>();
    }

    public String[] getCurrDayEventDate() {
        String[] arrDate = new String[3];

        arrDate[0] = mszCurrDayYear;
        arrDate[1] = mszCurrDayMonth;
        arrDate[2] = mszCurrDayDay;

        return arrDate;
    }

    public void setCurrDayEventDate(String szYear, String szMonth, String szDay) {
        mszCurrDayYear  = szYear;
        mszCurrDayMonth = szMonth;
        mszCurrDayDay   = szDay;
    }

    public int addEventData(int nYear, int nMonth, int nDay, int nDayOfWeek) {
        if (null == eventData) {
            return 0;
        }

        if (!isEventDate(nYear, nMonth, nDay)) {
            eventData.add(new EventData(nYear, nMonth, nDay, nDayOfWeek));
        }

        return eventData.size();
    }

    public void clearEvent() {
        if (null == eventData) {
            return;
        }

        if (eventData.size() > 0) {
            eventData.clear();
        }
    }

    public int getEventCount() {
        if (null == eventData) {
            return 0;
        }

        return eventData.size();
    }

    public boolean isEventDate(int nYear, int nMonth, int nDay) {
        if (null == eventData) {
            return false;
        }

        for (int i = 0; i < eventData.size(); i++) {
            if (null == eventData.get(i)) {
                continue;
            }

            if (nYear == eventData.get(i).getYear()) {
                if (nMonth == eventData.get(i).getMonth()) {
                    if (nDay == eventData.get(i).getDay()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void deleteEventData(int nYear, int nMonth, int nDay) {
        if (null == eventData) {
            return;
        }

        int nIndex = -1;

        for (int i = 0; i < eventData.size(); i++) {
            if (nYear == eventData.get(i).getYear()) {
                if (nMonth == eventData.get(i).getMonth()) {
                    if (nDay == eventData.get(i).getDay()) {
                        nIndex = i;

                        break;
                    }
                }
            }
        }

        if (0 <= nIndex) {
            eventData.remove(nIndex);
        }
    }

    public int getEventYear(int nIndex) {
        if (!isValidData(nIndex)) {
            return 0;
        }

        return eventData.get(nIndex).getYear();
    }

    public int getEventMonth(int nIndex) {
        if (!isValidData(nIndex)) {
            return 0;
        }

        return eventData.get(nIndex).getMonth();
    }

    public int getEventDay(int nIndex) {
        if (!isValidData(nIndex)) {
            return 0;
        }

        return eventData.get(nIndex).getDay();
    }

    private boolean isValidData(int nIndex) {
        if (null == eventData) {
            return false;
        }

        if (0 >= eventData.size()) {
            return false;
        }

        return true;
    }

    public int addEventDetail(String szId, String szCalendar, String szEvent, String szTime, String szWhere,
                              String szMessage, Calendar startDate, Calendar endDate, String szReminder,
                              int nRecurrence, boolean bAllDay, boolean bEditAble) {
        if (null == eventDetail) {
            return 0;
        }

        eventDetail.add(new EventDetail(szId, szCalendar, szEvent, szTime, szWhere, szMessage, startDate, endDate,
                                        szReminder, nRecurrence, bAllDay, bEditAble));
        sortEventDetail();

        return eventDetail.size();
    }

    private void sortEventDetail() {
        EventDetail tmpEvDetail = null;

        for (int i = 0; i < eventDetail.size(); i++) {
            for (int j = 0; j < eventDetail.size(); j++) {
                Calendar cFirst  = eventDetail.get(i).mdateStart;
                Calendar cSecond = eventDetail.get(j).mdateStart;

                if ((null == cFirst) || (null == cSecond)) {
                    break;
                }

                if (cFirst.before(cSecond)) {
                    tmpEvDetail = eventDetail.get(i);
                    eventDetail.set(i, eventDetail.get(j));
                    eventDetail.set(j, tmpEvDetail);
                }
            }
        }
    }

    public void deleteEventDetail(int nIndex) {
        if ((null == eventDetail) || (0 > nIndex) || (nIndex >= eventDetail.size())) {
            return;
        }

        eventDetail.remove(nIndex);
        sortEventDetail();
    }

    public void clearEventDetail() {
        if (null == eventDetail) {
            return;
        }

        if (eventDetail.size() > 0) {
            eventDetail.clear();
        }
    }

    public int getEventDetailCount() {
        if (null == eventDetail) {
            return 0;
        }

        return eventDetail.size();
    }

    public String getEventId(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszId;
    }

    public String getDetailCalendar(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszCalendar;
    }

    public String getDetailEvent(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszEvent;
    }

    public String getDetailTime(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszTime;
    }

    public String getWhere(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszWhere;
    }

    public String getDetailMessage(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszMessage;
    }

    public void setDetailDateStart(int nIndex, Calendar dateStart) {
        if (-1 == nIndex) {
            newEventCalendarStart = dateStart;

            return;
        }

        if (!isValidDetailData(nIndex)) {
            return;
        }

        eventDetail.get(nIndex).mdateStart = dateStart;
    }

    public Calendar getDetailDateStart(int nIndex) {
        if (-1 == nIndex) {
            return newEventCalendarStart;
        }

        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mdateStart;
    }

    public void setDetailDateEnd(int nIndex, Calendar dateEnd) {
        if (-1 == nIndex) {
            newEventCalendarEnd = dateEnd;

            return;
        }

        if (!isValidDetailData(nIndex)) {
            return;
        }

        eventDetail.get(nIndex).mdateEnd = dateEnd;
    }

    public Calendar getDetailDateEnd(int nIndex) {
        if (-1 == nIndex) {
            return newEventCalendarEnd;
        }

        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mdateEnd;
    }

    public void setDetailReminder(int nIndex, String szReminder) {
        if (!isValidDetailData(nIndex)) {
            return;
        }

        eventDetail.get(nIndex).mszReminder = szReminder;
    }

    public String getDetailReminder(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszReminder;
    }

    public String getDetailWhere(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return null;
        }

        return eventDetail.get(nIndex).mszWhere;
    }

    public void setDetailRecurrence(int nIndex, int nRecurrence) {
        if (!isValidDetailData(nIndex)) {
            return;
        }

        eventDetail.get(nIndex).mnRecurrence = nRecurrence;
    }

    public int getDetailRecurrence(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return 0;
        }

        return eventDetail.get(nIndex).mnRecurrence;
    }

    public void setDetailAllDay(int nIndex, boolean bAllDay) {
        if (!isValidDetailData(nIndex)) {
            return;
        }

        eventDetail.get(nIndex).mbAllDay = bAllDay;
    }

    public boolean getDetailAllDay(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return false;
        }

        return eventDetail.get(nIndex).mbAllDay;
    }

    public void setDetailEditAble(int nIndex, boolean bEditAble) {
        if (!isValidDetailData(nIndex)) {
            return;
        }

        eventDetail.get(nIndex).mbEditAble = bEditAble;
    }

    public boolean getDetailEditAble(int nIndex) {
        if (!isValidDetailData(nIndex)) {
            return false;
        }

        return eventDetail.get(nIndex).mbEditAble;
    }

    public void setCurrDayEventSelected(int nIndex) {
        mnCurrDayEventSelected = nIndex;
    }

    public int getCurrDayEventSelected() {
        return mnCurrDayEventSelected;
    }

    public void setCurrDayEventTitle(String szTitle) {
        mszCurrSelectedDayEventTitle = szTitle;
    }

    public String getCurrDayEventTitle() {
        return mszCurrSelectedDayEventTitle;
    }

    private boolean isValidDetailData(int nIndex) {
        if (null == eventDetail) {
            return false;
        }

        if ((0 >= eventDetail.size()) || (nIndex >= eventDetail.size()) || (0 > nIndex)) {
            return false;
        }

        return true;
    }

    /**
     * calendars data
     */
    public void addCalendar(String szTitle, String szMid, boolean bEnable) {
        if ((null == szTitle) || (0 >= szTitle.length())) {
            return;
        }

        calendars.add(new Calendars(szTitle, szMid, bEnable));
    }

    public void clearCalendar() {
        if (null == calendars) {
            return;
        }

        calendars.clear();
    }

    public int getCalendarsCount() {
        if (null == calendars) {
            return 0;
        }

        return calendars.size();
    }

    public String getCalendarTitle(int nIndex) {
        if (!isValidCalendarData(nIndex)) {
            return null;
        }

        return calendars.get(nIndex).mCalendarTitle;
    }

    public String getCalendarMid(int nIndex) {
        if (!isValidCalendarData(nIndex)) {
            return null;
        }

        return calendars.get(nIndex).mCalendarMID;
    }

    public boolean getIsCalendarEnable(int nIndex) {
        if (!isValidCalendarData(nIndex)) {
            return false;
        }

        return calendars.get(nIndex).mbEnabled;
    }

    private boolean isValidCalendarData(int nIndex) {
        if (null == calendars) {
            return false;
        }

        if ((0 > nIndex) || (nIndex >= calendars.size())) {
            return false;
        }

        return true;
    }

    public void setCalendarEnable(int nIndex, boolean bEnable) {
        if (!isValidCalendarData(nIndex)) {
            return;
        }

        calendars.get(nIndex).setEnable(bEnable);
    }

    public int getCalendarEnableCount() {
        int nCount = 0;

        if (null == calendars) {
            return 0;
        }

        for (int i = 0; i < calendars.size(); i++) {
            if (calendars.get(i).getIsEnable()) {
                nCount++;
            }
        }

        return nCount;
    }

    public int getMultiCalendarCount() {
        if (null == calendars) {
            return 0;
        }

        int nCount = 0;

        nCount = calendars.size();

        return nCount;
    }

    public String getCalendarMidByTitle(String szTitle) {
        if ((null == calendars) || (null == szTitle)) {
            return null;
        }

        String szCalendarTitle = null;

        for (int i = 0; i < calendars.size(); i++) {
            szCalendarTitle = getCalendarTitle(i);

            if (null != szCalendarTitle) {
                if (szCalendarTitle.equals(szTitle)) {
                    return getCalendarMid(i);
                }
            } else {
                continue;
            }
        }

        return null;
    }

    private class Calendars {
        private String  mCalendarMID   = null;
        private String  mCalendarTitle = null;
        private boolean mbEnabled      = true;

        public Calendars(String szTitle, String szMid, boolean bEnable) {
            mCalendarTitle = szTitle;
            mCalendarMID   = szMid;
            mbEnabled      = bEnable;
        }

        public String getTitle() {
            return mCalendarTitle;
        }

        public boolean getIsEnable() {
            return mbEnabled;
        }

        public void setEnable(boolean bEnable) {
            mbEnabled = bEnable;
        }

        public String getMId() {
            return mCalendarMID;
        }
    }


    ;
    private class EventData {
        public int m_nDay       = 0;
        public int m_nDayOfWeek = -1;    // 0~6
        public int m_nMonth     = 0;
        public int m_nYear      = 0;

        public EventData(int nYear, int nMonth, int nDay, int nDayOfWeek) {
            m_nYear      = nYear;
            m_nMonth     = nMonth;
            m_nDay       = nDay;
            m_nDayOfWeek = nDayOfWeek;
        }

        public int getYear() {
            return m_nYear;
        }

        public int getMonth() {
            return m_nMonth;
        }

        public int getDay() {
            return m_nDay;
        }
    }


    ;

    /**
     *
     * @author jugo
     * @event detail
     */
    private class EventDetail {
        private Calendar mdateEnd     = null;
        private Calendar mdateStart   = null;
        private int      mnRecurrence = 0;
        private String   mszCalendar  = null;
        private String   mszEvent     = null;
        private String   mszId        = null;
        private String   mszMessage   = null;
        private String   mszReminder  = null;
        private String   mszTime      = null;
        private String   mszWhere     = null;
        private boolean  mbAllDay     = false;
        private boolean  mbEditAble   = true;

        public EventDetail(String szId, String szCalendar, String szEvent, String szTime, String szWhere,
                           String szMessage, Calendar startDate, Calendar endDate, String szReminder, int nRecurrence,
                           boolean bAllDay, boolean bEditAble) {
            mszId        = szId;
            mszCalendar  = szCalendar;
            mszEvent     = szEvent;
            mszTime      = szTime;
            mszWhere     = szWhere;
            mszMessage   = szMessage;
            mdateStart   = startDate;
            mdateEnd     = endDate;
            mszReminder  = szReminder;
            mnRecurrence = nRecurrence;
            mbAllDay     = bAllDay;
            mbEditAble   = bEditAble;
        }
    }


    ;
}

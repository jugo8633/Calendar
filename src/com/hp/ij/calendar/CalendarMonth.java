//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.sql.Date;

import java.util.Calendar;

public class CalendarMonth {
    private static final String TAG             = "CalendarMonth";
    private final String[]      dayOfWeekName   = {
        "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    public int                  m_nDayOfToday   = 0;
    public int                  m_nMonthOfToday = 0;
    public int                  m_nYearOfToday  = 0;
    private final String[]      monthNAme       = {
        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
        "December"
    };
    private Calendar now = null;

    public CalendarMonth() {
        if (null == now) {
            now = Calendar.getInstance();
        }
    }

    public int getYear() {
        int year = (int) (now.get(Calendar.YEAR));

        return year;
    }

    public int getMonth() {
        int month_num = (int) (now.get(Calendar.MONTH));

        month_num++;

        return month_num;
    }

    public int getDay() {
        int nDay = (int) (now.get(Calendar.DAY_OF_MONTH));

        return nDay;
    }

    public int getFirstDayIndex() {
        int month_num     = (int) (now.get(Calendar.MONTH));
        int year          = (int) (now.get(Calendar.YEAR));
        int first_day_num = use(year, month_num);

        first_day_num = first_day_num - 1;

        return first_day_num;
    }

    public int use(int reyear, int remonth) {
        int week_num;

        now.set(reyear, remonth, 1);
        week_num = (int) (now.get(Calendar.DAY_OF_WEEK));

        return week_num;
    }

    public int getMonthDayScore() {
        int      year_log  = (int) (now.get(Calendar.YEAR));
        int      month_log = (int) (now.get(Calendar.MONTH));
        Date     date      = new Date(year_log, month_log + 1, 1);    // now
        Calendar cal       = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);

        int month_day_score = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return month_day_score;
    }

    public int getlastMonthDayScore() {
        int      year_log  = (int) (now.get(Calendar.YEAR));
        int      month_log = (int) (now.get(Calendar.MONTH));
        Date     date      = new Date(year_log, month_log, 1);    // now
        Calendar cal       = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);

        int month_day_score = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        return month_day_score;
    }

    public void setDate(int nYear, int nMonth) {
        now.set(nYear, nMonth - 1, 1);
    }

    public void reset() {
        now = Calendar.getInstance();
    }

    public void addMonth(int nValue) {
        if (null == now) {
            return;
        }

        now.add(Calendar.MONTH, nValue);
    }

    public String getMonthName(int nMonth) {
        if ((0 >= nMonth) || (nMonth > monthNAme.length)) {
            return null;
        }

        return monthNAme[nMonth - 1];
    }

    public String getDayOfWeekName(int nDay) {
        if ((0 > nDay) || (nDay > dayOfWeekName.length)) {
            return null;
        }

        return dayOfWeekName[nDay];
    }

    public int getMonthInt(String szMonth) {
        int nIndex = 0;

        for (nIndex = 0; nIndex < monthNAme.length; nIndex++) {
            if (monthNAme[nIndex] == szMonth) {
                break;
            }
        }

        return nIndex + 1;
    }

    public void getToday() {
        Calendar cToday;

        cToday          = Calendar.getInstance();
        m_nYearOfToday  = (int) (cToday.get(Calendar.YEAR));
        m_nMonthOfToday = (int) (cToday.get(Calendar.MONTH));
        m_nMonthOfToday++;
        m_nDayOfToday = (int) (cToday.get(Calendar.DAY_OF_MONTH));
    }
}

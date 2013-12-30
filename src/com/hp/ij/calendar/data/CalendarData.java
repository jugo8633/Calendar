//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.data;


import java.util.HashMap;
import java.util.Map;

public class CalendarData {
    public static final int      TYPE_INT = 0;
    public static final int      TYPE_STR = 1;
    public Map<Integer, Integer> dataInt  = new HashMap<Integer, Integer>();
    public Map<Integer, String>  dataStr  = new HashMap<Integer, String>();

    public CalendarData() {}

    public int getIntDataCount() {
        return dataInt.size();
    }

    public int getStrDataCount() {
        return dataStr.size();
    }
}

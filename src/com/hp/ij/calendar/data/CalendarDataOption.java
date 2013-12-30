//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.data;


public abstract class CalendarDataOption {
    private CalendarData mData = null;

    public CalendarDataOption(CalendarData cData) {
        mData = cData;
    }

    protected int setIntData(int nDataId, int nDataValue) {
        if (null == mData) {
            return -1;
        }

        mData.dataInt.put(nDataId, nDataValue);

        return mData.dataInt.size();
    }

    protected int setStrData(int nDataId, String nDataValue) {
        if (null == mData) {
            return -1;
        }

        mData.dataStr.put(nDataId, nDataValue);

        return mData.dataStr.size();
    }

    protected int getIntData(int nDataId) {
        if (null == mData) {
            return -1;
        }

        boolean bValid = mData.dataInt.containsKey(nDataId);

        if (!bValid) {
            return -1;
        }

        return mData.dataInt.get(nDataId);
    }

    protected String getStrData(int nDataId) {
        if (null == mData) {
            return null;
        }

        boolean bValid = mData.dataInt.containsKey(nDataId);

        if (!bValid) {
            return null;
        }

        return mData.dataStr.get(nDataId);
    }
}

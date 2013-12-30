//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.printing.imageprocess;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;

import android.util.Log;

import com.hp.ij.printing.common.data.Data;
import com.hp.ij.printing.common.data.PrinterSettings;
import com.hp.ij.printing.common.data.PrintingType;

public class DataProcess extends BaseProcess {
    private static final String TAG = "DataProcess";

    public DataProcess(Data data, PrinterSettings printerSettings, Context context) {
        super(printerSettings, context);
        setDataClass(data);
    }

    public void getPrintingPage(int idx) {

        // TODO Auto-generated method stub
        String url;

        if ((mData != null) && (mData.mHtmlURIList != null)) {
            url = mData.mHtmlURIList.get(idx - 1);
        } else {
            url = "";
        }

        // url = url.replace("file://", "").trim();
 //       Log.d(TAG, "setPrintingPage Callback!! url = [" + url + " ], idx = [" + idx + "]");

        // If network URL or file path, reutne FILE
        mPrintingCb.setPrintingPage(idx, PrintingType.FILE, url);
    }

    public String getPrintURI(int idx) {
  //      Log.d(TAG, "getPrintURI index = " + idx);

        String url;

        if ((mData != null) && (mData.mHtmlURIList != null)) {
            url = mData.mHtmlURIList.get(idx - 1);
        } else {
            url = "";
        }

  //      Log.d(TAG, "getPrintURI = " + url);

        return url;
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.handler;


import android.app.Application;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.hp.ij.calendar.CalendarApplication;

import frame.event.EventMessage;

public class CalendarThreads {
    private static final String TAG            = "CalendarThreads";
    private Application         mApplication   = null;
    private Handler             mHandler       = null;
    private HandlerThread       mHandlerThread = null;
    private Looper              mLooper        = null;

    public CalendarThreads(int nPriority) {
        mHandlerThread = new HandlerThread(TAG, nPriority);
    }

    public void startHandler(Application application) {
        mApplication = application;

        if (null != mHandlerThread) {
            mHandlerThread.start();
            mLooper  = mHandlerThread.getLooper();
            mHandler = new Handler(mLooper) {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case EventMessage.SHOW_PROGRESS_DLG :
                        if (msg.arg1 == EventMessage.WND_STOP) {

                            /**
                             * hide progress message bar
                             */
                            ((CalendarApplication) mApplication).showProgressBar(false);
                        } else {

                            /**
                             * show progress message bar
                             */
                        }

                        break;
                    }
                }
            };
        }
    }

    public void stopHandler() {
        if (null != mHandlerThread) {
            mHandlerThread.stop();
        }
    }

    public void closeHandler() {
        if (null != mHandlerThread) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public Looper getLooper() {
        return mLooper;
    }
}

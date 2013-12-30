//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.download;

//~--- non-JDK imports --------------------------------------------------------

import android.util.Log;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;

public class ImageThreadExecutor {
    private static final String                       TAG             = "ImageThreadExecutor";
    public static volatile int                        cancelImageId   = -1;
    private static int                                maxWorkerThread = 5;
    private static final LinkedList<DownloaderThread> workQueue       = new LinkedList<DownloaderThread>();
    private static final LinkedList<DownloaderThread> waittingQueue   = new LinkedList<DownloaderThread>();
    public volatile static boolean                    isCancel        = false;

    public static void setMaxWorkerThread(int max) {
        maxWorkerThread = max;
    }

    public static void cancelByImageId(int id) {

        // DownloaderThread.isCancel
        DownloaderThread.addCancelList(id);
    }

    public static void cancelAll() {
        DownloaderThread.isCancel = true;
    }

    public static void cleanAllCancel() {
        DownloaderThread.cleanCancelList();
    }

    public void exe(DownloaderThread thread) {
    //    Log.i(TAG, "exe ------>cancelImageId =[" + cancelImageId + "]");

        try {

            // synchronized (this) {
            DownloaderThread.isCancel = isCancel;
  //          Log.d(TAG, "imageId = " + thread.imageId);
            runThread(thread);

            // }
        } catch (Exception ex) {
            ex.printStackTrace();

            return;
        }
    }

    private void runThread(DownloaderThread thread) throws InterruptedException {
    //    Log.i(TAG, "runThread ------>cancelImageId =[" + cancelImageId + "]");

        if (!isCancel) {
            if (cancelImageId != thread.imageId) {
                startRunThread(thread);
            } else {
  //              Log.d(TAG, "cancel by single bitmap id =[" + cancelImageId + "]");
            }
        } else {
  //          Log.d(TAG, "User cancel");
        }
    }

    protected void startRunThread(DownloaderThread thread) throws InterruptedException {
   //     Log.i(TAG, "startRunThread ------> cancelImageId =[" + cancelImageId + "]");
        DownloaderThread.isCancel = isCancel;

        Thread trueThread = new Thread(thread);

        trueThread.setDaemon(true);
        trueThread.start();
    }
}

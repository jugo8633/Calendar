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

import java.util.ArrayList;
import java.util.LinkedList;

public class DownloaderThread implements Runnable {
    private static final String                       TAG                = "DownloaderThread";
    private static int                                THREAD_POOL_SIZE   = 2;
    private static int                                bitmapId           = 0;
    public static int                                 cancelImageId      = -1;
    public static boolean                             isCancel           = false;
    public final static ArrayList<Integer>            cancelDownloadList = new ArrayList<Integer>();
    private final static LinkedList<DownloaderThread> waittingQueue      = new LinkedList<DownloaderThread>();
    private final static LinkedList<DownloaderThread> workerQueue        = new LinkedList<DownloaderThread>();
    private String                                    address;
    public int                                        imageId;
    public LinkedList<DownloaderThread>               mWorkerQueue;
    private IDownloadThreadCallback                   threadCallback;
    private int                                       timeout;

    public DownloaderThread(IDownloadThreadCallback callback, String address, int timeout) {
        threadCallback = callback;
        this.address   = address;
        this.timeout   = timeout;

        // Generate uinque download image id.
        this.imageId = generatedBitmapId();
    }

    public static void addCancelList(int imageId) {
  //      Log.d(TAG, "addCancelList");
        cancelDownloadList.add(imageId);

        for (int i : cancelDownloadList) {
  //          Log.d(TAG, "Cancel Id =[" + i + "]");
        }
    }

    public static void cleanCancelList() {
 //       Log.d(TAG, "cleanCancelList");
        cancelDownloadList.clear();
    }

    protected int generatedBitmapId() {
        return bitmapId++;
    }

    public void run() {
  //      Log.d(TAG, "run");
  //      Log.d(TAG, "run : isCancel");
  //      Log.d(TAG, "run : isCancel = [" + String.valueOf(isCancel) + "]");
  //      Log.d(TAG, "run : cancelImageId = [" + cancelImageId + "]");

        // Add thread to queue to prepare running.
        waittingQueue.add(this);

        synchronized (workerQueue) {
   //         Log.d(TAG, "[enter workerQueue lock] wait queue thread count = [" + waittingQueue.size() + "]");

            // Set max running threads in the thread pool
            if (workerQueue.size() > THREAD_POOL_SIZE + 1) {
                try {
   //                 Log.d(TAG, "Thread waitting.............");
                    workerQueue.wait();
                } catch (InterruptedException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (workerQueue.size() <= THREAD_POOL_SIZE) {
                if (waittingQueue.size() > 0) {
                    workerQueue.add(waittingQueue.removeFirst());
                }
            }
        }

        DownloadResult result = null;

        if (!isCancel && (result == null)) {
            DownloadService service = new DownloadService();

            if (!isCancel) {
                if (!isCancelMarkedThread(imageId)) {
                    service.isCancel         = isCancel;
                    DownloadService.isCancel = isCancel;
                } else {
 //                   Log.d(TAG, "cancel by image id = [" + imageId + "]");
                    sendAbortRequest();
                }
            } else {
                service.isCancel = isCancel;
 //               Log.d(TAG, "set download service cancel");
                sendAbortRequest();
            }

            if (!isCancel) {
                if (!isCancelMarkedThread(imageId)) {
                    service.isCancel = isCancel;
                    result           = service.download(address, timeout);
                } else {
  //                  Log.d(TAG, "cancel by image id = [" + imageId + "]");
                    sendAbortRequest();
                }
            } else {
                service.isCancel = isCancel;
  //              Log.d(TAG, "service download cancel");
                sendAbortRequest();
            }

            result.setDownloadImageId(this.imageId);

            if (!isCancel) {
                if (!isCancelMarkedThread(imageId)) {
                    service.isCancel = isCancel;
                    threadCallback.sendImageResult(result);
                } else {
  //                  Log.d(TAG, "cancel by image id [" + this.imageId + "]");
                    sendAbortRequest();
                }

                finishedRunningThread();
            } else {
                service.isCancel = isCancel;
   //             Log.d(TAG, "sendImageResult cancel");
                sendAbortRequest();
            }
        }

        result = null;
    }

    protected boolean isCancelMarkedThread(int imageId) {
 //       Log.d(TAG, "isCancelMarkedThread");

        for (int cancelImageId : cancelDownloadList) {
            if (cancelImageId == imageId) {
                return true;
            }
        }

        return false;
    }

    protected void abortRunningThread(DownloadService service) {
        service.fileDownloader.mHttpClient.disconnect();
        service = null;
    }

    protected void sendAbortRequest() {
  //      Log.d(TAG, "sendAbortRequest");
        waittingQueue.clear();

        DownloadResult result = new DownloadResult();

        result.setDownloadImageId(imageId);
        result.setDownloadRequestCode(DownloaderConstant.DOWNLOAD_REQUEST_ABORT);
 //       Log.d(TAG, "sendAbortRequest : download request code = [" + result.getDownloadRequestCode() + "]");
 //       Log.d(TAG, "sendAbortRequest : download image id = [" + result.getDownloadImageId() + "]");
        threadCallback.sendImageResult(result);
        finishedRunningThread();
        result = null;
    }

    protected void finishedRunningThread() {
  //      Log.d(TAG, "finishedRunningThread");
  //      Log.d(TAG, "finishedRunningThread, workerQueue thread count =[" + workerQueue.size() + "]");

        synchronized (workerQueue) {
            if (workerQueue.size() > 0) {
                workerQueue.removeFirst();
            }

            workerQueue.notify();
        }
    }
}

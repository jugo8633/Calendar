//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.download;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Service;

import android.content.Intent;

import android.graphics.Bitmap;

import android.os.Binder;
import android.os.IBinder;

import android.util.Log;

public class DownloadService extends Service {
    private static final String TAG            = "DownloadService";
    public static boolean       isCancel       = false;
    public FileDownloader       fileDownloader = null;
    private final IBinder       mBinder        = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {

        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public void onCreate() {

        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {

        // TODO Auto-generated method stub
        // Stop service;
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {

        // TODO Auto-generated method stub
        // Start service
        super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    public DownloadResult download(String address, int timeout) {
 //       Log.d(TAG, "download");

        DownloadResult result = new DownloadResult();

        try {
            if (!isCancel) {
                Bitmap         bitmap         = null;
                FileDownloader downloadClient = new FileDownloader();

                fileDownloader = downloadClient;
                downloadClient.setTimeout(timeout);
                FileDownloader.isCancel = isCancel;

                if (!isCancel) {
                    bitmap = downloadClient.downloadBitmapFile(address);
                } else {
    //                Log.d(TAG, "disconnect client");
                    downloadClient.disconnect();
                }

                if (downloadClient.getFileDownloaderError() == DownloaderConstant.DOWMLOAD_NO_ERROR) {
                    result.setBitmap(bitmap);
                    result.setModuleErrorCode(DownloaderConstant.DOWMLOAD_NO_ERROR);
                    result.setHttpResErrorCode(DownloaderConstant.DOWMLOAD_NO_ERROR);
                } else {
                    result.setModuleErrorCode(downloadClient.getHttpClientErrorCode());
                    result.setHttpResErrorCode(downloadClient.getHttpResErrorCode());
                }

                downloadClient.disconnect();
                downloadClient = null;
            } else {
  //              Log.d(TAG, "return abort download result");

                return getAbortDownloadResult();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
//            Log.e(TAG, "Exception = " + ex.getMessage());

            return getAbortDownloadResult();
        }

        return result;
    }

    private DownloadResult getAbortDownloadResult() {
        DownloadResult result = new DownloadResult();

        result.setDownloadRequestCode(DownloaderConstant.DOWNLOAD_REQUEST_ABORT);

        return result;
    }

    // Free client
    public void freeResource(FileDownloader client) {
        client.mHttpClient = null;
    }

    public class LocalBinder extends Binder {
        DownloadService getService() {
            return DownloadService.this;
        }
    }
}

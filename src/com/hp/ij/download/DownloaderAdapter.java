//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.download;

//~--- non-JDK imports --------------------------------------------------------

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.util.Log;

public class DownloaderAdapter implements IDownloaderAdapter, IDownloadThreadCallback {
    private static final String TAG         = "DownloaderAdapter";
    static ImageThreadExecutor  exexcutor   = new ImageThreadExecutor();
    private ServiceConnection   _connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.LocalBinder) service).getService();
        }
        public void onServiceDisconnected(ComponentName name) {

            // downloadService.stopService(name);
        }
    };
    private Handler mMsgHandler = new Handler() {

        /**
         * handle message.
         * msg.what = Constant.REQUEST_COMPLETED, call processResponse();
         * msg.what = Constant.SERVICE_READY, call processEvent();
         * @param msg Message object
         */
        public void handleMessage(Message msg) {
            if (idownloaderCallback != null) {
             //   Log.i(TAG, "handleMessage: mAct=" + idownloaderCallback.toString());
            } else {
             //   Log.i(TAG, "handleMessage: mAct=null");
            }

            int    bitmapId            = 0;
            int    moduleError         = 0;
            int    httpResError        = 0;
            int    downloadRequestCode = 0;
            Bitmap bitmap              = null;

            if (msg != null) {
                bitmap              = msg.getData().getParcelable("IMAGE");
                bitmapId            = msg.getData().getInt("IMAGE_ID");
                moduleError         = msg.getData().getInt("MODULE_ERROR");
                httpResError        = msg.getData().getInt("HTTP_RES_ERROR");
                downloadRequestCode = msg.getData().getInt("REQUEST_CODE");
            }

            DownloadResult result = new DownloadResult();

            result.setBitmap(bitmap);
            result.setDownloadImageId(bitmapId);
            result.setModuleErrorCode(moduleError);
            result.setHttpResErrorCode(httpResError);
            result.setDownloadRequestCode(downloadRequestCode);
            idownloaderCallback.downloadResponse(result);
            bitmap = null;

            /*
             * switch (msg.what)
             * {
             * case Constant.REQUEST_COMPLETED:
             * {
             *       Log.i(TAG,"REQUEST_COMPLETED ");
             *       mAct.processResponse(msg.arg1,msg.arg2, (PResult)msg.getData().getParcelable(Constant.RESULT) );
             *       break;
             * }
             * case Constant.SERVICE_READY:
             *       mAct.processEvent(Constant.EVENT_SERVICE_READY, null);
             *       break;
             * default:
             *   super.handleMessage(msg);
             * }
             */
        }
    };
    private Context             context;
    private DownloadService     downloadService;
    private IDownloaderCallback idownloaderCallback;
    private int                 imageId;

    public DownloaderAdapter(Context ctx, IDownloaderCallback callback) {
        Intent in = new Intent(DownloaderConstant.DOWNLOAD_URI);

        idownloaderCallback = callback;
        context             = ctx;
    }

    public void stopService() {
        Intent in = new Intent(DownloaderConstant.DOWNLOAD_URI);
    }

    public int downloadImage(String address, int timeout) {
        disableCancel();

        DownloaderThread downloadThread = new DownloaderThread(this, address, timeout);

        // ImageThreadExecutor exexcutor = new ImageThreadExecutor();
        int imageId = downloadThread.imageId;

        exexcutor.exe(downloadThread);

        return imageId;
    }

    public static void cancelByImageId(int bitmapId) {
  //      Log.d(TAG, "cancelByImageId =[" + bitmapId + "]");
        ImageThreadExecutor.cancelByImageId(bitmapId);
    }

    public static void cancelAll() {
     //   Log.i(TAG, "cancelAll");
        ImageThreadExecutor.isCancel = true;
        ImageThreadExecutor.cancelAll();
    }

    public void disableCancel() {
        ImageThreadExecutor.isCancel = false;
        ImageThreadExecutor.cleanAllCancel();
    }

    public void sendImageResult(DownloadResult result) {
 //       Log.d(TAG, "sendImageResult");

//      idownloaderCallback.downloadResponse(result);
        Bundle bundle = new Bundle();

        bundle.putParcelable("IMAGE", result.getBitmap());

        Bitmap bitmap = result.getBitmap();

        bundle.putInt("IMAGE_ID", result.getDownloadImageId());
        bundle.putInt("MODULE_ERROR", result.getModuleErrorCode());
        bundle.putInt("HTTP_RES_ERROR", result.getHttpResErrorCode());
        bundle.putInt("REQUEST_CODE", result.getDownloadRequestCode());
  //      Log.d(TAG, "sendImageResult : request code = [" + result.getDownloadRequestCode() + "]");

        // idownloaderCallback.downloadResponse(bundle);
        Message msg = new Message();

        msg.what = 0;
        msg.arg1 = 0;
        msg.arg2 = 0;
        msg.setData(bundle);
        mMsgHandler.sendMessage(msg);
        bitmap = null;
        result = null;
    }
}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.download;

//~--- non-JDK imports --------------------------------------------------------

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;

import com.hp.ij.common.ccphttpclient.CCPHttpClient;
import com.hp.ij.common.ccphttpclient.CCPHttpClientCallback;
import com.hp.ij.common.ccphttpclient.RequestInfo;

import org.apache.http.HttpResponse;

//~--- JDK imports ------------------------------------------------------------

import java.io.InputStream;

/**
 * FileDownloader is the helper class that deals to down load file from the
 * remote server.
 *
 * @author Jim
 *
 */
public class FileDownloader extends BaseHttpClientController implements CCPHttpClientCallback {
    public static final int    RESPONSE_BITMAP_TYPE     = 1;
    public static final String TAG                      = "FileDownloader";
    public static boolean      isCancel                 = false;
    private Bitmap             bitmap                   = null;
    private int                mDetailErrorCode         = 0;
    private int                mFileDownloaderErrorCode = 0;
    private Object             bitmapLockObj            = new Object();

    public FileDownloader() {
        mHttpClientErrorCode     = DOWNLOAD_IMAGE_NO_ERROR;
        mFileDownloaderErrorCode = DOWNLOAD_IMAGE_NO_ERROR;
        mDetailErrorCode         = DOWNLOAD_IMAGE_NO_ERROR;
    }

    public int getFileDownloaderError() {
        return mFileDownloaderErrorCode;
    }

    public int getDetailErrorCode() {
        return mDetailErrorCode;
    }

    public void setTimeout(int timeout) {
        super.mTimeout = timeout;
    }

    /**
     * Given image url address and download the bitmap.
     *
     * @param address
     *            the string of url address
     * @return an instance of Bitmap
     */
    public Bitmap downloadBitmapFile(String address) throws Exception {
 //       Log.d(TAG, "downloadBitmapFile");

        try {
            bitmap = null;

            InputStream is = (InputStream) doHttpGet(address, 80);

            synchronized (httpRequestObj) {
                if (is == null) {
    //                Log.d(TAG, "waiting inputStream");
                    httpRequestObj.wait();
                } else {
    //                Log.d(TAG, "get InputStream");
                }
            }

            if (getHttpClientErrorCode() != DOWNLOAD_IMAGE_NO_ERROR) {
                mFileDownloaderErrorCode = getHttpClientErrorCode();
                mDetailErrorCode         = getHttpResErrorCode();

                return null;
            }

            // DownScale

            /*
             * BitmapFactory.Options newOpts = new BitmapFactory.Options();
             * newOpts.inSampleSize = (int) 2; Log.i(TAG,"DownSample rate = "+
             * Integer.toString(newOpts.inSampleSize));
             * newOpts.inJustDecodeBounds = false; bitmap =
             * BitmapFactory.decodeStream(is,null,newOpts);
             */
            synchronized (bitmapLockObj) {
                poolDecodeBmp(is);

                if (bitmap == null) {
                    bitmapLockObj.wait();
                }

                /*
                 * while(bitmap == null){
                 *       Log.d(TAG, "decode bitmap............");
                 *       if( bitmap!=null)
                 *               break;
                 * }
                 */
                if (bitmap != null) {
    //                Log.d(TAG, "Bitmap size = " + bitmap.getRowBytes());
                } else {
 //                   Log.e(TAG, "bitmap is null !!!!!!!!!!");
                }

                return bitmap;
            }
        } catch (Exception ex) {
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
 //           Log.d(TAG, "downloadBitmapFile, mHttpClientErrorCode = " + mHttpClientErrorCode);
            ex.printStackTrace();

            return null;
        }
    }

    private void poolDecodeBmp(InputStream is) {
        synchronized (bitmapLockObj) {
            int i = 1;

            do {
                bitmap = BitmapFactory.decodeStream(is);

                if (bitmap != null) {
   //                 Log.d(TAG, "decode finished");

                    break;
                } else {
                    try {
                        Thread.sleep(++i * 1000);

                        if (is == null) {
    //                        Log.d(TAG, "Decoding but inputstream is null !!!!!");
                        } else if ((is != null) && (i < 10)) {
     //                       Log.d(TAG, "decoding........");
                        } else {
   //                         Log.e(TAG, "can not decode bitmap !!!!!!!!");

                            break;
                        }
                    } catch (InterruptedException e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();
 //                       Log.e(TAG, e.toString());
                    }
                }
            } while (bitmap == null);

            bitmapLockObj.notify();
        }
    }

    public void onCompleted(CCPHttpClient arg0, RequestInfo arg1, HttpResponse arg2) {
        synchronized (httpRequestObj) {
  //          Log.d(TAG, "FileDownloader, Http Response onCompleted");

            int statusCode = arg2.getStatusLine().getStatusCode();

            mHttpResCode = statusCode;
   //         Log.d(TAG, String.format("onCompleted http status code:%d", statusCode));

            if (statusCode == 200) {
   //             Log.d(TAG, "Status Code = 200");
                mRes = arg2;
            } else {
    //            Log.d(TAG, "onCompleted: unExpect status code:" + statusCode);
                mRes = null;
            }

            httpRequestObj.notify();
        }
    }

    public void onError(RequestInfo arg0, HttpResponse arg1, int arg2) {
        synchronized (httpRequestObj) {
            mHttpErrorCode       = arg2;
            mHttpResCode         = arg2;
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
   //         Log.d(TAG, "onError, HttpClientCodeError = " + mHttpClientErrorCode);
            mHttpErrorCode = arg2;
            mRes           = arg1;
            httpRequestObj.notify();
        }
    }

    public void onProgress(CCPHttpClient cbObj, RequestInfo requestinfo, long total_size, long read_size,
                           long current_size) {

        // TODO Auto-generated method stub
    }
}

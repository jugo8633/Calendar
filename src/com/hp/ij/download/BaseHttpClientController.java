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

import com.hp.ij.common.ccphttpclient.CCPHttpClient;
import com.hp.ij.common.ccphttpclient.CCPHttpClient.PostContentBuilder;
import com.hp.ij.common.ccphttpclient.CCPHttpClientCallback;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;

//~--- JDK imports ------------------------------------------------------------

import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;

public class BaseHttpClientController {
    protected static final int  DOWNLOAD_IMAGE_ERROR    = -1;
    protected static final int  DOWNLOAD_IMAGE_NO_ERROR = 0;
    private static final String TAG                     = "BaseHttpClientController";
    protected String            debugURL                = "";
    protected Object            httpRequestObj          = null;
    protected long              mContentLen             = 0;
    protected int               mHttpClientErrorCode    = 0;
    protected int               mHttpResCode            = 0;
    protected int               mSocketTimeout          = 20;
    protected int               mTimeout                = 20;
    protected CCPHttpClient     mHttpClient;
    protected int               mHttpErrorCode;
    protected boolean           mLock;
    protected HttpResponse      mRes;
    protected Thread            mThared;

    public BaseHttpClientController() {
        this(5, 5);
    }

    /**
     *
     * @param socketTimeout
     * @param connTimeout
     */
    public BaseHttpClientController(int socketTimeout, int connTimeout) {
        this.mTimeout        = connTimeout;
        this.mSocketTimeout  = socketTimeout;
        mHttpClient          = getClientInstance();
        mHttpErrorCode       = DOWNLOAD_IMAGE_NO_ERROR;
        mHttpClientErrorCode = DOWNLOAD_IMAGE_NO_ERROR;
        mHttpResCode         = DOWNLOAD_IMAGE_NO_ERROR;
        httpRequestObj       = new Object();
    }

    public void disconnect() {
        if (mHttpClient != null) {
        //    Log.d(TAG, "mHttpClient disconnect");
            mHttpClient.disconnect();
        }
    }

    public CookieStore getCookieStore() {
        return mHttpClient.getCookieStore();
    }

    public void setCookieStore(CookieStore cookie) {
        mHttpClient.setCookieStore(cookie);
    }

    public CCPHttpClient getCCPClient() {
        return mHttpClient;
    }

    public CCPHttpClient getClientInstance() {
        mHttpClient = new CCPHttpClient();
        mHttpClient.setSocketTimeout(this.mSocketTimeout);
        mHttpClient.initRequest((CCPHttpClientCallback) this, 1, this.mTimeout);
      //  Log.d(TAG, "connection timeout = [" + this.mTimeout + "]");
      //  Log.d(TAG, "socket timeout = [" + this.mSocketTimeout + "]");

        return mHttpClient;
    }

    public Object doHttpPostFile(String url, int port, byte[] data) {
        InputStream is = null;

        try {
            synchronized (httpRequestObj) {
                PostContentBuilder builder = mHttpClient.new PostContentBuilder();

                builder.setPostContent(data);
                mHttpClient.startPostMethod(url, port, builder);
                httpRequestObj.wait();

                // waitHttpClient();

                if ((mHttpErrorCode == DOWNLOAD_IMAGE_NO_ERROR) && (mRes != null)) {
                    mContentLen = mRes.getEntity().getContentLength();
                    is          = mRes.getEntity().getContent();
                } else {
//                    Log.e(TAG, "getDOMwithHTTPGET: res is null, or error code=" + mHttpErrorCode);
                    mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;

                    return null;
                }
            }
        } catch (FactoryConfigurationError ex) {
            ex.printStackTrace();
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
            mLock                = false;
        } catch (Exception ex) {
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
            ex.printStackTrace();
            mLock = false;
        }

        return is;
    }

    public Object doHttpPost(String url, String params, int port) {
   //     Log.d(TAG, "method = doHttpPost");

        InputStream is = null;

        try {
            synchronized (httpRequestObj) {
                PostContentBuilder builder = mHttpClient.new PostContentBuilder();

                builder.setPostContent(params);
                mHttpClient.setHttpHeader("Content-Type", "application/x-www-form-urlencoded");
        //        Log.d(TAG, "Connection timeout = [" + mTimeout + "]");
                debugURL = url;
                mHttpClient.startPostMethod(url, port, builder);
                httpRequestObj.wait();

                // waitHttpClient();

                if ((mHttpErrorCode == DOWNLOAD_IMAGE_NO_ERROR) && (mRes != null)) {
                    mContentLen = mRes.getEntity().getContentLength();
                    is          = mRes.getEntity().getContent();
                } else {
                    Log.e(TAG, "doHttpPost: res is null, or error code=" + mHttpErrorCode);
                    mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;

                    // Make abort error code to httpResponse as detail error
                    // code

                    return null;
                }
            }
        } catch (FactoryConfigurationError ex) {
            ex.printStackTrace();
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
            mLock                = false;
        } catch (Exception ex) {
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
            ex.printStackTrace();
            mLock = false;
        }

        return is;
    }

    public Object doHttpsPost(String url, String params, int port) {
     //   Log.d(TAG, "method = doHttpsPost");

        return null;
    }

    public Object doHttpGet(String url, int port) {
//        Log.d(TAG, "method = doHttpGet");

        InputStream is = null;

        try {
            synchronized (httpRequestObj) {
          //      Log.d(TAG, "Connection timeout = [" + mTimeout + "]");
                debugURL = url;
                mHttpClient.startGetMethod(url, port);
          //      Log.d(TAG, "StartGetMethod Execute");
          //      Log.d(TAG, "method = doHttpGet, URL = " + url + ", port = " + port);
                httpRequestObj.wait();

                // waitHttpClient();

                if ((mHttpErrorCode == DOWNLOAD_IMAGE_NO_ERROR) && (mRes != null)) {
                    mContentLen = mRes.getEntity().getContentLength();
                    is          = mRes.getEntity().getContent();
                } else {
//                    Log.e(TAG, "getDOMwithHTTPGET: res is null, or error code=" + mHttpErrorCode);
                    mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;

                    return null;
                }
            }
        } catch (FactoryConfigurationError ex) {
            ex.printStackTrace();
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
 //           Log.e(TAG, "Exception [" + ex.toString() + "]");
            mLock = false;
        } catch (Exception ex) {
            mHttpClientErrorCode = DOWNLOAD_IMAGE_ERROR;
 //           Log.e(TAG, "Exception [" + ex.toString() + "]");
            ex.printStackTrace();
            mLock = false;
        }

        return is;
    }

    public void setHttpHeader(String header, String value) {
        mHttpClient.setHttpHeader(header, value);
    }

    public Object doHttpsGet(String url, String params, int port) {
   //     Log.d(TAG, "method = doHttpsGet");

        return null;
    }

    public int getHttpClientErrorCode() {
        return mHttpClientErrorCode;
    }

    public int getHttpResErrorCode() {
        return mHttpResCode;
    }
}

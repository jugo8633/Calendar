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

public class DownloadResult {
    private int    DownloadRequestCode;
    private int    HttpResErrorCode;
    private int    ModuleErrorCode;
    private Bitmap bitmap;
    private int    downloadImageId;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getDownloadImageId() {
        return downloadImageId;
    }

    public void setDownloadImageId(int downloadImageId) {
        this.downloadImageId = downloadImageId;
    }

    public int getModuleErrorCode() {
        return ModuleErrorCode;
    }

    public void setModuleErrorCode(int moduleErrorCode) {
        ModuleErrorCode = moduleErrorCode;
    }

    public int getHttpResErrorCode() {
        return HttpResErrorCode;
    }

    public void setHttpResErrorCode(int httpResErrorCode) {
        HttpResErrorCode = httpResErrorCode;
    }

    public int getDownloadRequestCode() {
        return DownloadRequestCode;
    }

    public void setDownloadRequestCode(int downloadRequestCode) {
        DownloadRequestCode = downloadRequestCode;
    }
}

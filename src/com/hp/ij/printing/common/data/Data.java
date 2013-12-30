//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.printing.common.data;

//~--- non-JDK imports --------------------------------------------------------

import android.graphics.Bitmap;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class Data {
    private static final int DEFAULT_LEFT_MARGIN   = 0;//46;
    private static final int DEFAULT_RENDER_DPI    = 100;
    private static final int DEFAULT_RENDER_HEIGHT = 716;

//  private static final int DEFAULT_RENDER_WIDTH = 680;
//  private static final int DEFAULT_RENDER_HEIGHT = 470;
    private static final int DEFAULT_RENDER_WIDTH = 512;
    private static final int DEFAULT_TOP_MARGIN   = 0;//40;
    public int               mLeftResId           = 0;
    public int               mPrintOutRotateAngle = 0;
    public int               mRenderDpi           = DEFAULT_RENDER_DPI;
    public int               mRenderWidth         = DEFAULT_RENDER_WIDTH;
    public int               mRenderHeight        = DEFAULT_RENDER_HEIGHT;
    public int               mTopMargin           = DEFAULT_TOP_MARGIN;
    public int               mLeftMargin          = DEFAULT_LEFT_MARGIN;
    public int               mTopResId            = 0;
    public boolean           mPreviewLandscape    = false;
    public ArrayList<Bitmap> mBitmapList;
    public ArrayList<String> mHtmlURIList;
}

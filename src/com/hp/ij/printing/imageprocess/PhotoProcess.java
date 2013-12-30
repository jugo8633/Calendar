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

import android.graphics.Bitmap;
import android.graphics.Matrix;

import android.util.Log;

import com.hp.ij.printing.common.data.Photo;
import com.hp.ij.printing.common.data.PrinterSettings;
import com.hp.ij.printing.common.data.PrintingType;

/**
 * This class is a sample to describing how to implement PhotoProcess.
 * That methods must implement interface IPrintiPreviewFunctions methods.
 * Besides, the additional methods could be added by developers and control all decode process flow.
 *
 *
 * @author jim
 *
 */
public class PhotoProcess extends BaseProcess {
    private static final String TAG = "PhotoProcess";
    private Photo               mPhoto;
    private float               mPhysicalPageHeight;
    private float               mPhysicalPageWidth;
    private float               mPrintableHeight;
    private float               mPrintableWidth;

    public PhotoProcess(Photo photo, PrinterSettings printerSettings, Context context) {
        super(printerSettings, context);

        // TODO Auto-generated constructor stub
        mPhoto = photo;
    }

    public void getPrintingPage(int idx) {

        // TODO Auto-generated method stub
 //       Log.d(TAG, "getPrintingPage idx = " + idx);

        // Bitmap bmp1 = bmpObj.getBitmap();
        Bitmap bmp1 = mData.mBitmapList.get(idx - 1);

        if (bmp1 != null) {
  //          Log.d(TAG, "getBitmap...");

            Bitmap bmp2 = Bitmap.createBitmap(bmp1, mData.mLeftMargin, mData.mTopMargin,
                                              bmp1.getWidth() - mData.mLeftMargin, bmp1.getHeight() - mData.mTopMargin);

            if (bmp2 != null) {
                Matrix matrix = new Matrix();

                // US Letter

/*
                        int printableWidth = (int)((float) (11 * 2.54 * 100) / 4);
                        int printableHeight = (int)((float) (8.5 * 2.54 * 100) / 4);
                        int oriWidth = bmp1.getWidth();
                        int oriHeight = bmp1.getHeight();

                        float scaleRatio = 0.0f;
                        float printWidthRatio = (float) oriWidth / printableWidth;
                        Log.d(TAG, "printWidthRatio = " + printWidthRatio);
                        float printHeightRatio = (float) oriHeight / printableHeight;
                        Log.d(TAG, "printHeightRatio = " + printHeightRatio);
                        float adjustWidthRatio = 1.0f;
                        float adjustHeightRatio = 1.0f;
                        float physicalScaleRatio = 1.0f;

                        if (printWidthRatio > 1){
                                adjustWidthRatio = printWidthRatio;
                                Log.d(TAG, "adjustWidthRatio = " + adjustWidthRatio);
                        }
                        if (printHeightRatio > 1){
                                adjustHeightRatio = printHeightRatio;
                                Log.d(TAG, "adjustHeightRatio = " + adjustHeightRatio);
                        }

                        scaleRatio = (adjustWidthRatio < adjustHeightRatio)?
                                          adjustHeightRatio : adjustWidthRatio;
                        Log.d(TAG, ">>>>>> before check scale ratio = " + scaleRatio);
                        if (scaleRatio * oriHeight > printableHeight) {
                                scaleRatio = adjustHeightRatio;
                        }
                        Log.d(TAG, ">>>>>> after scale ratio = " + scaleRatio);

                        if (scaleRatio > 1.0){
                                physicalScaleRatio = 1.0f/ scaleRatio;
                        }
                        Log.d(TAG, ">>>>>> physical scale ratio = " +
                              physicalScaleRatio);

                // width is already transform to height
                matrix.preScale(physicalScaleRatio, physicalScaleRatio);
                bmp2 = Bitmap.createScaledBitmap(bmp2,
                               (int)(bmp2.getWidth() * (1/printWidthRatio)),
                               (int)(bmp2.getHeight() * (1/printHeightRatio)), false);
*/
   //             Log.d(TAG, "mPrintOutRotateAngle = " + mData.mPrintOutRotateAngle);

                if (bmp2 != null) {
                    if (mData.mPrintOutRotateAngle > 0) {
   //                     Log.d(TAG, "Execute print out rotate !!!");
                        matrix.postRotate(mData.mPrintOutRotateAngle);
                        bmp2 = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(), bmp2.getHeight(), matrix, true);
                    } else {
  //                      Log.d(TAG, "Not execute print out rotate !!!");
                    }
                } else {
  //                  Log.d(TAG, "setPrintingPage...bmp2 is null !!");
                }

                if (mSavePreviewImage == true) {
                    saveBMPtoFile(bmp2, "printout.png");
                }

  //              Log.d(TAG, "setPrintingPage...");
                mPrintingCb.setPrintingPage(idx, PrintingType.BITMAP, bmp2);
                bmp2 = null;
                System.gc();
            }
        } else {
  //          Log.d(TAG, "setPrintingPage...bmp1 is null !!");
        }

        // bmpObj.release();
        // bmpObj = null;
    }

    public void setPhysicalPageSize(float width, float height) {
 //       Log.d(TAG, "setPhysicalPageSize(" + width + "," + height + ")");
        mPhysicalPageWidth  = width;
        mPhysicalPageHeight = height;
    }

    public void setPrintableSize(float width, float height) {
 //       Log.d(TAG, "setPrintableSize(" + width + "," + height + ")");
        mPrintableWidth  = width;
        mPrintableHeight = height;
    }
}

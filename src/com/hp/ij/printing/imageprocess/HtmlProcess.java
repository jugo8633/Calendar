//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.printing.imageprocess;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.hp.ij.printing.common.adapter.PrintPreviewAdapter;
import com.hp.ij.printing.common.constants.PrintingEnums;
import com.hp.ij.printing.common.data.Data;
import com.hp.ij.printing.common.data.DispatchType;
import com.hp.ij.printing.common.data.Photo;
import com.hp.ij.printing.common.data.PrinterSettings;
import com.hp.ij.printing.common.data.PrintingData;
import com.hp.print.apdk.PAPER_SIZE;


public class HtmlProcess {
	private static final String TAG = "HTMLProcess";

	public PrintingData mPrintingData;
	
	private Context mContext; 
	private PrintPreviewAdapter mProcPreviewAdapter;
	private boolean mPreviewLandscape = false;
	private boolean mSavePreviewImage = false;
	private String mServiceName = "";
	private Data mData;
	private Photo mPhoto;


	public HtmlProcess(Context context, Data data, DispatchType type, 
	           String serviceName) {

        mContext = context;
        mData			            = data;
        mPhoto			            = new Photo();
        mPrintingData 				= new PrintingData();     
        if (mPrintingData != null) {
            mPrintingData.printerSettings = new PrinterSettings();
            if (mPrintingData.printerSettings != null) {
            	mPrintingData.printerSettings.isPortrait = false;
            }
            mPrintingData.dispatchType = type;	
        }        
        mData.mHtmlURIList = new ArrayList<String>();
        mData.mBitmapList = new ArrayList<Bitmap>();
        mServiceName = serviceName;       
    }
	
	public void setHostIP(String hostip) {
		
    	if ((mPrintingData != null) && 
        	(mPrintingData.printerSettings != null)) {
        	 mPrintingData.printerSettings.mPrinterHost = hostip;
        }		
	}

	public void setPreviewGalleryPortrait(boolean enable) {
		
    	if ((mPrintingData != null) && 
        	(mPrintingData.printerSettings != null)) {
    		mPrintingData.printerSettings.isPortrait = enable;
        }		
	}
	
	public void setPapersize(PAPER_SIZE size) {

    	if ((mPrintingData != null) && 
            	(mPrintingData.printerSettings != null)) {
        		mPrintingData.printerSettings.mPaperSize = size;
        }				
	}
	
	public boolean showPreview(Intent intent, Application app) {
//		Log.d(TAG, "showPreview()...");
		
   	    Bundle launchSettings = new Bundle();
   	    if (launchSettings != null) {
   	    	
            launchSettings.putString(
    		          PrintingEnums.LAUNCH_RASTER_SERVICE_INTENT_STR, 
    		          mServiceName);                   
            intent.putExtras(launchSettings);
   	    	
   	    	if (mPrintingData.dispatchType == DispatchType.PRINTING_PHOTO) {
  // 	    		Log.d(TAG, "mPrintingData.dispatchType == " +
  // 	    				   "DispatchType.PRINTING_PHOTO...");
   	        
                PhotoProcess photoProcessor = new PhotoProcess(
                		              mPhoto, new PrinterSettings(), mContext);
                if (photoProcessor != null) {
                    photoProcessor.mSavePreviewImage = mSavePreviewImage;
                    photoProcessor.setDataClass(mData);
                    mProcPreviewAdapter = new PrintPreviewAdapter(
                    		       photoProcessor, mPrintingData, app, intent);              	
                }
   	    	}
   	    	
   	    	if (mPrintingData.dispatchType == DispatchType.PRINTING_DATA) {
  // 	    		Log.d(TAG, "mPrintingData.dispatchType == " +
//				   "DispatchType.PRINTING_DATA...");
   	    		   	        
                DataProcess dataProcessor = new DataProcess(
                		               mData, new PrinterSettings(), mContext);
                if (dataProcessor != null) {
                	dataProcessor.mSavePreviewImage = mSavePreviewImage; 
                    mProcPreviewAdapter = new PrintPreviewAdapter(
                    		        dataProcessor, mPrintingData, app, intent);                	
                }
   	    	}   	    	
   	    	
            if (mProcPreviewAdapter != null) {
                mProcPreviewAdapter.showPreview();
                return true;
            }   	   	         	    
   	    }   
   	    return false;
	}
	
	public void stopAllPreview() {
		
        if (mProcPreviewAdapter != null) {
            mProcPreviewAdapter.stopAllPreview();
            mProcPreviewAdapter = null;
            
            if (mData != null) {
            	// Clear array list.
                if ((mData.mHtmlURIList != null) && 
                    	(mData.mHtmlURIList.isEmpty() != false)) {
                	mData.mHtmlURIList.clear();
                }
                if ((mData.mBitmapList != null) && 
                	(mData.mBitmapList.isEmpty() != false)) {
                    for (int i = 0; i < mData.mBitmapList.size(); i++)
                    {
                    	Bitmap bmp = (Bitmap) mData.mBitmapList.get(i);
                    	if (bmp != null) {
                    		bmp.recycle();
                    		bmp = null;
                    	}
                    } 
                    mData.mBitmapList.clear();
                }
            }
        }		
	}
	
	public void savePreviewImage(boolean save) {
		mSavePreviewImage = save;
	}
}

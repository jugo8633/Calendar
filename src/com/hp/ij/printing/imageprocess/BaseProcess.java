//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.printing.imageprocess;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hp.ij.printing.common.data.Data;
import com.hp.ij.printing.common.data.PrinterSettings;
import com.hp.ij.printing.common.data.PrintingData;
import com.hp.ij.printing.common.data.PrintingType;
import com.hp.ij.printing.common.interfaces.IDispatchPreviewCallback;
import com.hp.ij.printing.common.interfaces.IDispatchPrintingCallback;
import com.hp.ij.printing.common.interfaces.IPrintPreviewFunctions;
import com.hp.ij.printing.common.interfaces.IRGBBytesCallback;
import com.hp.ij.calendar.BMPObj;


/**
 * This class is a sample to describing how to implement BaseProcess.
 * That methods must implement interface IPrintiPreviewFunctions methods.
 * Besides, the additional methods could be added by developers and control
 *  all decode process flow. 
 * 
 * 
 * @author jim
 *
 */

public class BaseProcess implements IPrintPreviewFunctions {
	private static final String TAG = "BaseProcess";
	
	protected Context mContext;
	protected PrintingData mPrintingData;
	protected Data mData;
	protected PrinterSettings mPrinterSettings;
	protected Thread mPreviewThread;
	protected htmlRenderRunnable mPreviewRunnable;
	//protected htmlRenderRunnable mPrintingRunnable;
	protected ArrayList<Bitmap> mBitmapList;
	
	protected IDispatchPreviewCallback mPreviewCb;
	protected IDispatchPrintingCallback mPrintingCb;
	
	public boolean mSavePreviewImage = false;
	private htmlRenderObj hrObj = null;
	
	
	public BaseProcess(PrinterSettings printerSettings, Context context) {		
		mContext = context;
		mPrinterSettings = printerSettings;
		mBitmapList = new ArrayList<Bitmap>();
	}
		
	public void setDataClass(Data data) {
		mData = data;
		if ((mData != null) && (mData.mBitmapList != null)) {
		    EmptyBitmapList(mData.mBitmapList);
		}	
	}
	
	public void initJob(PrintingData printData, 
			            IDispatchPreviewCallback preCb,
			            IDispatchPrintingCallback pricb) {
		
//		Log.d(TAG, "initJob()...");
		
		mPrintingCb = pricb;
		mPreviewCb = preCb;
		mPrintingData = printData;	
		//mData = printData.data;
        		
        mPreviewRunnable = new htmlRenderRunnable();
        if (mPreviewRunnable != null) {
            mPreviewThread = new Thread(mPreviewRunnable);
            mPreviewThread.start();
        }
        //mPrintingRunnable = new htmlRenderRunnable();
        //if (mPrintingRunnable != null) {
        //	mPrintingThread = new Thread(mPrintingRunnable);
        //	mPrintingThread.start();
        //}
	}

	public void registerPreviewCallback(IDispatchPreviewCallback preCb) {
		
		mPreviewCb = preCb;
	}

	public void registerPrintingCallback(IDispatchPrintingCallback priCb) {
		
		mPrintingCb = priCb;
	}
	
	private void EmptyBitmapList(ArrayList<Bitmap> list) {
		
		if ((list != null) && 
			(list.isEmpty() != true)) {
			
		    for (Bitmap bmp : list) { 
			     if (bmp != null) {
				     bmp.recycle();
				     bmp = null;
			     }
		    }
		    list.clear();
		}
		
		System.gc();
	}	
	
	public int getPageCount() {
		
		if ((mData != null) && (mData.mHtmlURIList != null)) {
	//		Log.d(TAG, "getPageCount = " + mData.mHtmlURIList.size());
			return mData.mHtmlURIList.size();			
		} else {
//			Log.d(TAG, "htmlFileURI is null");
			return 0;			
		}
	}
	
	public void getPreviewBMP(int idx, Rect previewSize, boolean isPortrait) {
//		Log.d(TAG, "getPreviewBMP idx = " + idx);
		Bitmap previewBmp;
		
		if ((idx <= 0) || (mData == null) || (mData.mBitmapList == null)) {			
//			Log.d(TAG, "Can't get previev image... ");
			mPreviewCb.setPreviewBMP(idx, null); 
			return;
		}
					
		try {
//		    Log.d(TAG, "Find preview image...");	
		   
		    previewBmp = (Bitmap) mData.mBitmapList.get(idx-1);
		   
		} catch (Exception e) {
//			Log.d(TAG, "Can't find preview image...");
			
			previewBmp = null;
			
			BMPObj bmpObj = new BMPObj();				        
            hrObj = new htmlRenderObj(mContext);
            if ((bmpObj != null) && (hrObj != null)) {
            	hrObj.mBmpObj = bmpObj;
                hrObj.mUri = mData.mHtmlURIList.get(idx-1);                        
                if (startRender(hrObj) == true) {
            		try {

            			synchronized(bmpObj) {		
            				bmpObj.wait();
            			}	            			
            			previewBmp = bmpObj.getBitmap();
            			mData.mBitmapList.add(previewBmp);
            			bmpObj = null;
            		} catch (InterruptedException e1) {
            			e1.printStackTrace();
            		}
                }
            }
		}	
        
        if (previewBmp != null) {
  //      	Log.d(TAG, "Add margin image...");
        	Canvas canvas = new Canvas(previewBmp);	        
        	if (canvas != null) {
        		
        		if (mData.mLeftResId > 0) {
        		    Bitmap bmpLeft = getBitmapFromDrawable(mData.mLeftResId);
        		    if (bmpLeft != null) {
                        canvas.drawBitmap(bmpLeft, 0, 0, null);
                        bmpLeft.recycle();
                        bmpLeft = null;
        		    }
        		}
        		if (mData.mTopResId > 0) {
        		    Bitmap bmpTop = getBitmapFromDrawable(mData.mTopResId);
        		    if (bmpTop != null) {
                        canvas.drawBitmap(bmpTop, 0, 0, null);
                        bmpTop.recycle();
                        bmpTop = null;
        		    }
        		}
        	}	    	    
        }          	
  //      Log.d(TAG, "setPreviewBMP...");
        mPreviewCb.setPreviewBMP(idx, previewBmp); 
        System.gc();
	}

	public Bitmap getPrintingBMP(int idx) {
		mPreviewCb.setPreviewBMP(idx, null);
		return null;
	}

	public void getPrintingPage(int idx) {

    }

	public void getPrintingRGBRaw(int idx, int rowIdx, IRGBBytesCallback rgbCb) {

	}

	public void stopAllPreviewBMP() {
	//	Log.d(TAG, "stopAllPreviewBMP()...");
		if (hrObj != null) {
		    stopRender(hrObj);
		    hrObj = null;
		}
	}

	public void stopAllPrinting() {

	}

	public void stopPreviewBMP(int idx) {

	}

	public void stopPrinting(int idx) {

	}

	public PrintingType getPrintingType() {
		return null;
	}

	public int getHeight(int idx) {
		return 0;
	}

	public int getWidth(int idx) {
		return 0;
	}

	public String getPrintURI(int idx) {
		return null;
	}

	public void setEffectiveResolution(int rx, int ry) {
		
	}

	public void setPhysicalPageSize(float width, float height) {
		
	}

	public void setPrintableSize(float width, float height) {
		
	}
	
	public boolean setPrintingSettings(Bundle arg0) {
		// TODO Auto-generated method stub
		return false;
	}	
	
	private boolean startRender(htmlRenderObj hrObj) {
//		Log.d(TAG, "send start render message...");
        Message msg = new Message();
        if ((mPreviewRunnable != null) && (hrObj != null) && (msg != null)) {        	
            msg.what = htmlRenderRunnable.MSG_Start_Render;
            msg.obj = hrObj;
            return mPreviewRunnable.sendMessage(msg);
        }
        return false;
	}
	
	private boolean stopRender(htmlRenderObj hrObj) {
//		Log.d(TAG, "send stop rendering message...");
        Message msg = new Message();
        if ((mPreviewRunnable != null) && (hrObj != null) && (msg != null)) {
            msg.what = htmlRenderRunnable.MSG_Stop_Rendering;
            msg.obj = hrObj;
            return mPreviewRunnable.sendMessage(msg);
        }
        return false;
	}
		
	
    private Bitmap getBitmapFromDrawable(int dwId) {
    	Bitmap bmp = null;
    	Resources res = mContext.getResources();	
    	if (res != null) {
    		bmp = BitmapFactory.decodeResource(res, dwId).
    		      copy(Bitmap.Config.RGB_565, true);	
    	}       
    	return bmp;
    }
	
	private class htmlRenderRunnable implements Runnable {
		public static final int MSG_QUIT = 1;
		public static final int MSG_Start_Render = 2;
		public static final int MSG_Stop_Rendering = 3;
		
		private Handler mHandler = null;
		private BMPObj mBmpObj = null;
		
		public void run() {
			
			 Looper.prepare();			
			 /**
			  *  Message loop
			  */       
			 mHandler = new Handler() {				  	  
		         public void handleMessage(Message msg) {
		        	 htmlRenderObj htmlRender;
		        	 
		             switch (msg.what) {       
                         case MSG_Start_Render: 
		            	 	 //webkitRender((htmlRenderObj) msg.obj);
                        	 htmlRender = (htmlRenderObj) msg.obj;
                        	 if (htmlRender != null) {
                        		 htmlRender.Run();
                        	 }
		                     break;
                         case MSG_Stop_Rendering:
                        	 htmlRender = (htmlRenderObj) msg.obj;
            	             if (htmlRender != null) {
            		             htmlRender.Cancel();
            	             }
                        	 break;
		                 case MSG_QUIT:
		                	 getLooper().quit();
		                	 break;
		             }       
		             //super.handleMessage(msg);                  
		        }          
		    };
		    Looper.loop();							
		}	
		
		public boolean sendMessage(Message msg) {
			if ((mHandler != null) && (msg != null)) {
			    mHandler.sendMessage(msg);
			    return true;
			} else {
				return false;
			}
		}
		
	    private void webkitRender(htmlRenderObj obj) {
	    	
	    	if (obj == null) {
//	    		Log.e(TAG, "webkitRender is fail !!");
       		    synchronized(mBmpObj) {
       		    	mBmpObj.notify();
      		    }	
	    		return;
	    	}
	    	
	    	Context context = obj.mContext; 
			if (context != null) {
				obj.mBrowser = new WebView(context);
				if (obj.mBrowser == null) {
//					Log.e(TAG, "webkitRender can't new WebWiew !!");
	       		    synchronized(mBmpObj) {
	       		    	mBmpObj.notify();
	      		    }	
					return;
				}
			}
			
			WebView webView = obj.mBrowser;
	    	String url = obj.mUri;	
	    	mBmpObj = obj.mBmpObj;	    	
			  			
			webView.clearCache(true);
			WebSettings websettings = webView.getSettings();  
			websettings.setJavaScriptEnabled(true);
//			Log.d(TAG, "Webkit clear cache...");
			webView.setWebViewClient(new WebViewClient() {
				
				@Override
				public void onPageFinished(WebView view, String url) {
//					Log.d(TAG, "renderBegin => Start = " + url);
					
					int dpi = mData.mRenderDpi; 
					int w = mData.mRenderWidth;
					int h = mData.mRenderHeight;
					boolean landscape = mData.mPreviewLandscape; 
					int hcount = 0;
					
		            int numPages = view.renderBegin(w, h, dpi, landscape);
	//	            Log.d(TAG, "renderBegin => End, renderPageCount=" + numPages); 
				
		            if (mBmpObj != null) {         	
		            	// Add bitmap content.
		            	int bmpByteCount = 3;
	        		    for (int i = 0; i < h; i++) {
	        		    	 byte[] pixels = new byte[w * bmpByteCount];
	        			     int bytenum = view.renderSwathRGB(
	        			    		            1, i, 1, false, pixels);
	        			     //Log.d(TAG, "renderSwathRGB = " + bytenum);
	        			     //Log.d(TAG, "pixels length = " + pixels.length);
		            	     if (bytenum > 0) {
		            	    	 mBmpObj.writeBitmapContent(pixels, 
		            	    			                    pixels.length);		            	    	 
		            	    	 hcount++;
		            	     }
	        		    }
//	        		    Log.d(TAG, "buildBitmap = " + hcount);
	        		    mBmpObj.buildBitmap(w, hcount, 
	        		    		          mData.mTopMargin, mData.mLeftMargin);
	        		    
	        		    if (mSavePreviewImage == true) {
		        		    Bitmap bmp = mBmpObj.getBitmap();
		        		    if (bmp != null) {      		    	
		        		        saveBMPtoFile(bmp, "preview.png"); 
		        		    }	        		    	
	        		    }
	            	}	   
					view.renderEnd();
					view.freeMemory();
					view = null;
					System.gc();
					
	       		    synchronized(mBmpObj) {
	       		    	mBmpObj.notify();
	      		    }					
				}
			});
//			Log.d(TAG, "Browser.loadUrl...");
			webView.loadUrl(url);
	    }
	}

	public class htmlRenderObj {

		public BMPObj mBmpObj;
		public String mUri;
        public WebView mBrowser;
        
        private Context mContext;
        private boolean misLoading;
		
		public htmlRenderObj(Context context) {
			mContext = context;
			mBmpObj = null;
			mUri = null;
			mBrowser = null;
			misLoading = false;
		}
		
		public boolean Run() {
	//		Log.d(TAG, "Webkit running...");
			
			mBrowser = new WebView(mContext); 
			if (mBrowser != null) {
				mBrowser.clearCache(true);
				WebSettings websettings = mBrowser.getSettings();  
				websettings.setJavaScriptEnabled(true);
				mBrowser.setWebViewClient(new WebViewClient() {
					
					@Override
					public void onPageFinished(WebView view, String url) {
	//					Log.d(TAG, "renderBegin => Start = " + url);
						
						int dpi = mData.mRenderDpi; 
						int w = mData.mRenderWidth;
						int h = mData.mRenderHeight;
						boolean landscape = mData.mPreviewLandscape; 
						int hcount = 0;
						
			            int numPages = view.renderBegin(w, h, dpi, landscape);
	//		            Log.d(TAG, "renderBegin => End, renderPageCount=" + numPages); 
					
			            if (mBmpObj != null) {         	
			            	// Add bitmap content.
			            	int bmpByteCount = 3;
		        		    for (int i = 0; i < h; i++) {
		        		    	 byte[] pixels = new byte[w * bmpByteCount];
		        			     int bytenum = view.renderSwathRGB(
		        			    		            1, i, 1, false, pixels);
		        			     //Log.d(TAG, "renderSwathRGB = " + bytenum);
		        			     //Log.d(TAG, "pixels length = " + pixels.length);
			            	     if (bytenum > 0) {
			            	    	 mBmpObj.writeBitmapContent(pixels, 
			            	    			                    pixels.length);		            	    	 
			            	    	 hcount++;
			            	     }
		        		    }
	//	        		    Log.d(TAG, "buildBitmap = " + hcount);
		        		    mBmpObj.buildBitmap(w, hcount, 
		        		    		          mData.mTopMargin, mData.mLeftMargin);
		        		    
		        		    if (mSavePreviewImage == true) {
			        		    Bitmap bmp = mBmpObj.getBitmap();
			        		    if (bmp != null) {      		    	
			        		        saveBMPtoFile(bmp, "preview.png"); 
			        		    }	        		    	
		        		    }
		            	}	   
						view.renderEnd();
						view.freeMemory();
						view = null;
						System.gc();
						misLoading = false;
						
		       		    synchronized(mBmpObj) {
		       		    	mBmpObj.notify();
		      		    }					
					}
				});
//				Log.d(TAG, "Browser.loadUrl...");
				mBrowser.loadUrl(mUri);
				misLoading = true;
				return true;
			}
			return false;
		}
		
		public void Cancel() {
	//		Log.d(TAG, "Webkit stopping...");
			if (mBrowser != null && misLoading == true) {
				mBrowser.stopLoading();	
				mBrowser = null;
				System.gc();
			}
		}
	}
	
    public boolean saveBMPtoFile(Bitmap bmp, String fname) {  
		
    	//File f = new File(PRINTOUT_PATH + filename);
    	//f.delete(); 
    	
        FileOutputStream fOut = null;
        
        try {
        	fOut = mContext.openFileOutput(fname, 
        			                       Context.MODE_WORLD_READABLE |
        			                       Context.MODE_WORLD_WRITEABLE);
            if ((fOut != null) && (bmp != null)) {
                bmp.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            }
            fOut.flush();
            fOut.close();
            return true;
            
        } catch (FileNotFoundException e) { 
            e.printStackTrace();
        } catch (IOException e) { 
            e.printStackTrace(); 
        }     
        return false;
    }


    
}

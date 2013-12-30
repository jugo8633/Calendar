//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.printpreview;

import java.sql.Timestamp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HpFooterBarButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.android.print.PrinterData;
import com.hp.ij.classes.printpreviewdata.PreviewAdapter;
import com.hp.ij.classes.printpreviewdata.PreviewGallery;
import com.hp.ij.classes.printpreviewdata.PreviewAdapter.PrinterAddressVerified;
import com.hp.ij.iprintersetup.IPrinterSetupEnums;
import com.hp.ij.iprintersetup.PrinterSetupUtils;
import com.hp.ij.printing.common.constants.PrintingEnums;
import com.hp.ij.printing.common.imageprocess.PrintingBaseApplication;
import com.hp.ij.printpreview.PrintingToastDialog.OnBtnListener;
import com.hp.ij.calendar.CalendarApp;
import com.hp.ij.calendar.CalendarApplication;
import com.hp.ij.calendar.CalendarDateWnd;
import com.hp.ij.calendar.CalendarSyncDlg;
import com.hp.ij.calendar.R;
import com.hp.print.apdk.PAPER_SIZE;

import frame.event.EventMessage;
import frame.view.PrintCheckDialog;

public class CalendarPrintPreviewActivity extends Activity
{
    protected static final String TAG = "CalendarPrintPreviewActivity";

    private static final int INVALID_REQUEST     = 0;
    private static final int PRINT_REQUEST       = 1;
    private static final int PRINT_SETUP_REQUEST = 2;

    private Bundle m_printSettings;

    private static final int REQUEST_UPDATE_VIEW        = 1;
    private static final int REQUEST_RESET_PREVIEW      = 2;   
    
    private Context mContext;
    private PrintingToastDialog mConfirmDlg = null;
    private long m_PrintJobId = 0;
    private PrintingBaseApplication m_app; 
    private Button  mPrintBtn, mReturnBtn;
    private TextView mSettingBtn;
    private TextView mLogoutBtn;    
    
    private static final String SAVED_PRINT_SETTINGS 	= "SAVED_PRINT_SETTINGS";
    public CalendarApplication         application          = null;    
    public  boolean  Logout = false;
    // Message loop
    private Handler m_uiHandler =
        new Handler()
        {
            public void handleMessage(Message msg)
            {
            	Log.d(TAG, "m_uiHandler [handleMessage]");
                switch(msg.what)
                {
                    case REQUEST_RESET_PREVIEW:
                    	Log.d(TAG, "REQUEST_RESET_PREVIEW");
                        PreviewGallery previews = (PreviewGallery)  
                        	           findViewById(R.id.PreviewImageGallery);
                        previews.setSelection(0);                         
                        break;
                    case REQUEST_UPDATE_VIEW:
                    	Log.d(TAG, "REQUEST_UPDATE_VIEW");
                    	if (m_previewAdapter != null) {
                    	    m_previewAdapter.notifyDataSetChanged();
                    	}
                    default:
                    	Log.d(TAG, "adpter nofity DataSet changed");
                    	if (m_previewAdapter != null) { // fixe bug for screen rotation.
                            m_previewAdapter.notifyDataSetChanged();
                    	}
                        break;
                }
                super.handleMessage(msg);
            }
        };
        
    private void launchPrintSetup(int code)
    {        	
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_COLOR_OPTION, true);
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_DUPLEX_OPTION, true);
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_BORDERLESS_OPTION, true);    
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_DUPLEX_OPTION, true);    
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_MEDIA_TRAY_OPTION, true);    
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_NUM_COPIES_OPTION, true);
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_NUP_OPTION, true);
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_ORIENTATION_OPTION, true);
       	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_PAPER_SIZE_OPTION, true);
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_PAPER_TYPE_OPTION, true);
    	m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_HIDE_QUALITY_OPTION, true);
    	
        Intent printSetupIntent = new Intent("com.hp.ij.printersetup.PRINT_SETUP");
        printSetupIntent.replaceExtras(m_printSettings);
        startActivityForResult(printSetupIntent, code);
    }

    private View.OnClickListener myListener =
        new View.OnClickListener()
        {
            public void onClick(View v)
            {
                int type = INVALID_REQUEST;
                Log.d(TAG, "View Id= ["+ v.getId() + "]");
                
                switch(v.getId())
                {
                    case R.id.PrintButton:
                    	Log.d(TAG, "PrintButton");
                		m_printSettings.putLong(PrintingEnums.PRINT_JOB_ID, m_PrintJobId);
                		m_printSettings.putBoolean(PrintingEnums.IS_STOP_PRINT, false);   
                        m_previewAdapter.requestPrint(m_printSettings);
                        type = PRINT_REQUEST;                          
                        showDialog(100);
        	    		break;                                                                 
                    case R.id.SelectedPrinterButton:
                    	Log.d(TAG, "SelectedPrinterButton");
                        launchPrintSetup((type == INVALID_REQUEST) ? PRINT_SETUP_REQUEST : type);
                        break;                        
                    case R.id.PreviewReturn:
                    	Log.d(TAG, "ReturnButton");
                    	application.setRunPrintView(false);
                    	finish();
                    	break;                     	
                    default:
                        break;
                }
            }
        };

    private View.OnTouchListener myTouchListener =
        new View.OnTouchListener()
        {
    		public boolean onTouch(View v, MotionEvent event) {
    			Log.d(TAG, "onTouch");
					
    			if (v.getId() == R.id.SelectedPrinterButton) {
    				//mSettingBtn.setSelect(true);
    			}else if(v.getId() == R.id.tvPrintLogout){
    				if (event.getAction() == MotionEvent.ACTION_DOWN ) {		
    					v.setBackgroundResource(R.drawable.ok_btn_focus);
    				}else if(event.getAction() == MotionEvent.ACTION_UP){
    					v.setBackgroundResource(R.drawable.ok_btn);				            
    					String szCaption = CalendarPrintPreviewActivity.this.getString(R.string.would_you_like_to_log_out);
    					CalendarPrintPreviewActivity.this.showCheckDlg(true, szCaption, EventMessage.SERVICE_LOGOUT);				            			            											
    				}						
    			}
    			return false;
			}
        };
        
    OnBtnListener doPrintingToastDialog = new OnBtnListener() {

		public void onBtnClick(int id) {
			
			if (id == PrintingToastDialog.BUTTON_OK) {
				// TODO Auto-generated method stub
			}
		    if (id == PrintingToastDialog.BUTTON_CANCEL) {
		  
				CharSequence text = getString(R.string.txt_printer_cancel);
				int duration = Toast.LENGTH_SHORT;				
				Toast toast = Toast.makeText(mContext, text, duration);
				toast.show();
		    	
            	m_printSettings.putBoolean(PrintingEnums.IS_STOP_PRINT, true);                	
                m_previewAdapter.requestPrint(m_printSettings);
			}	
		    mConfirmDlg.dismiss();
		}   			
	};

    private PreviewAdapter m_previewAdapter = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        boolean settingsChanged = false;
        Log.d(TAG, "settingsChanged = "  + String.valueOf(settingsChanged));
         
        m_app.setSettingChange( false );
           
        if (resultCode == RESULT_OK)
        {
            if (data != null)
            {
            	Log.d(TAG, "RESULT_OK"); //setup activity return
                if (data.getExtras() != null)
                {
                    m_printSettings = (Bundle)data.getExtras().clone();
                    settingsChanged = true;
                }else{
                	Log.w(TAG, " Setup Bundle is null");
                }
            }
            if (requestCode == PRINT_REQUEST)
            {
            	Log.d(TAG, "PRINT_REQUEST");
                //if (m_printSettings.getString(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS) != null)
                //{              	
            		Log.d(TAG,"PRINT_REQUEST");
                	m_printSettings.putBoolean(PrintingEnums.IS_STOP_PRINT, true);                	
                    m_previewAdapter.requestPrint(m_printSettings);
                    finish();
                    return;
                //}
            }else{
            	Log.w(TAG, "REQUEST ANOTHER CODE");
            }
        }
        //finish();
        //Callback to UI       
        if (settingsChanged) {        	       	
        	Log.d(TAG, "settingsChanged = true");       

        	//If need to back adapter
            //m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINT_PORTRAIT_MODE, false);
            //m_printSettings.putParcelable(IPrinterSetupEnums.BUNDLE_PRINT_PAPER_SIZE, PAPER_SIZE.A4);
//            m_previewAdapter.settingsChanged(m_printSettings);            
//            settingChanged(  data.getExtras() , true );                
//            finish();
        	updateSelectedPrinter();
            //If do not need to back adapter
            settingChanged(  data.getExtras() , false );           
        }
        
    }

    //return to UI
    private void settingChanged(Bundle settings, boolean isNeedBack){
    	 Log.d(TAG, "setting changed function");
    	 //Pass to PrintPreviewAdapter to notify the type is setup change rather than printing
    	 settings.putBoolean(PrintingEnums.IS_SETTINGS_CHANGED, true);
    	 settings.putBoolean(PrintingEnums.IS_NEEDED_BACK_ADAPTER, isNeedBack);
    	 m_previewAdapter.requestPrint(settings);
    }
    
    private PreviewAdapter.DataChangeListener m_changeListener =
        new PreviewAdapter.DataChangeListener()
        {
            public void previewDataChanged()
            {
                m_uiHandler.sendMessage(m_uiHandler.obtainMessage(REQUEST_UPDATE_VIEW));
            }

            public void previewReset()
            {
                m_uiHandler.sendMessage(m_uiHandler.obtainMessage(REQUEST_RESET_PREVIEW));
            }
        };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {	
        Log.d(TAG, "onCreate");
        Log.d(TAG, "Invalid Request =["+ INVALID_REQUEST +"]");
        Log.d(TAG, "Print Request =["+ PRINT_REQUEST +"]");
        Log.d(TAG, "Print Setup Request =["+ PRINT_SETUP_REQUEST +"]");
        
        application = (CalendarApplication) this.getApplication();        
        
        super.onCreate(savedInstanceState);
        // Micro add 01/15/2010
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Micro
        setContentView(R.layout.printing_preview);
        
        mContext = CalendarPrintPreviewActivity.this;
        m_app = PrintingBaseApplication.getInstance();
        m_app.setSettingChange( false );

        mPrintBtn = (Button) findViewById(R.id.PrintButton);
        if (mPrintBtn != null) {
            mPrintBtn.setOnClickListener(myListener);
        }
        
        if (mPrintBtn != null) {
            mPrintBtn.setEnabled(false); 
        }
                   
        mReturnBtn = (Button) findViewById(R.id.PreviewReturn);
        if (mReturnBtn != null) {
            mReturnBtn.setOnClickListener(myListener);
        }
        
        mSettingBtn = (TextView) findViewById(R.id.SelectedPrinterButton);
        if (mSettingBtn != null) {
        	mSettingBtn.setOnClickListener(myListener);
        	mSettingBtn.setOnTouchListener(myTouchListener);
        } 
        
        mLogoutBtn = (TextView) findViewById(R.id.tvPrintLogout);
        if (mLogoutBtn != null) {
        	mLogoutBtn.setOnClickListener(myListener);
        	mLogoutBtn.setOnTouchListener(myTouchListener);
        }         
       
        if (savedInstanceState != null)
            m_printSettings = savedInstanceState.getBundle(SAVED_PRINT_SETTINGS);
        else if (getIntent().getExtras() != null) {
            //m_printSettings = (Bundle)getIntent().getExtras().clone();
            m_printSettings =  getIntent().getExtras();
            if (m_printSettings.getString(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS) == null)
            	m_printSettings.putAll(PrinterSetupUtils.get(this));
            String testIp = m_printSettings.getString(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS);
            Log.e(TAG, " Printer Ip = " + testIp);
        }
        else {
        	Log.e(TAG, " getIntent().getExtras() is null !!! ");
            m_printSettings = new Bundle();
            
        }        
                
        //getLastUsedPrinterInfo();
                       
        PreviewGallery previews = (PreviewGallery) findViewById(
        		                                  R.id.PreviewImageGallery);        
        m_previewAdapter = previews.createAdapter(this, m_printSettings,
        		                                  R.layout.previewimageitem,
        					                      R.id.PreviewImageItem, 
        					                      m_changeListener);
        if (m_previewAdapter != null) {
            m_previewAdapter.setGuideIDs(R.id.PreviewTopGuide, R.id.PreviewLeftGuide);
        }         
                
        // 0727 Add indicating text - "Checking for printer..."                 
        if (savedInstanceState == null)
        {
            m_previewAdapter.getPrinterSelection(m_printAddrVerified);
        } else {
            updateSelectedPrinter();
        }
        
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        if (ts != null) {
            m_PrintJobId = ts.getTime();
        }
        Log.d(TAG, "Print Job TimeStamp = " + m_PrintJobId);          
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBundle(SAVED_PRINT_SETTINGS, m_printSettings);
    }
    
    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart");    
        super.onStart();   
    }

    @Override
    public void onDestroy()
    {    	
        m_previewAdapter.quit();
        m_previewAdapter = null;
        m_printSettings = null;
        m_uiHandler = null;
        m_app.setSettingChange( true );
        
        if (this.isFinishing() == true) {
        	application.setRunPrintView(false);
        	if (Logout) {
        		sendAppFinishMsg(EventMessage.WND_MSG, EventMessage.WND_CHK_YES, null);
        	}
        }
        
        super.onDestroy();
    }
    
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK :
        	application.setRunPrintView(false);
        	finish();
            break;
        }
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d(TAG, "onCreateDialog : "+id);		
	    switch (id) {
	    case EventMessage.SHOW_CHECK_DLG :
	        int         nResId = application.getCheckDlgResId();
	        PrintCheckDialog chkDlg = new PrintCheckDialog(this, application.getHandler(), nResId);
            chkDlg.setCheckCaption(application.getCheckCaption());
            chkDlg.showWindow(true);	            
	        return chkDlg.getDialog();	        
	    case 100  :	// toast	        	
	        mConfirmDlg = new PrintingToastDialog(mContext);
    		if (mConfirmDlg != null) {
    			mConfirmDlg.setOnBtnClick(doPrintingToastDialog);
	    	}	        	
	        return mConfirmDlg;	            	            
	    }
        return super.onCreateDialog(id);	    
	}
	
	@Override
	protected void onPause() {		
		super.onPause();
		Log.d(TAG, "onPause");
		
	}

	@Override
	protected void onResume() {		
		super.onResume();
		Log.d(TAG, "onResume");
		//mSettingBtn.setSelect(false);
	}
	
	 private String getPaperSizeOption() {
        String option = null;
        if (m_printSettings.containsKey(IPrinterSetupEnums.BUNDLE_PRINT_PAPER_SIZE)) {
            PAPER_SIZE size =
                    m_printSettings.getParcelable(IPrinterSetupEnums.BUNDLE_PRINT_PAPER_SIZE);
            switch (size) {

            case A4:
                option = getString(android.R.string.paper_size_a4);
                break;

            case PHOTO_SIZE:
                option = getString(android.R.string.paper_size_4x6);
                break;

            case PHOTO_5x7:
            case PHOTO_5x7_MAIN_TRAY:
                option = getString(android.R.string.paper_size_5x7);
                break;

            case LETTER:
            default:
                option = getString(android.R.string.paper_size_letter);
                break;
            }
        }

        return (option);
    }

    private String getColorOption() {
        String option = null;
        if (m_printSettings.containsKey(IPrinterSetupEnums.BUNDLE_PRINT_IN_COLOR)) {
            boolean inColor = m_printSettings.getBoolean(IPrinterSetupEnums.BUNDLE_PRINT_IN_COLOR);
            option =
                    getString(inColor ? R.string.PrintInColorStringID : R.string.PrintInBWStringID);
        }
        return (option);
    }
	
	
    private void updateSelectedPrinter() {
        String selectedPrinter = null;
        TextView label = (TextView) findViewById(R.id.SelectedPrinterButton);

        if ((m_printSettings != null)
                && (m_printSettings.getString(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS) != null)) {
            selectedPrinter = m_printSettings.getString(IPrinterSetupEnums.BUNDLE_PRINTER_NAME);
            if (TextUtils.isEmpty(selectedPrinter))
                selectedPrinter =
                        m_printSettings.getString(IPrinterSetupEnums.BUNDLE_PRINTER_MODEL);
            if (TextUtils.isEmpty(selectedPrinter))
                selectedPrinter =
                        m_printSettings.getString(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS);
        }

        StringBuilder sb;

        if (TextUtils.isEmpty(selectedPrinter)) {
            sb = new StringBuilder(getString(R.string.NoPrinterSelectedStringID));
            sb.append("\n");
            label.setText(sb.toString());
            findViewById(R.id.PrintButton).setEnabled(false);
        } else {
            sb = new StringBuilder(selectedPrinter);
            sb.append("\n");
            String paperSize = getPaperSizeOption();
            String color = getColorOption();

            if (!TextUtils.isEmpty(paperSize))
                sb.append(paperSize);
            if (!TextUtils.isEmpty(paperSize) && !TextUtils.isEmpty(color))
                sb.append(", ");
            if (!TextUtils.isEmpty(color))
                sb.append(color);
            label.setText(sb.toString());
            findViewById(R.id.PrintButton).setEnabled(true);
        }
    }	
	
	
	
	
    public void showCheckDlg(boolean bShow, String szCaption, int nResId) {
        application.setCheckCaption(szCaption, nResId);

        if (bShow) {
            showDialog(EventMessage.SHOW_CHECK_DLG);
        } else {
            removeDialog(EventMessage.SHOW_CHECK_DLG);
        }
    }
    
    protected void sendAppFinishMsg(int nWhat, int nArg1, Object objData) {
        Message msg = new Message();

        msg.what = nWhat;       // message type
        msg.arg1 = nArg1;       // message
        msg.arg2 = EventMessage.SERVICE_LOGOUT;    // window id
        msg.obj  = objData;
        application.getHandler().sendMessage(msg);
    }
    
    private void getLastUsedPrinterInfo(){
    	Log.d(TAG, "getLastUsedPrinterInfo");
    	Bundle mPrinterBundle 	= PrinterSetupUtils.get(this);
    	String mPrinterIp		= mPrinterBundle.getString(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS); 
    	String mPrinterName		= mPrinterBundle.getString(IPrinterSetupEnums.BUNDLE_PRINTER_NAME);
    	String mPrinterModel	= mPrinterBundle.getString(IPrinterSetupEnums.BUNDLE_PRINTER_MODEL);
    	
        TextView label = (TextView) findViewById(R.id.SelectedPrinterButton);
        Log.d(TAG, "printerIp " + mPrinterIp +" printerName " + mPrinterName +" printerModel " + mPrinterModel);

        StringBuilder sb = null;
        if( mPrinterName != null && !mPrinterName.trim().equals("")){
        	sb = new StringBuilder(mPrinterName);
        }else if( mPrinterModel != null && !mPrinterModel.trim().equals("")){
        	sb = new StringBuilder(mPrinterModel);
        }else  if( mPrinterIp != null && !mPrinterIp.trim().equals("")){
        	sb = new StringBuilder(mPrinterIp);
        }
        
        if(sb != null){
        	label.setText(sb.toString());
        	findViewById(R.id.PrintButton).setEnabled(true);
        }else{
        	 // 0727 Add indicattion text - "Checkong for printer..."
            sb = new StringBuilder(getString(R.string.NoPrinterSelectedStringID));
            sb.append("\n");
            label.setText(sb.toString());
            //
        	findViewById(R.id.PrintButton).setEnabled(false);
        }                
    }    
    
    PrinterAddressVerified m_printAddrVerified = new PrinterAddressVerified() {

        public void printerFound(final PrinterData printer, final boolean isBase)
        {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (m_printSettings == null)
                        return;
                     m_printSettings.putString(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS, printer.getAddress());
                     if (isBase)
                     {
                         m_printSettings.putString(IPrinterSetupEnums.BUNDLE_PRINTER_NAME, getString(R.string.BasePrinterNameStringID));
                         m_printSettings.putString(IPrinterSetupEnums.BUNDLE_PRINTER_MODEL, getString(R.string.BasePrinterModelStringID));
                         m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINTER_SPECIFIED_BY_ADDRESS, false);
                     }
                     else
                     {
                         String name = printer.getName();
                         m_printSettings.putString(IPrinterSetupEnums.BUNDLE_PRINTER_NAME, ((name == null) ? "" : name));
                         String model = printer.getModel();
                         m_printSettings.putString(IPrinterSetupEnums.BUNDLE_PRINTER_MODEL, ((model == null) ? "" : model));
                         m_printSettings.putBoolean(IPrinterSetupEnums.BUNDLE_PRINTER_SPECIFIED_BY_ADDRESS, TextUtils.isEmpty(name) && TextUtils.isEmpty(model));
                     }
                     updateSelectedPrinter();
                 }
            });
        }

        public void noPrintersFound()
        {
            runOnUiThread(new Runnable() {
                public void run() {
                   if (m_printSettings == null)
                       return;
                   m_printSettings.remove(IPrinterSetupEnums.BUNDLE_PRINTER_NAME);
                   m_printSettings.remove(IPrinterSetupEnums.BUNDLE_PRINTER_MODEL);
                   m_printSettings.remove(IPrinterSetupEnums.BUNDLE_PRINTER_ADDRESS);
                   m_printSettings.remove(IPrinterSetupEnums.BUNDLE_PRINTER_SPECIFIED_BY_ADDRESS);
                   updateSelectedPrinter();
                }
            });
        } 	
    };
    
	
}

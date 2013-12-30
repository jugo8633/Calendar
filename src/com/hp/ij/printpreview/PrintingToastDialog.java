//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.printpreview;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.util.Log;

import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;

import com.hp.ij.calendar.R;

//~--- JDK imports ------------------------------------------------------------

import java.util.EventListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Micro Hsiung
 *
 */
public class PrintingToastDialog extends Dialog {
    public static final int BUTTON_CANCEL = 0;
    public static final int BUTTON_OK     = 1;

    // debug message tag
    public static final String TAG = "PrintingToastDialog";
    private Context            mContext;
    private OnBtnListener      mIListener;

    /**
     *
     * @param context .
     */
    public PrintingToastDialog(Context context) {
        //super(context, R.style.Theme_CommLoadingDialog);
        super(context, R.style.PrintDialog);               
    }

    /**
     *
     * @param context .
     * @param theme .
     */
    public PrintingToastDialog(Context context, int theme) {
        //super(context, R.style.Theme_CommLoadingDialog);
        super(context, R.style.PrintDialog);         
        mContext = context;
    }

    /**
     * @param savedInstanceState .
     */
    protected void onCreate(Bundle savedInstanceState) {
 //       Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printing_toast);

        Button btnOK = (Button) findViewById(R.id.PrintingToast_Ok);

        btnOK.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mIListener.onBtnClick(BUTTON_OK);
            }
        });

        Button btnCancel = (Button) findViewById(R.id.PrintingToast_Cancel);

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mIListener.onBtnClick(BUTTON_CANCEL);
            }
        });
    }

    @Override
    protected void onStart() {

        // TODO Auto-generated method stub
        super.onStart();
        setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                case KeyEvent.KEYCODE_BACK :
               //     Log.i(TAG, "Back key pressed!!");
                    mIListener.onBtnClick(BUTTON_OK);

                    return true;
                }

                return false;
            }
        });

        TimerTask task = new TimerTask() {
            public void run() {
           //     Log.i(TAG, "Timer:run !!");
                dismiss();
            }
        };
        Timer timer = new Timer();

        if (timer != null) {
         //   Log.i(TAG, "Timer:schedule !!");
            timer.schedule(task, 4000);
        }
    }

    public void setOnBtnClick(OnBtnListener I) {
        mIListener = I;
    }

    public interface OnBtnListener extends EventListener {
        public void onBtnClick(int id);
    }
}

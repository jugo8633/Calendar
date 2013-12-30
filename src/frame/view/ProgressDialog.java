//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.view;


import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;

import android.graphics.drawable.ColorDrawable;

import android.os.Handler;
import android.os.Message;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import frame.event.EventHandler;

import frame.window.WndBase;

public class ProgressDialog extends WndBase {
    private static final String TAG        = "ProgressDialog";
    private final int           KEY_BACK   = 4;
    private EventHandler        WndEvent   = null;
    private Dialog              progDlg    = null;
    private Handler             WndHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case EventHandler.EVENT_HANDLE_CREATED :
                break;
            case EventHandler.EVENT_HANDLE_ON_TOUCH :             // touch event
                switch (msg.arg1) {
                case EventHandler.EVENT_HANDLE_ON_TOUCH_DOWN :    // touch down
                    break;
                case EventHandler.EVENT_HANDLE_ON_TOUCH_UP :      // touch up
                    break;
                }
            case EventHandler.EVENT_HANDLE_ON_CLICK :
                break;
            }
        }
    };

    public ProgressDialog(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
    }

    @Override
    public void showWindow(boolean bShow) {
        if (bShow) {
            WndEvent = new EventHandler(WndHandler);
            progDlg  = new Dialog(super.getApp());

            ColorDrawable drawable = new ColorDrawable(0);

            progDlg.getWindow().setBackgroundDrawable(drawable);
            progDlg.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            progDlg.setContentView(getProgView());
            progDlg.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KEY_BACK == keyCode) {
                        sendAppMsg(SHOW_PROGRESS_DLG, WND_STOP, null);
                    }

                    return false;
                }
            });

            if (progDlg.isShowing()) {
                return;
            }

            try {
                progDlg.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            progDlg.cancel();
        }
    }

    @Override
    public void closeWindow() {

        // TODO Auto-generated method stub
        if (null != WndEvent) {
            WndEvent = null;
        }
    }

    public Dialog getDialog() {
        return progDlg;
    }

    private View getProgView() {
        RelativeLayout rl     = new RelativeLayout(super.getApp());
        LayoutParams   params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        rl.setLayoutParams(params);

        ProgressBar pBar = new ProgressBar(super.getApp());

        params.width  = 100;
        params.height = 100;
        pBar.setLayoutParams(params);
        rl.addView(pBar);

        return rl;
    }
}

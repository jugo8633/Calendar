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
import android.view.Window;

import android.widget.TextView;

import com.hp.ij.calendar.R;
import com.hp.ij.printpreview.CalendarPrintPreviewActivity;

import frame.event.EventHandler;

import frame.window.WndBase;

public class PrintCheckDialog extends WndBase {
    private static final String TAG           = "PrintCheckDialog";
    private final int           KEY_BACK      = 4;
    private EventHandler        WndEvent      = null;
    private Dialog              checkDlg      = null;
    private String              mszCheckInfo  = null;
    private Activity            PrintActivity = null;
    private Handler             WndHandler    = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView tvBtn = null;

            switch (msg.what) {
            case EventHandler.EVENT_HANDLE_CREATED :
                break;
            case EventHandler.EVENT_HANDLE_ON_TOUCH :             // touch event
                switch (msg.arg1) {
                case EventHandler.EVENT_HANDLE_ON_TOUCH_DOWN :    // touch down
                    tvBtn = (TextView) checkDlg.findViewById(msg.arg2);

                    if (null != tvBtn) {
                        tvBtn.setBackgroundResource(R.drawable.ok_btn_focus);
                    }

                    switch (msg.arg2) {
                    case R.id.tvPrintCheckDlgOkBtn :
                        break;
                    case R.id.tvPrintCheckDlgNoBtn :
                        break;
                    }

                    break;
                case EventHandler.EVENT_HANDLE_ON_TOUCH_UP :      // touch up
                    tvBtn = (TextView) checkDlg.findViewById(msg.arg2);

                    if (null != tvBtn) {
                        tvBtn.setBackgroundResource(R.drawable.ok_btn);
                    }

                    switch (msg.arg2) {
                    case R.id.tvPrintCheckDlgOkBtn :
                        ((CalendarPrintPreviewActivity) PrintActivity).application.setRunPrintView(false);
                        ((CalendarPrintPreviewActivity) PrintActivity).Logout = true;
                        PrintActivity.finish();

                        // sendAppMsg(WND_MSG, WND_CHK_YES, null);
                        break;
                    case R.id.tvPrintCheckDlgNoBtn :
                        showWindow(false);

                        break;
                    }

                    break;
                }
            case EventHandler.EVENT_HANDLE_ON_CLICK :
                break;
            }
        }
    };

    public PrintCheckDialog(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
        PrintActivity = active;
    }

    public Dialog getDialog() {
        return checkDlg;
    }

    public void setCheckCaption(String szCaption) {
        mszCheckInfo = szCaption;
    }

    @Override
    public void showWindow(boolean bShow) {
        if (bShow) {
            WndEvent = new EventHandler(WndHandler);
            checkDlg = new Dialog(super.getApp());

            ColorDrawable drawable = new ColorDrawable(0);

            checkDlg.getWindow().setBackgroundDrawable(drawable);
            checkDlg.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            checkDlg.setContentView(R.layout.print_check_dlg);

            if (null != mszCheckInfo) {
                TextView tv = (TextView) checkDlg.findViewById(R.id.tvPrintCheckDlgCaption);

                tv.setText(mszCheckInfo);
            }

            TextView tvBtn = (TextView) checkDlg.findViewById(R.id.tvPrintCheckDlgOkBtn);

            if (null != tvBtn) {
                WndEvent.registerViewOnTouch(tvBtn);
            }

            tvBtn = (TextView) checkDlg.findViewById(R.id.tvPrintCheckDlgNoBtn);

            if (null != tvBtn) {
                WndEvent.registerViewOnTouch(tvBtn);
            }

            checkDlg.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KEY_BACK == keyCode) {
                        sendAppMsg(SHOW_CHECK_DLG, WND_STOP, null);
                    }

                    return false;
                }
            });
        } else {
            if (checkDlg.isShowing()) {
                checkDlg.cancel();
            }
        }
    }

    @Override
    public void closeWindow() {
        if (checkDlg.isShowing()) {
            checkDlg.cancel();
        }
    }
}

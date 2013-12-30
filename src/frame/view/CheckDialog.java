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

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hp.ij.calendar.R;

import frame.event.EventHandler;
import frame.event.EventMessage;

import frame.window.WndBase;

public class CheckDialog extends WndBase {
    private static final String TAG          = "CheckDialog";
    private static int          mnWndId      = -1;
    private final int           KEY_BACK     = 4;
    private EventHandler        WndEvent     = null;
    private Dialog              checkDlg     = null;
    private String              mszCheckInfo = null;
    private Handler             WndHandler   = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView tvBtn = null;

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
                switch (msg.arg2) {
                case R.id.btnCheckDlgOkBtn :
                    if (EventMessage.SHOW_SYNC_ALARM_DLG == mnWndId) {
                        sendAppMsg(EventMessage.SHOW_SYNC_ALARM_DLG, EventMessage.WND_CLOSE, null);
                    } else {
                        sendAppMsg(WND_MSG, WND_CHK_YES, null);
                    }

                    break;
                case R.id.btnCheckDlgNoBtn :
                    if (EventMessage.SHOW_SYNC_ALARM_DLG == mnWndId) {
                        sendAppMsg(EventMessage.SHOW_SYNC_ALARM_DLG, EventMessage.WND_SYNC, null);
                    } else {
                        sendAppMsg(SHOW_CHECK_DLG, WND_STOP, null);
                    }

                    break;
                }

                break;
            }
        }
    };

    public CheckDialog(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
        mnWndId = nId;
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
            checkDlg.setContentView(R.layout.calendar_check_dlg);

            Button btn = null;

            if (null != mszCheckInfo) {
                TextView tv = (TextView) checkDlg.findViewById(R.id.tvCheckDlgCaption);

                tv.setText(mszCheckInfo);
                tv = null;
            }

            btn = (Button) checkDlg.findViewById(R.id.btnCheckDlgOkBtn);

            if (null != btn) {
                WndEvent.registerViewOnClick(btn);

                if (EventMessage.SHOW_SYNC_ALARM_DLG == mnWndId) {
                    btn.setText(R.string.cancel);
                }
            }

            btn = (Button) checkDlg.findViewById(R.id.btnCheckDlgNoBtn);

            if (null != btn) {
                WndEvent.registerViewOnClick(btn);

                if (EventMessage.SHOW_SYNC_ALARM_DLG == mnWndId) {
                    LayoutParams params = btn.getLayoutParams();

                    params.width = 120;
                    btn.setLayoutParams(params);
                    btn.setText(R.string.try_again);
                    params = null;
                }
            }

            btn = null;

            if (EventMessage.SHOW_SYNC_ALARM_DLG == mnWndId) {
                ImageView ivIcon = (ImageView) checkDlg.findViewById(R.id.ivAlarm);

                if (null != ivIcon) {
                    ivIcon.setVisibility(View.VISIBLE);
                }
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

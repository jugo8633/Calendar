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

import frame.event.EventHandler;

import frame.window.WndBase;

public class AlarmDialog extends WndBase {
    private static final String TAG           = "AlarmDialog";
    private final int           KEY_BACK      = 4;
    private EventHandler        WndEvent      = null;
    private Dialog              alarmDlg      = null;
    private String              m_szAlarmInfo = null;
    private Handler             WndHandler    = new Handler() {
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
                if (msg.arg2 == R.id.btnAlarmBtnOK) {
                    TextView tv = (TextView) alarmDlg.findViewById(R.id.btnAlarmBtnOK);

                    tv.setBackgroundResource(R.drawable.ok_btn_focus);
                    sendAppMsg(SHOW_ALARM_DLG, WND_STOP, null);
                }

                break;
            }
        }
    };

    public AlarmDialog(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
    }

    @Override
    public void showWindow(boolean bShow) {
        if (bShow) {
            WndEvent = new EventHandler(WndHandler);
            alarmDlg = new Dialog(super.getApp());

            ColorDrawable drawable = new ColorDrawable(0);

            alarmDlg.getWindow().setBackgroundDrawable(drawable);
            alarmDlg.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            alarmDlg.setContentView(R.layout.alarm_dialog);

            if (null != m_szAlarmInfo) {
                TextView tv = (TextView) alarmDlg.findViewById(R.id.tvAlarmInfo);

                tv.setText(m_szAlarmInfo);
            }

            TextView tvBtn = (TextView) alarmDlg.findViewById(R.id.btnAlarmBtnOK);

            if (null != tvBtn) {
                WndEvent.registerViewOnClick(tvBtn);
            }

            alarmDlg.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (KEY_BACK == keyCode) {
                        sendAppMsg(SHOW_ALARM_DLG, WND_STOP, null);
                    }

                    return false;
                }
            });

            if (alarmDlg.isShowing()) {
                return;
            }

            try {
                alarmDlg.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (alarmDlg.isShowing()) {
                alarmDlg.cancel();
            }
        }
    }

    public void setAlarmInfo(String szInfo) {
        m_szAlarmInfo = szInfo;
    }

    public Dialog getDialog() {
        return alarmDlg;
    }

    @Override
    public void closeWindow() {

        // TODO Auto-generated method stub
        if (alarmDlg.isShowing()) {
            alarmDlg.cancel();
        }
    }
}

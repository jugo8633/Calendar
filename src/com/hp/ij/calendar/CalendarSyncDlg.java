//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;

import android.graphics.drawable.ColorDrawable;

import android.os.Handler;

import android.view.KeyEvent;
import android.view.Window;

import android.widget.TextView;

public class CalendarSyncDlg extends CalendarWnd {
    private static final String TAG                 = "CalendarSyncDlg";
    public final int            TYPE_FINISH         = 1;
    public final int            TYPE_RUN            = 0;
    private final int           m_nMainLayoutResId  = R.layout.calendar_sync_dlg;
    private final int           m_nMainLayout2ResId = R.layout.calendar_sync_finish_dlg;
    private Dialog              syncDlg             = null;

    public CalendarSyncDlg(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
    }

    public void initDialog(int nType) {
        if ((TYPE_RUN != nType) && (TYPE_FINISH != nType)) {
            return;
        }

        if (null != syncDlg) {
            syncDlg = null;
        }

        syncDlg = new Dialog(super.getApp());

        ColorDrawable drawable = new ColorDrawable(254);

        syncDlg.getWindow().setBackgroundDrawable(drawable);
        syncDlg.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        switch (nType) {
        case TYPE_RUN :
            application.setReflashMonthEnable(true);
            syncDlg.setContentView(m_nMainLayoutResId);

            break;
        case TYPE_FINISH :
            syncDlg.setContentView(m_nMainLayout2ResId);

            TextView tvTmp = (TextView) syncDlg.findViewById(R.id.tvSyncFinishBtnOk);

            super.RegisterEvent(tvTmp, super.EVENT_TOUCH);

            break;
        default :
            return;
        }

        syncDlg.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    if (0 == application.getSyncWndType()) {
                        application.setReflashMonthEnable(false);
                    }

                    application.setSyncWndType(-1);
                    sendAppMsg(WND_MSG, WND_STOP, null);
                }

                return true;
            }
        });
    }

    public Dialog getDialog() {
        return syncDlg;
    }

    @Override
    protected void onClick(int nResId) {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onClose() {}

    @Override
    protected void onShow() {}

    @Override
    protected void onTouchDown(int nResId) {
        switch (nResId) {
        case R.id.tvSyncFinishBtnOk :
            TextView tvTmp = null;

            tvTmp = (TextView) syncDlg.findViewById(R.id.tvSyncFinishBtnOk);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.ok_btn_focus);
            }

            break;
        }
    }

    @Override
    protected void onTouchUp(int nResId) {
        switch (nResId) {
        case R.id.tvSyncFinishBtnOk :
            TextView tvTmp = null;

            tvTmp = (TextView) syncDlg.findViewById(R.id.tvSyncFinishBtnOk);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.ok_btn);
            }

            application.setReflashMonthEnable(true);
            sendAppMsg(WND_MSG, WND_STOP, null);

            break;
        }
    }

    public void setRunType(int nType) {
        application.setSyncWndType(nType);
    }

    public int getRunType() {
        return application.getSyncWndType();
    }
}

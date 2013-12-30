//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.dialog;


import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;

import android.graphics.drawable.ColorDrawable;

import android.view.KeyEvent;
import android.view.Window;

public class DialogFactory {
    private Dialog   dialog     = null;
    private int      mnDialogId = -1;
    private Activity mActivity  = null;

    public DialogFactory(Activity activity, int nDialogId, boolean bHandleBackKey) {
        mActivity  = activity;
        mnDialogId = nDialogId;
        dialog     = new Dialog(activity);
        initDialog(nDialogId, bHandleBackKey);

        if (bHandleBackKey) {
            initBackKeyHandle(activity, nDialogId);
        }
    }

    private void initDialog(int nDialogId, boolean bHandleBackKey) {
        ColorDrawable drawable = new ColorDrawable(254);

        dialog.getWindow().setBackgroundDrawable(drawable);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setCntent(int nLayoutResID) {
        if (null != dialog) {
            dialog.setContentView(nLayoutResID);
        }
    }

    private void initBackKeyHandle(final Activity active, final int nDialogId) {
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    active.removeDialog(nDialogId);
                }

                return true;
            }
        });
    }

    public void removeDialog() {
        if ((null != mActivity) && (0 < mnDialogId)) {
            mActivity.removeDialog(mnDialogId);
        }
    }
}

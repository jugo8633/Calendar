//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Activity;

import android.content.Context;

import android.os.Handler;

import android.view.inputmethod.InputMethodManager;

import android.widget.ImageView;
import android.widget.TextView;

import com.hp.ij.calendar.controler.CalendarLoginEditTextControl;

import frame.event.EventMessage;

import frame.view.SharedData;

public class CalendarLoginWnd extends CalendarWnd {
    private static final String          TAG                          = "CalendarLoginWnd";
    private final int                    m_nMainLayoutResId           = R.layout.calendar_login;
    private CalendarLoginEditTextControl calendarLoginEditTextControl = null;

    public CalendarLoginWnd(Activity activity, Handler handler, int id) {
        super(activity, handler, id);
        super.setLayoutResId(m_nMainLayoutResId, HIDE_VIEW, HIDE_VIEW);
        super.setViewClickEvent(R.id.btnLoginOK);
        super.setViewClickEvent(R.id.ivLoginPwdChkBox);
        calendarLoginEditTextControl = new CalendarLoginEditTextControl(activity);
    }

    @Override
    protected void onShow() {
        ImageView iv = (ImageView) super.getApp().findViewById(R.id.ivLoginPwdChkBox);

        if (application.IsSavePWD()) {
            iv.setImageResource(R.drawable.checkbox_focus);
        } else {
            iv.setImageResource(R.drawable.checkbox);
        }

        iv = null;
        calendarLoginEditTextControl.initEditText();
    }

    @Override
    protected void onClose() {}

    @Override
    protected void onClick(int resId) {
        switch (resId) {
        case R.id.ivLoginPwdChkBox :
            setPasswordSave();

            break;
        case R.id.btnLoginOK :
            hideKeyboard();

            String szAccount  = null;
            String szPassword = null;

            szAccount  = calendarLoginEditTextControl.getAccount();
            szPassword = calendarLoginEditTextControl.getPassword();

            if ((null == szAccount) || (0 >= szAccount.length()) || (null == szPassword)
                    || (0 >= szPassword.length())) {
                String szAlarm = application.getActivity().getString(R.string.Unable_2_recognize_username_password);

                application.getActivity().showAlarmDlg(true, null, szAlarm);

                return;
            }

            sendAppMsg(WND_MSG, RUN_PROGWND, null);

            String[] aszLogin = new String[3];

            aszLogin[0] = szAccount;
            aszLogin[1] = szPassword;

            if (application.IsSavePWD()) {
                aszLogin[2] = EventMessage.TRUE;
            } else {
                aszLogin[2] = EventMessage.FALSE;
            }

            sendAppMsg(WND_MSG, WND_BTN_OK, aszLogin);

            break;
        }
    }

    @Override
    protected void onTouchDown(int resId) {}

    @Override
    protected void onTouchUp(int resId) {}

    private void setPasswordSave() {
        ImageView iv = (ImageView) super.getApp().findViewById(R.id.ivLoginPwdChkBox);

        if (iv == null) {
            return;
        }

        if (application.IsSavePWD()) {
            application.setSavePWD(false);
            iv.setImageResource(R.drawable.checkbox);
        } else {
            application.setSavePWD(true);
            iv.setImageResource(R.drawable.checkbox_focus);
        }

        iv = null;
    }

    public void initConf(boolean bIsSaveAccount, String szAccount, String szPassword) {
        application.setSavePWD(bIsSaveAccount);
        calendarLoginEditTextControl.setAccount(szAccount);

        if (bIsSaveAccount) {
            ImageView iv = (ImageView) super.getApp().findViewById(R.id.ivLoginPwdChkBox);

            if (null != iv) {
                iv.setImageResource(R.drawable.checkbox_focus);
            }

            calendarLoginEditTextControl.setPassword(szPassword);
        }
    }

    private void hideKeyboard() {
        InputMethodManager mgr = (InputMethodManager) super.getApp().getSystemService(Context.INPUT_METHOD_SERVICE);

        /**
         * account edit view
         */
        TextView tvTmp = (TextView) super.getApp().findViewById(R.id.etLoginAccount);

        if (null != tvTmp) {
            mgr.hideSoftInputFromWindow(tvTmp.getWindowToken(), 0);
        }

        /**
         * password edit view
         */
        tvTmp = (TextView) super.getApp().findViewById(R.id.etLoginPassword);

        if (null != tvTmp) {
            mgr.hideSoftInputFromWindow(tvTmp.getWindowToken(), 0);
        }
    }

    public void initCalendarLoginWnd() {
        String szAccount = getSavedAccount();

        if (isSaveAccount()) {
            String szPassword = getSavedPassword();

            if ((null == szAccount) && (null == szPassword)) {
                return;
            } else if (null == szAccount) {
                return;
            }

            initConf(true, szAccount, szPassword);
        } else {
            if (null == szAccount) {
                return;
            }

            initConf(false, szAccount, null);
        }
    }

    private boolean isSaveAccount() {
        boolean    bSave = false;
        SharedData sdata = new SharedData(super.getApp(), null, 0);

        sdata.initPreferences("CalendarConf");

        String szIsSave = sdata.getValue("SAVEACCOUNT");

        sdata.closeWindow();
        sdata = null;

        if (null == szIsSave) {
            return false;
        }

        if (szIsSave.equals(EventMessage.TRUE)) {
            bSave = true;
        } else {
            bSave = false;
        }

        return bSave;
    }

    private String getSavedAccount() {
        SharedData sdata = new SharedData(super.getApp(), null, 0);

        sdata.initPreferences("CalendarConf");

        String szAccount = sdata.getValue("ACCOUNT");

        sdata.closeWindow();
        sdata = null;

        return szAccount;
    }

    private String getSavedPassword() {
        SharedData sdata = new SharedData(super.getApp(), null, 0);

        sdata.initPreferences("CalendarConf");

        String szPassword = sdata.getValue("PASSWORD");

        sdata.closeWindow();
        sdata = null;

        return szPassword;
    }

    public void clearEditSelected() {
        calendarLoginEditTextControl.clearEditSelected();
    }
}

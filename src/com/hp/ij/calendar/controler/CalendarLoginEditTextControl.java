//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import android.app.Activity;

import android.text.method.PasswordTransformationMethod;

import android.widget.EditText;

import com.hp.ij.calendar.R;

public class CalendarLoginEditTextControl extends EditTextControl {
    private static final String TAG                   = "CalendarLoginEditTextControl";
    private EditText            metAccount            = null;
    private EditText            metPassword           = null;
    private String              mszAccount            = null;
    private String              mszPassword           = null;
    private int                 mEditAccountSelStart  = -1;
    private int                 mEditPasswordSelStart = -1;
    private final int           ID_ACCOUNT            = R.id.etLoginAccount;
    private final int           ID_PASSWORD           = R.id.etLoginPassword;

    public CalendarLoginEditTextControl(Activity activity) {
        super(activity);

        // TODO Auto-generated constructor stub
    }

    public void initEditText() {
        metAccount  = getEditText(ID_ACCOUNT);
        metPassword = getEditText(ID_PASSWORD);

        if ((null != metAccount) && (null != metPassword)) {
            clearEditFocus();

            /**
             * initial password edit to display password character.
             */
            metPassword.setTransformationMethod(new PasswordTransformationMethod());

            if (null != mszAccount) {
                metAccount.setText(mszAccount);
            }

            if (null != mszPassword) {
                metPassword.setText(mszPassword);
            }

            if ((-1 != mEditAccountSelStart) && (null != mszAccount)) {
                metAccount.setSelection(mszAccount.length());
                metAccount.requestFocus();
            } else if ((-1 != mEditPasswordSelStart) && (null != mszPassword)) {
                metPassword.setSelection(mszPassword.length());
                metPassword.requestFocus();
            }

            initTextWatcher(metAccount);
            initTextWatcher(metPassword);
        }
    }

    public void setAccount(String szAccount) {
        mszAccount = szAccount;
    }

    public void setPassword(String szPassword) {
        mszPassword = szPassword;
    }

    public String getAccount() {
        return mszAccount;
    }

    public String getPassword() {
        return mszPassword;
    }

    @Override
    protected void onEditTextChanged(int nResId, String s) {

        // TODO Auto-generated method stub
        switch (nResId) {
        case ID_ACCOUNT :
            mszAccount = s;

            break;
        case ID_PASSWORD :
            mszPassword = s;

            break;
        }
    }

    @Override
    protected void onEditTextFocusChange(int nResId, boolean hasFocus) {

        // TODO Auto-generated method stub
        switch (nResId) {
        case ID_ACCOUNT :
            if (hasFocus) {
                mEditAccountSelStart = metAccount.getSelectionStart();
            } else {
                mEditAccountSelStart = -1;
            }

            break;
        case ID_PASSWORD :
            if (hasFocus) {
                mEditPasswordSelStart = metPassword.getSelectionStart();
            } else {
                mEditPasswordSelStart = -1;
            }

            break;
        }
    }

    public void clearEditFocus() {
        metAccount.setText(null);
        metPassword.setText(null);
    }

    public void clearEditSelected() {
        mEditAccountSelStart  = -1;
        mEditPasswordSelStart = -1;
    }
}

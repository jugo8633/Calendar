//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import android.app.Activity;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.View;
import android.view.View.OnFocusChangeListener;

import android.widget.EditText;

public abstract class EditTextControl {
    private static final String TAG       = "EditTextControl";
    private Activity            mActivity = null;

    public EditTextControl(Activity activity) {
        mActivity = activity;
    }

    protected EditText getEditText(int nResId) {
        if (null == mActivity) {
            return null;
        }

        EditText editText = null;

        editText = (EditText) mActivity.findViewById(nResId);

        return editText;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    protected abstract void onEditTextChanged(int nResId, String s);

    protected abstract void onEditTextFocusChange(int nResId, boolean hasFocus);

    protected void initTextWatcher(final EditText editText) {
        TextWatcher textWatcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != s) {
                    onEditTextChanged(editText.getId(), s.toString());
                }
            }
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };

        editText.addTextChangedListener(textWatcher);
        editText.setOnFocusChangeListener(FocusChangeListener);
    }

    OnFocusChangeListener FocusChangeListener = new OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {

            // TODO Auto-generated method stub
            int nId = v.getId();

            onEditTextFocusChange(nId, hasFocus);
        }
    };
}

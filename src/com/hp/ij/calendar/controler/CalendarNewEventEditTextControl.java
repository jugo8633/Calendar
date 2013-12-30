//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import android.app.Activity;

import android.widget.EditText;

import com.hp.ij.calendar.R;

public class CalendarNewEventEditTextControl extends EditTextControl {
    private static final String TAG                      = "CalendarNewEventEditTextControl";
    private EditText            metEventTitle            = null;
    private EditText            metWhere                 = null;
    private EditText            metDescription           = null;
    private String              mszDescription           = null;
    private String              mszEventTitle            = null;
    private String              mszWhere                 = null;
    private int                 mEditEventTitleSelStart  = -1;
    private int                 mEditWhereSelStart       = -1;
    private int                 mEditDescriptionSelStart = -1;
    private final int           ID_EVENT_TITLE           = R.id.etNewEventTitle;
    private final int           ID_WHERE                 = R.id.etNewEventWhere;
    private final int           ID_DESCRIPTION           = R.id.etNewEventDescription;

    public CalendarNewEventEditTextControl(Activity activity) {
        super(activity);

        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onEditTextChanged(int nResId, String s) {

        // TODO Auto-generated method stub
        switch (nResId) {
        case ID_EVENT_TITLE :
            mszEventTitle = s;

            break;
        case ID_WHERE :
            mszWhere = s;

            break;
        case ID_DESCRIPTION :
            mszDescription = s;

            break;
        }
    }

    @Override
    protected void onEditTextFocusChange(int nResId, boolean hasFocus) {

        // TODO Auto-generated method stub
        switch (nResId) {
        case ID_EVENT_TITLE :
            if (hasFocus) {
                mEditEventTitleSelStart = metEventTitle.getSelectionStart();
            } else {
                mEditEventTitleSelStart = -1;
            }

            break;
        case ID_WHERE :
            if (hasFocus) {
                mEditWhereSelStart = metWhere.getSelectionStart();
            } else {
                mEditWhereSelStart = -1;
            }

            break;
        case ID_DESCRIPTION :
            if (hasFocus) {
                mEditDescriptionSelStart = metDescription.getSelectionStart();
            } else {
                mEditDescriptionSelStart = -1;
            }

            break;
        }
    }

    public void initEditText() {
        metEventTitle  = getEditText(ID_EVENT_TITLE);
        metWhere       = getEditText(ID_WHERE);
        metDescription = getEditText(ID_DESCRIPTION);

        if ((null != metEventTitle) && (null != metWhere) && (null != metDescription)) {
            clearEditFocus();

            if (null != mszEventTitle) {
                metEventTitle.setText(mszEventTitle);
            }

            if (null != mszWhere) {
                metWhere.setText(mszWhere);
            }

            if (null != mszDescription) {
                metDescription.setText(mszDescription);
            }

            if ((-1 != mEditEventTitleSelStart) && (null != mszEventTitle)) {
                metEventTitle.setSelection(mszEventTitle.length());
                metEventTitle.requestFocus();
            } else if ((-1 != mEditWhereSelStart) && (null != mszWhere)) {
                metWhere.setSelection(mszWhere.length());
                metWhere.requestFocus();
            } else if ((-1 != mEditDescriptionSelStart) && (null != mszDescription)) {
                metDescription.setSelection(mszDescription.length());
                metDescription.requestFocus();
            }

            initTextWatcher(metEventTitle);
            initTextWatcher(metWhere);
            initTextWatcher(metDescription);
        }
    }

    public void setEventTitle(String szEventTitle) {
        mszEventTitle = szEventTitle;
    }

    public void setWhere(String szWhere) {
        mszWhere = szWhere;
    }

    public void setDescription(String szDescription) {
        mszDescription = szDescription;
    }

    public String getEventTitle() {
        return mszEventTitle;
    }

    public String getWhere() {
        return mszWhere;
    }

    public String getDescription() {
        return mszDescription;
    }

    public void clearEditFocus() {
        metEventTitle.setText(null);
        metWhere.setText(null);
        metDescription.setText(null);
    }

    public void clearEditSelected() {
        mEditEventTitleSelStart  = -1;
        mEditWhereSelStart       = -1;
        mEditDescriptionSelStart = -1;
    }
}

package com.hp.ij.calendar;


import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;

import android.os.Handler;

import android.widget.Button;
import android.widget.HpPickerButton;
import android.widget.HpToggleButton;
import android.widget.HpToggleGroup;

import com.hp.ij.calendar.dialog.DialogFactory;

import frame.event.EventMessage;

public class CalendarTimeWnd extends CalendarWnd {
    private static final String TAG                = "CalendarTimeWnd";
    private DialogFactory       dialog             = null;
    private final int           m_nMainLayoutResId = R.layout.calendar_time_dialog;
    private HpPickerButton      pickBtnHour        = null;
    private HpPickerButton      pickBtnMinute      = null;
    private HpToggleGroup       toggleGroupAMPM    = null;
    private HpToggleButton      toggleBtnAM        = null;
    private HpToggleButton      toggleBtnPM        = null;
    private int                 mCallingResId      = -1;
    private final String[]      PICKER_HOUR        = new String[] {
        "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"
    };
    private final String[]      PICKER_MINUTE      = new String[] { "00", "15", "30", "45" };

    public CalendarTimeWnd(Activity active, Handler handler, int nId) {
        super(active, handler, nId);

        // super.setLayoutResId(m_nMainLayoutResId, HIDE_VIEW, HIDE_VIEW);
        // TODO Auto-generated constructor stub
        dialog = new DialogFactory(getActivity(), nId, true);
        initDialog();
    }

    @Override
    protected void onClick(int nResId) {

        // TODO Auto-generated method stub
        switch (nResId) {
        case R.id.btnTimeOK :
            ArrayList<Integer> arData = new ArrayList<Integer>();

            arData.add(getCallingResourceId());
            arData.add(getPickerHour());
            arData.add(getPickerMinute());
            arData.add(getSelectedAMPM());
            this.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_BTN_OK, arData);
            arData = null;
            dialog.removeDialog();

            break;
        case R.id.btnTimeCancel :
            dialog.removeDialog();

            break;
        }
    }

    @Override
    protected void onClose() {

        // TODO Auto-generated method stub
        pickBtnHour   = null;
        pickBtnMinute = null;
        dialog        = null;
    }

    @Override
    protected void onShow() {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onTouchDown(int nResId) {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onTouchUp(int nResId) {

        // TODO Auto-generated method stub
    }

    public void initDialog() {
        dialog.setCntent(m_nMainLayoutResId);
        pickBtnHour     = (HpPickerButton) getDialog().findViewById(R.id.pickHour);
        pickBtnMinute   = (HpPickerButton) getDialog().findViewById(R.id.pickMinute);
        toggleBtnAM     = (HpToggleButton) getDialog().findViewById(R.id.toggleBtnAM);
        toggleBtnPM     = (HpToggleButton) getDialog().findViewById(R.id.toggleBtnPM);
        toggleGroupAMPM = (HpToggleGroup) getDialog().findViewById(R.id.tgAMPMMain);

        Button btnTime = (Button) dialog.getDialog().findViewById(R.id.btnTimeCancel);

        if (null != btnTime) {
            super.RegisterEvent(btnTime, EVENT_CLICK);
        }

        btnTime = (Button) dialog.getDialog().findViewById(R.id.btnTimeOK);

        if (null != btnTime) {
            super.RegisterEvent(btnTime, EVENT_CLICK);
        }

        btnTime = null;
        initPickerButton();
    }

    public Dialog getDialog() {
        return dialog.getDialog();
    }

    private void initPickerButton() {
        if (null != pickBtnHour) {
            pickBtnHour.setRange(0, 11, PICKER_HOUR);
        }

        if (null != pickBtnMinute) {
            pickBtnMinute.setRange(0, 3, PICKER_MINUTE);
        }
    }

    public void setPickerTime(int nHourIndex, int nMinuteIndex) {
        if ((null != pickBtnHour) && (0 <= nHourIndex) && (nHourIndex < 12)) {
            pickBtnHour.setCurrent(nHourIndex);
        }

        if ((null != pickBtnMinute) && (0 <= nMinuteIndex) && (nMinuteIndex < 4)) {
            pickBtnMinute.setCurrent(nMinuteIndex);
        }
    }

    public int getPickerHour() {
        if (null != pickBtnHour) {
            int    nIndex = pickBtnHour.getCurrent();
            String szHour = PICKER_HOUR[nIndex];

            return Integer.valueOf(szHour);
        }

        return 0;
    }

    public int getPickerMinute() {
        if (null != pickBtnMinute) {
            int    nIndex   = pickBtnMinute.getCurrent();
            String szMinute = PICKER_MINUTE[nIndex];

            return Integer.valueOf(szMinute);
        }

        return 0;
    }

    public void setAMPM(int nNoon) {
        if ((null != toggleBtnAM) && (null != toggleBtnPM)) {
            if (Calendar.AM == nNoon) {
                toggleBtnPM.setChecked(false);
                toggleBtnAM.setChecked(true);
            } else if (Calendar.PM == nNoon) {
                toggleBtnAM.setChecked(false);
                toggleBtnPM.setChecked(true);
            }
        }
    }

    public int getSelectedAMPM() {
        if (null != toggleGroupAMPM) {
            int nCheckedResId = toggleGroupAMPM.getCheckedToggleButtonId();

            switch (nCheckedResId) {
            case R.id.toggleBtnAM :
                return Calendar.AM;
            case R.id.toggleBtnPM :
                return Calendar.PM;
            }
        }

        return -1;
    }

    public void setPickerTime(int nHour, int nMinute, int nNoon) {
        if ((1 <= nHour) && (12 >= nHour)) {
            pickBtnHour.setCurrent(nHour - 1);
        }

        if ((0 <= nMinute) && (59 >= nMinute)) {
            int nIndex = nMinute / 15;

            pickBtnMinute.setCurrent(nIndex);
        }

        setAMPM(nNoon);
    }

    public void setCallingResourceId(int nResourceId) {
        mCallingResId = nResourceId;
    }

    public int getCallingResourceId() {
        return mCallingResId;
    }
}

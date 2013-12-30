//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.Calendar;

import android.app.Activity;

import android.content.Context;

import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hp.ij.calendar.controler.CalendarNewEventEditTextControl;
import com.hp.ij.calendar.data.CalendarNewEventData;

import frame.event.EventMessage;

import frame.view.SpinnerList;

public class CalendarNewEventWnd extends CalendarWnd {
    private static final String             TAG                             = "CalendarNewEventWnd";
    public final int                        UPDATE_DATE                     = 0;
    public final int                        UPDATE_TIME                     = 1;
    private CalendarNewEventData            mCalendarNewEventData           = null;
    private int                             mnEventIndex                    = -1;
    private int                             mnType                          = -1;
    private SpinnerList                     spinListRecurrence              = null;
    private SpinnerList                     spinListReminder                = null;
    private String                          szHeaderTitle                   = null;
    private CalendarNewEventEditTextControl calendarNewEventEditTextControl = null;
    private int                             mnMainLayoutResId               = R.layout.calendar_new_event;
    private final int                       mnHeaderLayoutResId             = R.layout.calendar_header_contextual;
    private final int                       mnFootLayoutResId               = R.layout.footbar_check_menu;
    private Button                          mBtnTimeFrom                    = null;
    private Button                          mBtnTimeTo                      = null;
    private static CalendarTimeData         calendarTimeDataFrom            = null;
    private static CalendarTimeData         calendarTimeDataTo              = null;
    private static CalendarDateData         calendarDateDataFrom            = null;
    private static CalendarDateData         calendarDateDataTo              = null;

    /**
     * string array for recurrence and reminder
     */
    private final String[] Recurrence = {
        "No Recurrence", "Daily", "Every Weekday", "Every Mon. Wed. and Fri", "Every Tues. and Thurs.", "Weekly",
        "Monthly", "Yearly"
    };
    private final String[] Reminder = {
        "No Reminder", "5 min before", "15 min before", "30 min before", "1 hour before", "2 hour before",
        "1 day before", "2 days before", "On day of event"
    };
    private final String[] ReminderCustom = {
        "No Reminder", "5 min before", "15 min before", "30 min before", "1 hour before", "2 hour before",
        "1 day before", "2 days before", "On day of event", null
    };

    /**
     * handler for spinner list
     */
    Handler mSpinnerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int nSelectedIndex = msg.arg1;

            /**
             * if msg.arg1 = -1, it is spinner clicked on text edit not click on list item
             */
            if (-1 == nSelectedIndex) {
                hideKeyboard();
            }

            switch (msg.arg2) {
            case R.id.tvSpinnerEditRecurrence :
                application.setRecurrenceSelected(nSelectedIndex);

                break;
            case R.id.tvSpinnerEditReminder :
                application.setReminderSelected(nSelectedIndex);

                break;
            }

            super.handleMessage(msg);
        }
    };

    public CalendarNewEventWnd(Activity activity, Handler handler, int id) {
        super(activity, handler, id);
        mnType = id;

        switch (id) {
        case EventMessage.RUN_NEWEVENTWND :
            szHeaderTitle = super.getApp().getString(R.string.add_new_event);

            break;
        case EventMessage.RUN_EDITEVENTWND :
            szHeaderTitle = super.getApp().getString(R.string.edit_event);

            break;
        }

        calendarDateDataFrom = new CalendarDateData();
        calendarDateDataTo   = new CalendarDateData();
        calendarTimeDataFrom = new CalendarTimeData();
        calendarTimeDataTo   = new CalendarTimeData();
        initCalendarNewEventWnd();
        super.setViewTouchEvent(R.id.tvCalendarHeaderLogout);
        super.setViewTouchEvent(R.id.tvCalendarHeaderBack);
        super.setViewTouchEvent(R.id.tvFootbarChkMenuOK);
        super.setViewClickEvent(R.id.ivNewEventAllDayChkBox);
        super.setViewClickEvent(R.id.tvNewEventAllChkBox);
        super.setViewTouchEvent(R.id.tvNewEventFromDate);
        super.setViewTouchEvent(R.id.tvNewEventToDate);
        super.setViewTouchEvent(R.id.etNewEventTitle);
        super.setViewTouchEvent(R.id.etNewEventWhere);
        super.setViewTouchEvent(R.id.etNewEventDescription);
        super.setViewClickEvent(R.id.btnNewEventTimeFrom);
        super.setViewClickEvent(R.id.btnNewEventTimeTo);
        mCalendarNewEventData           = new CalendarNewEventData(application.getCalendarData());
        calendarNewEventEditTextControl = new CalendarNewEventEditTextControl(activity);
    }

    public void initCalendarNewEventWnd() {
        int nOrientation = application.getOrientation();

        if (application.ORIENTATION_LAND == nOrientation) {
            mnMainLayoutResId = R.layout.calendar_new_event;
        } else {
            mnMainLayoutResId = R.layout.calendar_new_event_v;
        }

        super.setLayoutResId(mnMainLayoutResId, mnFootLayoutResId, mnHeaderLayoutResId);
    }

    @Override
    protected void onShow() {

        /**
         * initial time button
         */
        mBtnTimeFrom = (Button) application.getActivity().findViewById(R.id.btnNewEventTimeFrom);
        mBtnTimeTo   = (Button) application.getActivity().findViewById(R.id.btnNewEventTimeTo);
        setHeaderTitle(szHeaderTitle);

        if (EventMessage.RUN_EDITEVENTWND == mnType) {    // Edit Event mode
            mnEventIndex = application.getEventData().getCurrDayEventSelected();
            initEditState();
            initEditSpinner();
        } else {                                          // New Event mode
            mnEventIndex = -1;
            initState();
            initSpinner();
        }

        initDateField();
        initTimeButton();
        calendarNewEventEditTextControl.initEditText();
    }

    @Override
    protected void onClose() {
        mCalendarNewEventData           = null;
        calendarNewEventEditTextControl = null;
    }

    @Override
    protected void onClick(int resId) {
        switch (resId) {
        case R.id.ivNewEventAllDayChkBox :
        case R.id.tvNewEventAllChkBox :
            setAllDayCheck();

            break;
        case R.id.btnNewEventTimeFrom :
        case R.id.btnNewEventTimeTo :
            showTimeDialog(resId);

            break;
        }
    }

    @Override
    protected void onTouchDown(int resId) {
        TextView tvTmp = null;

        switch (resId) {
        case R.id.tvCalendarHeaderLogout :
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvCalendarHeaderLogout);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.ok_btn_focus);
            }

            break;
        case R.id.tvCalendarHeaderBack :
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvCalendarHeaderBack);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.btn_header_focus);
            }

            break;
        case R.id.tvFootbarChkMenuOK :
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvFootbarChkMenuOK);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.ok_btn_focus);
            }

            break;
        case R.id.tvNewEventFromDate :
            application.setDateShowFromResId(R.id.tvNewEventFromDate);
            sendAppMsg(WND_MSG, EventMessage.WND_SHOW_DATE, null);

            break;
        case R.id.tvNewEventToDate :
            application.setDateShowFromResId(R.id.tvNewEventToDate);
            sendAppMsg(WND_MSG, EventMessage.WND_SHOW_DATE, null);

            break;
        case R.id.etNewEventTitle :
        case R.id.etNewEventWhere :
        case R.id.etNewEventDescription :
            hideAllSpinnerDropList();
            tvTmp = (TextView) super.getApp().findViewById(resId);
            tvTmp.requestFocus();
            showKeyboard(tvTmp);

            break;
        }
    }

    @Override
    protected void onTouchUp(int resId) {
        TextView tvTmp = null;

        switch (resId) {
        case R.id.tvCalendarHeaderLogout :    // done
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvCalendarHeaderLogout);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.ok_btn);
            }

            sendAppMsg(WND_MSG, EventMessage.WND_FINISH, null);

            break;
        case R.id.tvCalendarHeaderBack :
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvCalendarHeaderBack);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.btn_header);
            }

            hideKeyboard();
            sendAppMsg(WND_MSG, EventMessage.WND_STOP, null);

            break;
        case R.id.tvFootbarChkMenuOK :
            tvTmp = (TextView) super.getApp().findViewById(R.id.tvFootbarChkMenuOK);

            if (null != tvTmp) {
                tvTmp.setBackgroundResource(R.drawable.ok_btn);
            }

            if (EventMessage.RUN_EDITEVENTWND == mnType) {
                sendAppMsg(WND_MSG, EventMessage.WND_EDIT_EVENT, null);
            } else {
                sendAppMsg(WND_MSG, EventMessage.WND_NEW_EVENT, null);
            }

            break;
        }
    }

    private void showTimeDialog(int nResId) {
        int nHour   = 0;
        int nMinute = 0;
        int nNoon   = Calendar.AM;

        switch (nResId) {
        case R.id.btnNewEventTimeFrom :
            if (null != calendarTimeDataFrom) {
                nHour   = calendarTimeDataFrom.getHour();
                nMinute = calendarTimeDataFrom.getMinute();
                nNoon   = calendarTimeDataFrom.getNoon();
            }

            break;
        case R.id.btnNewEventTimeTo :
            if (null != calendarTimeDataTo) {
                nHour   = calendarTimeDataTo.getHour();
                nMinute = calendarTimeDataTo.getMinute();
                nNoon   = calendarTimeDataTo.getNoon();
            }

            break;
        default :
            return;
        }

        CalendarTimeWnd calendarTimeWnd = (CalendarTimeWnd) application.getWindow(EventMessage.RUN_TIMEWND);

        calendarTimeWnd.setCallingResourceId(nResId);
        calendarTimeWnd.setPickerTime(nHour, nMinute, nNoon);
        calendarTimeWnd = null;
        sendAppMsg(WND_MSG, EventMessage.RUN_TIMEWND, null);
    }

    private void setTimeButton(Button button, int nHour, int nMinute, boolean bAM) {
        if (null == button) {
            return;
        }

        String szTime = String.format("%d:%02d%s", nHour, nMinute, bAM
                ? "AM"
                : "PM");

        button.setText(szTime);
    }

    private void initDateField() {
        TextView tvDate = null;
        String   szDate = null;

        if (null != calendarDateDataFrom) {
            szDate = String.format("%d/%d/%d", calendarDateDataFrom.getMonth(), calendarDateDataFrom.getDay(),
                                   calendarDateDataFrom.getYear());
            tvDate = (TextView) application.getActivity().findViewById(R.id.tvNewEventFromDate);

            if (null != tvDate) {
                tvDate.setText(szDate);
            }
        }

        if (null != calendarDateDataTo) {
            szDate = String.format("%d/%d/%d", calendarDateDataTo.getMonth(), calendarDateDataTo.getDay(),
                                   calendarDateDataTo.getYear());
            tvDate = (TextView) application.getActivity().findViewById(R.id.tvNewEventToDate);

            if (null != tvDate) {
                tvDate.setText(szDate);
            }
        }

        tvDate = null;
        szDate = null;
    }

    private void initTimeButton() {
        boolean bAM = true;

        if (null != calendarTimeDataFrom) {
            if (Calendar.PM == calendarTimeDataFrom.getNoon()) {
                bAM = false;
            }

            setTimeButton(mBtnTimeFrom, calendarTimeDataFrom.getHour(), calendarTimeDataFrom.getMinute(), bAM);
        }

        if (null != calendarTimeDataTo) {
            bAM = true;

            if (Calendar.PM == calendarTimeDataTo.getNoon()) {
                bAM = false;
            }

            setTimeButton(mBtnTimeTo, calendarTimeDataTo.getHour(), calendarTimeDataTo.getMinute(), bAM);
        }
    }

    private int transTimeMinute(int nMinute) {
        if (0 >= nMinute) {
            return 0;
        }

        int nTmp    = 0;
        int nResult = 0;

        nTmp = nMinute / 15;

        int nTmp2 = nMinute % 15;

        if (0 < nTmp2) {
            nResult = 15 * (nTmp + 1);
        } else {
            nResult = 15 * nTmp;
        }

        return nResult;
    }

    private void setAllDayCheck() {
        ImageView ivTmp = (ImageView) super.getApp().findViewById(R.id.ivNewEventAllDayChkBox);

        if (null == ivTmp) {
            return;
        }

        boolean bAllChked = false;

        if (-1 == mnEventIndex) {    // new event
            bAllChked = application.getIsAllDayCheck();
        } else {
            bAllChked = application.getEventData().getDetailAllDay(mnEventIndex);
        }

        if (bAllChked) {
            if (-1 == mnEventIndex) {    // new event
                application.setAllDayCheck(false);
            } else {
                application.getEventData().setDetailAllDay(mnEventIndex, false);
            }

            showTimeView(true);
            ivTmp.setImageResource(R.drawable.checkbox);
        } else {
            if (-1 == mnEventIndex) {    // new event
                application.setAllDayCheck(true);
            } else {
                application.getEventData().setDetailAllDay(mnEventIndex, true);
            }

            showTimeView(false);
            ivTmp.setImageResource(R.drawable.checkbox_focus);
        }
    }

    public void initEditText(String szEventTitle, String szWhere, String szDescription) {
        if (null != calendarNewEventEditTextControl) {

            // event title
            calendarNewEventEditTextControl.setEventTitle(szEventTitle);

            // Where
            calendarNewEventEditTextControl.setWhere(szWhere);

            // szDescription
            calendarNewEventEditTextControl.setDescription(szDescription);
        }
    }

    private void initSpinner() {
        RelativeLayout base                = null;
        int            nRecurrenceSelected = 0;
        int            nReminderSelected   = 0;

        nRecurrenceSelected = application.getRecurrenceSelected();
        nReminderSelected   = application.getReminderSelected();

        if (0 > nRecurrenceSelected) {
            nRecurrenceSelected = 0;
            application.setRecurrenceSelected(nRecurrenceSelected);
        }

        if (0 > nReminderSelected) {
            nReminderSelected = 0;
            application.setReminderSelected(nReminderSelected);
        }

        base               = (RelativeLayout) super.getApp().findViewById(R.id.rlNewEventRecurrence);
        spinListRecurrence = new SpinnerList(super.getApp(), R.layout.spinner_list_recurrence, base,
                R.id.tvSpinnerEditRecurrence, R.id.flipperSpinnerListRecurrence, mSpinnerHandler);
        spinListRecurrence.initListView(R.id.lvSpinnerMenuRecurrence, Recurrence,
                                        R.layout.spinner_list_item_recurrence, R.id.tvSpinnerItemRecurrence);
        spinListRecurrence.setSelected(nRecurrenceSelected, 0);
        base             = (RelativeLayout) super.getApp().findViewById(R.id.rlNewEventReminder);
        spinListReminder = new SpinnerList(super.getApp(), R.layout.spinner_list_reminder, base,
                                           R.id.tvSpinnerEditReminder, R.id.flipperSpinnerListReminder,
                                           mSpinnerHandler);
        spinListReminder.setAnimation(spinListReminder.ANIMATION_UP);
        spinListReminder.initListView(R.id.lvSpinnerMenuReminder, Reminder, R.layout.spinner_list_item_reminder,
                                      R.id.tvSpinnerItemReminder);
        spinListReminder.setSelected(nReminderSelected, 0);
        spinListReminder.setViewInvalid(spinListRecurrence);
        spinListRecurrence.setViewInvalid(spinListReminder);
    }

    private void initEditSpinner() {
        RelativeLayout base                = null;
        int            nRecurrenceSelected = 0;
        int            nReminderSelected   = 0;

        nRecurrenceSelected = application.getRecurrenceSelected();

        if (0 > nRecurrenceSelected) {
            nRecurrenceSelected = 0;
            application.setRecurrenceSelected(nRecurrenceSelected);
        }

        /**
         * get recurrence
         */
        nRecurrenceSelected = getRecurrenceIndex(mnEventIndex);
        application.setRecurrenceSelected(nRecurrenceSelected);

        /**
         * get reminder
         */
        nReminderSelected = getReminderIndex(mnEventIndex);
        application.setReminderSelected(nReminderSelected);
        base               = (RelativeLayout) super.getApp().findViewById(R.id.rlNewEventRecurrence);
        spinListRecurrence = new SpinnerList(super.getApp(), R.layout.spinner_list_recurrence, base,
                R.id.tvSpinnerEditRecurrence, R.id.flipperSpinnerListRecurrence, mSpinnerHandler);
        spinListRecurrence.initListView(R.id.lvSpinnerMenuRecurrence, Recurrence,
                                        R.layout.spinner_list_item_recurrence, R.id.tvSpinnerItemRecurrence);
        spinListRecurrence.setSelected(nRecurrenceSelected, 0);
        base             = (RelativeLayout) super.getApp().findViewById(R.id.rlNewEventReminder);
        spinListReminder = new SpinnerList(super.getApp(), R.layout.spinner_list_reminder, base,
                                           R.id.tvSpinnerEditReminder, R.id.flipperSpinnerListReminder,
                                           mSpinnerHandler);
        spinListReminder.setAnimation(spinListReminder.ANIMATION_UP);

        if (Reminder.length > nReminderSelected) {
            spinListReminder.initListView(R.id.lvSpinnerMenuReminder, Reminder, R.layout.spinner_list_item_reminder,
                                          R.id.tvSpinnerItemReminder);
        } else {
            spinListReminder.initListView(R.id.lvSpinnerMenuReminder, ReminderCustom,
                                          R.layout.spinner_list_item_reminder, R.id.tvSpinnerItemReminder);
        }

        spinListReminder.setSelected(nReminderSelected, 0);
        spinListReminder.setViewInvalid(spinListRecurrence);
        spinListRecurrence.setViewInvalid(spinListReminder);
    }

    private int getRecurrenceIndex(int nEventIndex) {
        int nRecurrence = application.getEventData().getDetailRecurrence(nEventIndex);

        return nRecurrence;
    }

    private int getReminderIndex(int nEventIndex) {
        String szReminder = application.getEventData().getDetailReminder(nEventIndex);

        if (null == szReminder) {
            return 0;
        }

        int nReminder = 0;

        try {
            nReminder = Integer.valueOf(szReminder);
        } catch (NumberFormatException e) {
            nReminder = 0;
        }

        if (0 >= nReminder) {

            /**
             * No Reminder
             */
            return 0;
        }

        switch (nReminder) {
        case 5 :

            /**
             * 5 min before
             */
            return 1;
        case 15 :

            /**
             * 15 min before
             */
            return 2;
        case 30 :

            /**
             * 30 min before
             */
            return 3;
        case 60 :

            /**
             * 1 hour before
             */
            return 4;
        case 120 :

            /**
             * 2 hour before
             */
            return 5;
        case 1440 :

            /**
             * 1 day before
             */
            return 6;
        case 2880 :

            /**
             * 2 days before
             */
            return 7;
        }

        /**
         * set custom reminder
         */
        int    nCount         = 0;
        String szReminderItem = null;

        if (1440 <= nReminder) {                                                            // reminder by day
            nCount         = nReminder / 1440;
            szReminderItem = String.format("%d days before", nCount);
        } else if ((1440 > nReminder) && (60 <= nReminder) && (0 == (nReminder % 60))) {    // reminder by hour
            nCount         = nReminder / 60;
            szReminderItem = String.format("%d hour before", nCount);
        } else {                                                                            // reminder by minute
            szReminderItem = String.format("%d min before", nReminder);
        }

        ReminderCustom[9] = szReminderItem;

        return 9;
    }

    public void setHeaderTitle(String szTitle) {
        if (null == szTitle) {
            return;
        }

        TextView tvTmp = (TextView) super.getApp().findViewById(R.id.tvCalendarHeaderTitle);

        if (null != tvTmp) {
            tvTmp.setText(szTitle);
        }
    }

    private void initState() {
        boolean bAllDayChk = false;

        bAllDayChk = application.getIsAllDayCheck();

        ImageView ivTmp = (ImageView) super.getApp().findViewById(R.id.ivNewEventAllDayChkBox);

        if (null != ivTmp) {
            if (bAllDayChk) {
                showTimeView(false);
                ivTmp.setImageResource(R.drawable.checkbox_focus);
            } else {
                showTimeView(true);
                ivTmp.setImageResource(R.drawable.checkbox);
            }
        }
    }

    private void initEditState() {
        boolean bAllDayChk = false;

        bAllDayChk = application.getEventData().getDetailAllDay(mnEventIndex);

        ImageView ivTmp = (ImageView) super.getApp().findViewById(R.id.ivNewEventAllDayChkBox);

        if (null != ivTmp) {
            if (bAllDayChk) {
                showTimeView(false);
                ivTmp.setImageResource(R.drawable.checkbox_focus);
            } else {
                showTimeView(true);
                ivTmp.setImageResource(R.drawable.checkbox);
            }
        }
    }

    private void showTimeView(boolean bShow) {
        Button button = (Button) application.getActivity().findViewById(R.id.btnNewEventTimeFrom);

        showView(button, bShow);
        button = (Button) application.getActivity().findViewById(R.id.btnNewEventTimeTo);
        showView(button, bShow);
        button = null;
    }

    private void showView(View view, boolean bShow) {
        if (null == view) {
            return;
        }

        if (bShow) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /*
     *  public void setDateField(String szYear, String szMonth, String szDay) {
     *      String szDate = String.format("%s/%s/%s", szMonth, szDay, szYear);
     *      int    nResId = application.getDateShowFromResId();
     *
     *      if ((0 > nResId) || (null == szDate)) {
     *          return;
     *      }
     *
     *      Calendar calendarTmp = Calendar.getInstance();
     *
     *      if (R.id.tvNewEventFromDate == nResId) {
     *          calendarTmp.set(Calendar.YEAR, Integer.valueOf(szYear));
     *          calendarTmp.set(Calendar.MONTH, Integer.valueOf(szMonth) - 1);
     *          calendarTmp.set(Calendar.DAY_OF_MONTH, Integer.valueOf(szDay));
     *          application.getEventData().setDetailDateStart(mnEventIndex, calendarTmp);
     *      } else if (R.id.tvNewEventToDate == nResId) {
     *          calendarTmp.set(Calendar.YEAR, Integer.valueOf(szYear));
     *          calendarTmp.set(Calendar.MONTH, Integer.valueOf(szMonth) - 1);
     *          calendarTmp.set(Calendar.DAY_OF_MONTH, Integer.valueOf(szDay));
     *          application.getEventData().setDetailDateEnd(mnEventIndex, calendarTmp);
     *      }
     *
     *      Message msg = new Message();
     *
     *      msg.arg1 = nResId;
     *      msg.obj  = szDate;
     *
     *      wndHandler.sendMessage(msg);
     *
     *      msg = null;
     *  }
     */
    public String[] getNewEventData(boolean bIsRecurr) {
        String[] arrData    = new String[11];
        String   szTmp      = null;
        boolean  bAllDatChk = false;

        if (-1 == mnEventIndex) {
            bAllDatChk = application.getIsAllDayCheck();
        } else {
            bAllDatChk = application.getEventData().getDetailAllDay(mnEventIndex);
        }

        // title
        TextView tvTmp = (TextView) super.getApp().findViewById(R.id.etNewEventTitle);

        if (null == tvTmp) {
            return null;
        }

        szTmp = tvTmp.getText().toString();

        if ((null == szTmp) || (szTmp.length() <= 0)) {
            ((CalendarApp) super.getApp()).showAlarmDlg(true, null, super.getApp().getString(R.string.EnterTitle));

            return null;
        }

        arrData[0] = szTmp;

        // content
        tvTmp = (TextView) super.getApp().findViewById(R.id.etNewEventDescription);

        if (null == tvTmp) {
            return null;
        }

        szTmp      = tvTmp.getText().toString();
        arrData[1] = szTmp;

        // where
        tvTmp = (TextView) super.getApp().findViewById(R.id.etNewEventWhere);

        if (null == tvTmp) {
            return null;
        }

        szTmp      = tvTmp.getText().toString();
        arrData[2] = szTmp;

        // when
        int nYearStart          = 0,
            nMonthStart         = 0,
            nDayStart           = 0;
        int nYearEnd            = 0,
            nMonthEnd           = 0,
            nDayEnd             = 0;
        int nRecurrenceSelected = 0;
        int nReminderSelected   = 0;

        nRecurrenceSelected = application.getRecurrenceSelected();
        nReminderSelected   = application.getReminderSelected();

        int nSHours    = 0,
            nSMinutes  = 0,
            nEHours    = 0,
            nEMinutes  = 0;
        int nRemainder = 0;

        if (null != calendarTimeDataFrom) {
            nSHours    = calendarTimeDataFrom.getHour();
            nSMinutes  = calendarTimeDataFrom.getMinute();
            nRemainder = nSHours % 12;

            if (Calendar.PM == calendarTimeDataFrom.getNoon()) {
                nSHours = 12 + nRemainder;
            } else {
                nSHours = nRemainder;
            }
        }

        if (null != calendarTimeDataTo) {
            nEHours    = calendarTimeDataTo.getHour();
            nEMinutes  = calendarTimeDataTo.getMinute();
            nRemainder = nEHours % 12;

            if (Calendar.PM == calendarTimeDataTo.getNoon()) {
                nEHours = 12 + nRemainder;
            } else {
                nEHours = nRemainder;
            }
        }

        Calendar calendarTmp = Calendar.getInstance();

        if (null != calendarDateDataFrom) {
            nYearStart  = calendarDateDataFrom.getYear();
            nMonthStart = calendarDateDataFrom.getMonth();
            nDayStart   = calendarDateDataFrom.getDay();
            calendarTmp.set(Calendar.YEAR, nYearStart);
            calendarTmp.set(Calendar.MONTH, nMonthStart - 1);
            calendarTmp.set(Calendar.DAY_OF_MONTH, nDayStart);
            application.getEventData().setDetailDateStart(mnEventIndex, calendarTmp);
        }

        if (null != calendarDateDataTo) {
            nYearEnd  = calendarDateDataTo.getYear();
            nMonthEnd = calendarDateDataTo.getMonth();
            nDayEnd   = calendarDateDataTo.getDay();
            calendarTmp.set(Calendar.YEAR, nYearEnd);
            calendarTmp.set(Calendar.MONTH, nMonthEnd - 1);
            calendarTmp.set(Calendar.DAY_OF_MONTH, nDayEnd);
            application.getEventData().setDetailDateEnd(mnEventIndex, calendarTmp);
        }

        if (bAllDatChk) {
            calendarTmp = addDay(nYearEnd, nMonthEnd, nDayEnd, 1);

            /**
             * if set all day, the end day must shift one day
             */
            int nYearEndAllDay  = calendarTmp.get(Calendar.YEAR);
            int nMonthEndAllDay = calendarTmp.get(Calendar.MONTH) + 1;
            int nDayEndAllDay   = calendarTmp.get(Calendar.DAY_OF_MONTH);

            calendarTmp = null;

            if (bIsRecurr) {
                szTmp      = String.format("%04d%02d%02d", nYearStart, nMonthStart, nDayStart);
                arrData[3] = szTmp;
                szTmp      = String.format("%04d%02d%02d", nYearEndAllDay, nMonthEndAllDay, nDayEndAllDay);
                arrData[4] = szTmp;
            } else {
                szTmp      = String.format("%04d-%02d-%02d", nYearStart, nMonthStart, nDayStart);
                arrData[3] = szTmp;
                szTmp      = String.format("%04d-%02d-%02d", nYearEndAllDay, nMonthEndAllDay, nDayEndAllDay);
                arrData[4] = szTmp;
            }
        } else {
            if (bIsRecurr) {
                szTmp = String.format("%04d%02d%02dT%02d%02d00", nYearStart, nMonthStart, nDayStart, nSHours,
                                      nSMinutes);
                arrData[3] = szTmp;
                szTmp      = String.format("%04d%02d%02dT%02d%02d00", nYearEnd, nMonthEnd, nDayEnd, nEHours, nEMinutes);
                arrData[4] = szTmp;
            } else {
                szTmp = String.format("%04d-%02d-%02dT%02d:%02d:00", nYearStart, nMonthStart, nDayStart, nSHours,
                                      nSMinutes);
                arrData[3] = szTmp;
                szTmp      = String.format("%04d-%02d-%02dT%02d:%02d:00", nYearEnd, nMonthEnd, nDayEnd, nEHours,
                                           nEMinutes);
                arrData[4] = szTmp;
            }
        }

        if (arrData[3].compareTo(arrData[4]) > 0) {    // Check from-to setting
            ((CalendarApp) super.getApp()).showAlarmDlg(true, null, super.getApp().getString(R.string.InvalidFromTo));

            return null;
        }

        // recurrence
        if (bIsRecurr) {

            // reminder
            arrData[5] = getRecurrence1(nRecurrenceSelected);
            arrData[6] = getRecurrence2(nRecurrenceSelected, nYearStart, nMonthStart, nDayStart, nDayEnd);
            arrData[7] = String.format("%04d%02d%02d", nYearStart + 1, nMonthStart, nDayStart);
            szTmp      = getReminderMinutes(nReminderSelected);
            arrData[9] = "alert";

            if (szTmp.equals("0") || szTmp.equals("")) {
                arrData[8]  = "-1";
                arrData[10] = "false";
            } else {
                arrData[8]  = szTmp;
                arrData[10] = "true";
            }
        } else {

            // reminder
            szTmp      = getReminderMinutes(nReminderSelected);
            arrData[6] = "alert";

            if (szTmp.equals("0") || szTmp.equals("")) {
                arrData[5] = "-1";
                arrData[7] = "false";
            } else {
                arrData[5] = szTmp;
                arrData[7] = "true";
            }
        }

        return arrData;
    }

    private Calendar addDay(int nYear, int nMonth, int nDay, int nAddDay) {
        Calendar calendarTmp = Calendar.getInstance();

        calendarTmp.set(Calendar.YEAR, nYear);
        calendarTmp.set(Calendar.MONTH, nMonth - 1);
        calendarTmp.set(Calendar.DAY_OF_MONTH, nDay);
        calendarTmp.add(Calendar.DAY_OF_MONTH, 1);

        return calendarTmp;
    }

    public boolean IsReCurrence() {
        boolean bReCurr = false;
        int     nReCurr = application.getRecurrenceSelected();

        if (0 < nReCurr) {
            bReCurr = true;
        }

        return bReCurr;
    }

    private String getReminderMinutes(int nIndex) {
        if ((0 > nIndex) || (nIndex >= ReminderCustom.length)) {
            return "0";
        }

        switch (nIndex) {
        case 0 :
            return "0";
        case 1 :
            return "5";
        case 2 :
            return "15";
        case 3 :
            return "30";
        case 4 :
            return "60";
        case 5 :
            return "120";
        case 6 :
            return "1440";
        case 7 :
            return "2880";
        case 8 :
            return "On day of event";
        case 9 :    // customer reminder
            String szReminder = application.getEventData().getDetailReminder(mnEventIndex);

            return szReminder;
        }

        return "0";
    }

    private String getRecurrence1(int nIndex) {
        switch (nIndex) {
        case 0 :
            return "";
        case 1 :
            return "DAILY";
        case 2 :
            return "WEEKLY";
        case 3 :
            return "WEEKLY";
        case 4 :
            return "WEEKLY";
        case 5 :
            return "WEEKLY";
        case 6 :
            return "MONTHLY";
        case 7 :
            return "YEARLY";
        }

        return "";
    }

    private String getRecurrence2(int nIndex, int nYear, int nMonth, int nDayStart, int nDayEnd) {
        switch (nIndex) {
        case 0 :
            return "";
        case 1 :
            return "";
        case 2 :
            return "MO,TU,WE,TH,FR";
        case 3 :
            return "MO,WE,FR";
        case 4 :
            return "TU,TH";
        case 5 :    // return day of weekly
            String szByDay = getDayOfWeekly(nYear, nMonth, nDayStart, nDayEnd);

            return szByDay;
        case 6 :
            return "";
        case 7 :
            return "";
        }

        return "";
    }

    private String getDayOfWeekly(int nYear, int nMonth, int nDayStart, int nDayEnd) {
        Calendar calendarTmp = Calendar.getInstance();

        calendarTmp.set(Calendar.YEAR, nYear);
        calendarTmp.set(Calendar.MONTH, nMonth - 1);
        calendarTmp.set(Calendar.DAY_OF_MONTH, nDayStart);

        int    nDayOfWeekly = -1;
        String szDayOfWeek  = null;

        nDayOfWeekly = calendarTmp.get(Calendar.DAY_OF_WEEK);
        szDayOfWeek  = getDayOfWeekStr(nDayOfWeekly);

        return szDayOfWeek;
    }

    private String getDayOfWeekStr(int nDayOfWeek) {
        switch (nDayOfWeek) {
        case Calendar.SUNDAY :
            return "SU";
        case Calendar.MONDAY :
            return "MO";
        case Calendar.TUESDAY :
            return "TU";
        case Calendar.WEDNESDAY :
            return "WE";
        case Calendar.THURSDAY :
            return "TH";
        case Calendar.FRIDAY :
            return "FR";
        case Calendar.SATURDAY :
            return "SA";
        }

        return "";
    }

    public void hideKeyboard() {
        InputMethodManager mgr = (InputMethodManager) super.getApp().getSystemService(Context.INPUT_METHOD_SERVICE);

        // title
        TextView tvTmp = (TextView) super.getApp().findViewById(R.id.etNewEventTitle);

        if (null != tvTmp) {
            mgr.hideSoftInputFromWindow(tvTmp.getWindowToken(), 0);
        }

        // Where
        tvTmp = (TextView) super.getApp().findViewById(R.id.etNewEventWhere);

        if (null != tvTmp) {
            mgr.hideSoftInputFromWindow(tvTmp.getWindowToken(), 0);
        }

        // content
        tvTmp = (TextView) super.getApp().findViewById(R.id.etNewEventDescription);

        if (null != tvTmp) {
            mgr.hideSoftInputFromWindow(tvTmp.getWindowToken(), 0);
        }
    }

    private void showKeyboard(TextView textView) {
        ((InputMethodManager) super.getApp().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(textView, 0);
    }

    private void hideAllSpinnerDropList() {
        spinListReminder.hideDropList();
        spinListRecurrence.hideDropList();
    }

    public void setWindowIdFrom(int nWndId) {
        mCalendarNewEventData.setPreWindowId(nWndId);
    }

    public int getWindowIdFrom() {
        return mCalendarNewEventData.getPreWindowId();
    }

    public void clearEditSelected() {
        calendarNewEventEditTextControl.clearEditSelected();
    }

    public void setDateDataFrom(int nYear, int nMonth, int nDay) {
        if (null != calendarDateDataFrom) {
            calendarDateDataFrom.setDate(nYear, nMonth, nDay);
        }
    }

    public void setDateDataTo(int nYear, int nMonth, int nDay) {
        if (null != calendarDateDataTo) {
            calendarDateDataTo.setDate(nYear, nMonth, nDay);
        }
    }

    public void setTimeDataFrom(int nHour, int nMinute, int nNoon) {
        if (null != calendarTimeDataFrom) {
            calendarTimeDataFrom.setTime(nHour, nMinute, nNoon);
        }
    }

    public void setTimeDataTo(int nHour, int nMinute, int nNoon) {
        if (null != calendarTimeDataTo) {
            calendarTimeDataTo.setTime(nHour, nMinute, nNoon);
        }
    }

    public void setDateDataNow() {
        Calendar calendar = Calendar.getInstance();

        setDateDataFrom(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH));
        setDateDataTo(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                      calendar.get(Calendar.DAY_OF_MONTH));
        calendar = null;
    }

    public void setTimeDataNow() {
        Calendar calendar = Calendar.getInstance();
        int      nMinute  = transTimeMinute(calendar.get(Calendar.MINUTE));

        if (45 < nMinute) {
            nMinute = 0;
            calendar.add(Calendar.HOUR, 1);
        }

        boolean bNoon = true;

        if (Calendar.PM == calendar.get(Calendar.AM_PM)) {
            bNoon = false;
        }

        setTimeDataFrom(calendar.get(Calendar.HOUR), nMinute, calendar.get(Calendar.AM_PM));
        setTimeDataTo(calendar.get(Calendar.HOUR), nMinute, calendar.get(Calendar.AM_PM));
        calendar = null;
    }

    /**
     * class for date and time
     * @author jugo
     *
     */
    private class CalendarTimeData {
        private int mnHour   = 0;
        private int mnMinute = 0;
        private int mnNoon   = Calendar.AM;

        public CalendarTimeData() {}

        public void setTime(int nHour, int nMinute, int nNoon) {
            if (0 == nHour) {
                nHour = 12;
            }

            mnHour   = nHour;
            mnMinute = nMinute;

            if ((Calendar.AM == nNoon) || (Calendar.PM == nNoon)) {
                mnNoon = nNoon;
            }
        }

        public int getHour() {
            return mnHour;
        }

        public int getMinute() {
            return mnMinute;
        }

        public int getNoon() {
            return mnNoon;
        }
    }


    private class CalendarDateData {
        private int mnYear  = 0;
        private int mnMonth = 0;
        private int mnDay   = 0;

        public CalendarDateData() {}

        public void setDate(int nYear, int nMonth, int nDay) {
            mnYear  = nYear;
            mnMonth = nMonth;
            mnDay   = nDay;
        }

        public int getYear() {
            return mnYear;
        }

        public int getMonth() {
            return mnMonth;
        }

        public int getDay() {
            return mnDay;
        }
    }


    public void invalidView(int nWhat) {
        wndHandler.sendEmptyMessage(nWhat);
    }

    /**
     * handler for access view component
     */
    Handler wndHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            super.handleMessage(msg);

            switch (msg.what) {
            case UPDATE_DATE :
                initDateField();

                break;
            case UPDATE_TIME :
                initTimeButton();

                break;
            }
        }
    };
}

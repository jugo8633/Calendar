//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar.controler;


import android.view.ViewGroup;

import android.widget.HpFooterBarButton;

import com.hp.ij.calendar.CalendarApplication;
import com.hp.ij.calendar.CalendarDayWnd;
import com.hp.ij.calendar.CalendarMonthWnd;
import com.hp.ij.calendar.CalendarWnd;
import com.hp.ij.calendar.R;

import frame.event.EventMessage;

import frame.view.MenuList;
import frame.view.ViewUtile;

public class CalendarMonthFootBarControl extends CalendarButtonControl {
    private static final String TAG           = "CalendarMonthFootBarControl";
    private static boolean      sbMenuShow    = false;
    private CalendarWnd         mCalendarWnd  = null;
    private int                 mDefaultResId = -1;
    private MenuList            mMenu         = null;

    public CalendarMonthFootBarControl(CalendarWnd wnd, int nMenuResId) {
        super(wnd);
        mCalendarWnd = wnd;
    }

    /**
     * initial button, set default selected button.
     * if parameter is -1 will not set default selected button
     * @param nDefaultResId : view resource id that set default selected.
     */
    public void initButton(int nDefaultResId, int nMenuResId) {
        if (null == mCalendarWnd.getActivity()) {
            return;
        }

        clearButton();
        addButton(R.id.fbMonthView);
        addButton(R.id.fbDayView);
        addButton(R.id.fbToday);
        addButton(R.id.fbAdd);
        addButton(R.id.fbPrint);
        addButton(R.id.fbMore);
        mDefaultResId = nDefaultResId;
        initMenu(nMenuResId);

        if (!sbMenuShow) {
            setOneSelected(nDefaultResId);
        } else {
            HpFooterBarButton fBtn = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(R.id.fbMore);

            if (null != fBtn) {
                fBtn.setSelect(true);
            }
        }
    }

    public boolean buttonClickHandle(int nResId) {
        switch (nResId) {

        /**
         * foot bar button click
         */
        case R.id.fbMonthView :
        case R.id.fbDayView :
        case R.id.fbToday :
        case R.id.fbAdd :
        case R.id.fbPrint :
        case R.id.fbMore :
            setOneSelected(nResId);
            buttonFootBarClickHandle(nResId);

            break;

        /**
         * more menu button click
         */
        case R.id.rlMonthMultiCalendarMain :
        case R.id.rlMonthMenuSyncMain :
            buttonMenuClickHandle(nResId);
            ShowHideMoreMenu();

            break;
        default :
            return false;
        }

        return true;
    }

    private void buttonFootBarClickHandle(int nResId) {
        if (R.id.fbMore != nResId) {
            if (sbMenuShow && (null != mMenu)) {
                mMenu.showMenu(false);
                sbMenuShow = false;
            }
        }

        if (nResId == mDefaultResId) {
            return;
        }

        switch (nResId) {
        case R.id.fbMonthView :
            // +++++ add by Chance 2010-08-19 for Mantis[0007551] +++++ //
            ((CalendarApplication) mCalendarWnd.getActivity().getApplication()).setCurrDate();
            // ----- add by Chance 2010-08-19 for Mantis[0007551] ----- //
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_MONTH_VIEW, null);

            break;
        case R.id.fbDayView :
            ((CalendarApplication) mCalendarWnd.getActivity().getApplication()).setCurrDate();
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_DAY_VIEW, null);

            break;
        case R.id.fbToday :
            if (mDefaultResId == R.id.fbMonthView) {
                ((CalendarMonthWnd) mCalendarWnd).goToDay();
            } else if (mDefaultResId == R.id.fbDayView) {
                ((CalendarDayWnd) mCalendarWnd).goToDay();
            }

            break;
        case R.id.fbAdd :
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_NEW_EVENT, null);

            break;
        case R.id.fbPrint :
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_PRINT, null);

            break;
        case R.id.fbMore :
            ShowHideMoreMenu();

            break;
        }
    }

    private void initMenu(int nMenuResId) {
        mMenu = null;
        mMenu = (MenuList) mCalendarWnd.getActivity().findViewById(nMenuResId);

        if (null != mMenu) {
            mMenu.setMenuView(R.layout.calendar_month_menu, null, ViewGroup.LayoutParams.FILL_PARENT,
                              ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        mCalendarWnd.RegisterEvent(R.id.rlMonthMultiCalendarMain, mCalendarWnd.EVENT_CLICK);
        mCalendarWnd.RegisterEvent(R.id.rlMonthMenuSyncMain, mCalendarWnd.EVENT_CLICK);
        initMenuState();
    }

    private void initMenuState() {
        if (null == mMenu) {
            return;
        }

        mMenu.showMenu(sbMenuShow);
    }

    public void ShowHideMoreMenu() {
        if (null == mMenu) {
            return;
        }

        sbMenuShow = mMenu.isShow();

        if (sbMenuShow) {
            sbMenuShow = false;
            setOneSelected(mDefaultResId);
        } else {
            sbMenuShow = true;
            initMenuItem();
        }

        mMenu.showMenu(sbMenuShow);
    }

    private void buttonMenuClickHandle(int nResId) {
        setMenuItemSelected(nResId, true);

        switch (nResId) {
        case R.id.rlMonthMultiCalendarMain :
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_MULTI, null);

            break;
        case R.id.rlMonthMenuSyncMain :
            mCalendarWnd.sendAppMsg(EventMessage.WND_MSG, EventMessage.WND_SYNC, null);

            break;
        }
    }

    private void setMenuItemSelected(int nResId, boolean bSelected) {
        ViewUtile viewUtile = new ViewUtile(mCalendarWnd.getActivity());

        switch (nResId) {
        case R.id.rlMonthMultiCalendarMain :
            viewUtile.setTextViewSelected(R.id.tvMonthMenuMultiCalendar, bSelected);

            if (bSelected) {
                viewUtile.setImageSrc(R.id.ivMonthMenuMultiCalendar, R.drawable.calendar_multi_f);
            } else {
                viewUtile.setImageSrc(R.id.ivMonthMenuMultiCalendar, R.drawable.calendar_multi);
            }

            break;
        case R.id.rlMonthMenuSyncMain :
            viewUtile.setTextViewSelected(R.id.tvMonthMenuSynch, bSelected);

            if (bSelected) {
                viewUtile.setImageSrc(R.id.ivMonthMenuSync, R.drawable.calendar_synch_f);
            } else {
                viewUtile.setImageSrc(R.id.ivMonthMenuSync, R.drawable.calendar_synch);
            }

            break;
        }

        viewUtile = null;
    }

    private void initMenuItem() {
        setMenuItemSelected(R.id.rlMonthMultiCalendarMain, false);
        setMenuItemSelected(R.id.rlMonthMenuSyncMain, false);
    }

    public void buttonDownHandle(int nResId) {
        if ((nResId == R.id.rlMonthMultiCalendarMain) || (nResId == R.id.rlMonthMenuSyncMain)) {
            setMenuItemSelected(nResId, true);

            return;
        }

        if (nResId == mDefaultResId) {
            return;
        }

        HpFooterBarButton fb = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nResId);

        if (null == fb) {
            return;
        }

        HpFooterBarButton footBtn = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(mDefaultResId);

        if (null != footBtn) {
            footBtn.setSelect(false);
            footBtn = null;
        }

        switch (nResId) {
        case R.id.fbMonthView :
        case R.id.fbDayView :
        case R.id.fbToday :
        case R.id.fbAdd :
        case R.id.fbPrint :
        case R.id.fbMore :
            fb.setSelect(true);

            break;
        }

        fb = null;
    }

    public void buttonUpHandle(int nResId) {
        if ((nResId == R.id.rlMonthMultiCalendarMain) || (nResId == R.id.rlMonthMenuSyncMain)) {
            setMenuItemSelected(nResId, false);

            return;
        }

        if (nResId == mDefaultResId) {
            return;
        }

        HpFooterBarButton fb = (HpFooterBarButton) mCalendarWnd.getActivity().findViewById(nResId);

        if (null == fb) {
            return;
        }

        switch (nResId) {
        case R.id.fbMonthView :
        case R.id.fbDayView :
        case R.id.fbToday :
        case R.id.fbAdd :
        case R.id.fbPrint :
        case R.id.fbMore :
            fb.setSelect(false);

            break;
        }

        fb = null;
        setOneSelected(mDefaultResId);
    }
}

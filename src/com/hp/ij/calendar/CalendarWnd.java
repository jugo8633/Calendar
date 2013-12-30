//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import android.os.Handler;
import android.os.Message;

import android.view.View;

import frame.event.EventHandler;

import frame.window.WndBase;

public abstract class CalendarWnd extends WndBase {
    public static final int       EVENT_CLICK          = 1;
    public static final int       EVENT_TOUCH          = 0;
    private static final String   TAG                  = "CalendarWnd";
    protected final int           HIDE_VIEW            = -1;
    private EventHandler          WndEvent             = null;
    protected CalendarApplication application          = null;
    private List<Integer>         listClickResId       = null;
    private List<Integer>         listTouchResId       = null;
    private int                   m_nMainLayoutResId   = HIDE_VIEW;
    private int                   m_nHeaderLayoutResId = HIDE_VIEW;
    private int                   m_nFootLayoutResId   = HIDE_VIEW;

    /**
     * window event handle
     */
    private Handler WndHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean bOptionBlock = application.getOptionBlock();

            switch (msg.what) {
            case EventHandler.EVENT_HANDLE_CREATED :
                break;
            case EventHandler.EVENT_HANDLE_ON_TOUCH :
                switch (msg.arg1) {
                case EventHandler.EVENT_HANDLE_ON_TOUCH_DOWN :
                    if (bOptionBlock) {
                        return;
                    }

                    onTouchDown(msg.arg2);

                    break;
                case EventHandler.EVENT_HANDLE_ON_TOUCH_UP :
                    if (bOptionBlock) {
                        return;
                    }

                    onTouchUp(msg.arg2);

                    break;
                }

                break;
            case EventHandler.EVENT_HANDLE_ON_CLICK :
                if (bOptionBlock) {
                    return;
                }

                onClick(msg.arg2);

                break;
            }
        }
    };

    public CalendarWnd(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
        application    = (CalendarApplication) active.getApplication();
        WndEvent       = new EventHandler(WndHandler);
        listTouchResId = new ArrayList<Integer>();
        listClickResId = new ArrayList<Integer>();
    }

    protected abstract void onTouchDown(int nResId);

    protected abstract void onTouchUp(int nResId);

    protected abstract void onClick(int nResId);

    protected abstract void onShow();

    protected abstract void onClose();

    @Override
    public void showWindow(boolean bShow) {
        if (bShow) {
            if (HIDE_VIEW != m_nMainLayoutResId) {
                if ((HIDE_VIEW == m_nFootLayoutResId) && (HIDE_VIEW == m_nHeaderLayoutResId)) {
                    ((CalendarApp) super.getApp()).addMainView(m_nMainLayoutResId, false, false);
                } else if ((HIDE_VIEW != m_nFootLayoutResId) && (HIDE_VIEW == m_nHeaderLayoutResId)) {
                    ((CalendarApp) super.getApp()).addMainView(m_nMainLayoutResId, false, true);
                    ((CalendarApp) super.getApp()).addFootView(m_nFootLayoutResId);
                } else if ((HIDE_VIEW == m_nFootLayoutResId) && (HIDE_VIEW != m_nHeaderLayoutResId)) {
                    ((CalendarApp) super.getApp()).addMainView(m_nMainLayoutResId, true, false);
                    ((CalendarApp) super.getApp()).addHeaderView(m_nHeaderLayoutResId);
                } else if ((HIDE_VIEW != m_nFootLayoutResId) && (HIDE_VIEW != m_nHeaderLayoutResId)) {
                    ((CalendarApp) super.getApp()).addMainView(m_nMainLayoutResId, true, true);
                    ((CalendarApp) super.getApp()).addHeaderView(m_nHeaderLayoutResId);
                    ((CalendarApp) super.getApp()).addFootView(m_nFootLayoutResId);
                }

                onShow();
                initEvents();
                sendAppMsg(WND_MSG, WND_SHOW, null);
            }
        }
    }

    @Override
    public void closeWindow() {
        onClose();

        if (listTouchResId != null) {
            if (listTouchResId.size() > 0) {
                listTouchResId.clear();
            }

            listTouchResId = null;
        }

        if (listClickResId != null) {
            if (listClickResId.size() > 0) {
                listClickResId.clear();
            }

            listClickResId = null;
        }

        if (null != WndEvent) {
            WndEvent = null;
        }
    }

    public void setLayoutResId(int nMain, int nFoot, int nHeader) {
        if (nMain == -1) {
            return;
        }

        m_nMainLayoutResId = nMain;

        if (nFoot != -1) {
            m_nFootLayoutResId = nFoot;
        }

        if (nHeader != -1) {
            m_nHeaderLayoutResId = nHeader;
        }
    }

    public void setViewTouchEvent(int nResId) {
        listTouchResId.add(nResId);
    }

    public void setViewClickEvent(int nResId) {
        listClickResId.add(nResId);
    }

    private void initEvents() {
        if (null != listTouchResId) {
            if (listTouchResId.size() > 0) {
                for (int i = 0; i < listTouchResId.size(); i++) {
                    View tView = super.getApp().findViewById(listTouchResId.get(i));

                    if (null != tView) {
                        WndEvent.registerViewOnTouch(tView);
                    }
                }
            }
        }

        if (null != listClickResId) {
            if (listClickResId.size() > 0) {
                for (int i = 0; i < listClickResId.size(); i++) {
                    View cView = super.getApp().findViewById(listClickResId.get(i));

                    if (null != cView) {
                        WndEvent.registerViewOnClick(cView);
                    }
                }
            }
        }
    }

    public void RegisterEvent(int nResId, int nEventType) {
        View cView = super.getApp().findViewById(nResId);

        if (null == cView) {
            return;
        }

        switch (nEventType) {
        case EVENT_TOUCH :
            WndEvent.registerViewOnTouch(cView);

            break;
        case EVENT_CLICK :
            WndEvent.registerViewOnClick(cView);

            break;
        }
    }

    public void RegisterEvent(View vRes, int nEventType) {
        if (null == vRes) {
            return;
        }

        switch (nEventType) {
        case EVENT_TOUCH :
            WndEvent.registerViewOnTouch(vRes);

            break;
        case EVENT_CLICK :
            WndEvent.registerViewOnClick(vRes);

            break;
        }
    }

    public Activity getActivity() {
        return super.getApp();
    }
}

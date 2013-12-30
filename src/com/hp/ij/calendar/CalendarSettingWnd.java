//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import java.util.ArrayList;

import android.app.Activity;

import android.content.Context;

import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hp.ij.calendar.data.CalendarMultiCalendarData;

import frame.event.EventMessage;

public class CalendarSettingWnd extends CalendarWnd {
    private static final String       TAG                        = "CalendarSettingWnd";
    private ListView                  listView                   = null;
    private CalendarMultiCalendarData mCalendarMultiCalendarData = null;
    private int                       mnMainLayoutResId          = R.layout.calendar_setting;
    private final int                 mnHeaderLayoutResId        = R.layout.calendar_header_contextual;
    private final int                 mnFootLayoutResId          = R.layout.footbar_check_menu;

    public CalendarSettingWnd(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
        super.setLayoutResId(mnMainLayoutResId, mnFootLayoutResId, mnHeaderLayoutResId);
        super.setViewClickEvent(R.id.tvCalendarHeaderLogout);
        super.setViewClickEvent(R.id.tvCalendarHeaderBack);
        super.setViewClickEvent(R.id.tvFootbarChkMenuOK);
        mCalendarMultiCalendarData = new CalendarMultiCalendarData(application.getCalendarData());
    }

    @Override
    protected void onShow() {
        setHeaderTitle(super.getApp().getString(R.string.select_calendars));
        initListView();
    }

    @Override
    protected void onClose() {
        mCalendarMultiCalendarData = null;
    }

    @Override
    protected void onClick(int nResId) {
        switch (nResId) {
        case R.id.tvCalendarHeaderLogout :
            sendAppMsg(WND_MSG, EventMessage.WND_FINISH, null);

            break;
        case R.id.tvCalendarHeaderBack :
            sendAppMsg(WND_MSG, EventMessage.WND_STOP, null);

            break;
        case R.id.tvFootbarChkMenuOK :
            if (!application.checkNetWorkEnable()) {
                return;
            }

            int nCount = application.getEventData().getCalendarsCount();

            if (0 >= nCount) {
                return;
            }

            this.updateMultiCalendar();

            ArrayList<String> arrCalendar = new ArrayList<String>();
            boolean           bEnable     = false;
            String            szTitle     = null;

            for (int i = 0; i < nCount; i++) {
                bEnable = application.getEventData().getIsCalendarEnable(i);

                if (bEnable) {
                    szTitle = application.getEventData().getCalendarTitle(i);

                    if ((null != szTitle) && (0 < szTitle.length())) {
                        arrCalendar.add(szTitle);
                    }
                }
            }

            sendAppMsg(WND_MSG, EventMessage.WND_MULTI, arrCalendar);

            break;
        default :
            int nEnable = mCalendarMultiCalendarData.getMultiCalendarEnable(nResId);

            if (0 < nEnable) {
                mCalendarMultiCalendarData.setMultiCalendarEnable(nResId, 0);
            } else {
                mCalendarMultiCalendarData.setMultiCalendarEnable(nResId, 1);
            }

            listView.invalidateViews();

            break;
        }
    }

    @Override
    protected void onTouchDown(int nResId) {}

    @Override
    protected void onTouchUp(int nResId) {}

    private void initListView() {
        if (listView != null) {
            listView = null;
        }

        listView = (ListView) super.getApp().findViewById(R.id.lvCalendarSettingList);

        try {
            listView.setAdapter(new MultiCalendarAdapter(super.getApp()));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void syncMultiCalendarData() {
        int     nCount  = 0;
        boolean bEnable = false;

        nCount = application.getEventData().getMultiCalendarCount();

        for (int nIndex = 0; nIndex < nCount; nIndex++) {
            bEnable = application.getEventData().getIsCalendarEnable(nIndex);
            mCalendarMultiCalendarData.setMultiCalendarEnable(nIndex, bEnable
                    ? 1
                    : 0);
        }
    }

    private void updateMultiCalendar() {
        int nCount  = 0;
        int nEnable = 0;

        nCount = application.getEventData().getMultiCalendarCount();

        for (int nIndex = 0; nIndex < nCount; nIndex++) {
            nEnable = mCalendarMultiCalendarData.getMultiCalendarEnable(nIndex);

            if (0 < nEnable) {
                application.getEventData().setCalendarEnable(nIndex, true);
            } else {
                application.getEventData().setCalendarEnable(nIndex, false);
            }
        }
    }

    public class MultiCalendarAdapter extends BaseAdapter {
        private Context        m_context;
        private LayoutInflater m_inflater;

        public MultiCalendarAdapter(Context c) {
            m_context  = c;
            m_inflater = LayoutInflater.from(this.m_context);
        }

        public int getCount() {
            int nCount = application.getEventData().getCalendarsCount();

            return nCount;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = m_inflater.inflate(R.layout.calendar_setting_item, null);

            if (null == convertView) {
                return null;
            }

            ImageView ivChk = (ImageView) convertView.findViewById(R.id.ivCalendarSettingChkBox);

            if (null != ivChk) {
                int nEnable = mCalendarMultiCalendarData.getMultiCalendarEnable(position);

                ivChk.setId(position);

                if (0 < nEnable) {
                    ivChk.setImageResource(R.drawable.checkbox_focus);
                } else {
                    ivChk.setImageResource(R.drawable.checkbox);
                }

                CalendarSettingWnd.this.RegisterEvent(ivChk, CalendarWnd.EVENT_CLICK);
            }

            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvCalendarSettingItemName);
            String   szTitle = application.getEventData().getCalendarTitle(position);

            if ((null != tvTitle) && (null != szTitle)) {
                tvTitle.setText(szTitle);
            }

            return convertView;
        }
    }
}

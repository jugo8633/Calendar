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

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import frame.view.ListViewEx;

public class CalendarDayListEvent {
    private static final String TAG            = "CalendarDayListEvent";
    private CalendarApplication application    = null;
    private ListViewEx          eventListView  = null;
    private final int           m_nLayoutResId = R.id.lvDayEvent;
    public CalendarWnd          parentWnd      = null;
    private Activity            theAct         = null;

    public CalendarDayListEvent(Activity act, CalendarWnd pWnd) {
        theAct    = act;
        parentWnd = pWnd;
        parentWnd.setEventConSume(false);
        application = (CalendarApplication) act.getApplication();
    }

    public boolean initEventList() {
        eventListView = (ListViewEx) theAct.findViewById(m_nLayoutResId);

        if (null != eventListView) {
            eventListView.destroyDrawingCache();
            eventListView.setAlwaysDrawnWithCacheEnabled(true);
            eventListView.setDrawingCacheEnabled(true);
            eventListView.setAnimationCacheEnabled(true);
            eventListView.setAdapter(new eventAdapter(theAct));

            return true;
        }

        return false;
    }

    public void close() {}

    public int getCurrSelected() {
        if (null == eventListView) {
            return -1;
        }

        return eventListView.getSelectedItemPosition();
    }

    public class eventAdapter extends BaseAdapter {
        private Context        m_context;
        private LayoutInflater m_inflater;

        public eventAdapter(Context c) {
            m_context  = c;
            m_inflater = LayoutInflater.from(this.m_context);
        }

        public int getCount() {
            int nEventDetailCount = 0;

            nEventDetailCount = application.getEventData().getEventDetailCount();

            return nEventDetailCount;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = m_inflater.inflate(R.layout.calendar_event_list, null);

            if (null == convertView) {
                return null;
            }

            RelativeLayout rlEvent = (RelativeLayout) convertView.findViewById(R.id.rlEventListDetailMain);
            Calendar       CalSel  = Calendar.getInstance();

            CalSel.clear();
            CalSel.set(application.getSelYear(), application.getSelMonth() - 1, application.getSelDay(), 0, 0, 0);

            Calendar CalendarStart = Calendar.getInstance();

            CalendarStart.clear();
            CalendarStart.set(application.getEventData().getDetailDateStart(position).get(Calendar.YEAR),
                              application.getEventData().getDetailDateStart(position).get(Calendar.MONTH),
                              application.getEventData().getDetailDateStart(position).get(Calendar.DAY_OF_MONTH), 0, 0,
                              0);

            Calendar CalendarEnd = Calendar.getInstance();

            CalendarEnd.clear();
            CalendarEnd.set(application.getEventData().getDetailDateEnd(position).get(Calendar.YEAR),
                            application.getEventData().getDetailDateEnd(position).get(Calendar.MONTH),
                            application.getEventData().getDetailDateEnd(position).get(Calendar.DAY_OF_MONTH), 0, 0, 0);

            if (null != rlEvent) {
                rlEvent.setId(position);
                CalendarDayListEvent.this.parentWnd.RegisterEvent(rlEvent, CalendarWnd.EVENT_CLICK);

                String   szTmp = null;
                TextView tvTmp = (TextView) convertView.findViewById(R.id.tvEventListTitle);    // title

                tvTmp = (TextView) rlEvent.findViewById(R.id.tvEventListName);                  // calendar

                if (null != tvTmp) {
                    szTmp = application.getEventData().getDetailCalendar(position);
                    tvTmp.setText(szTmp);
                }

                tvTmp = (TextView) rlEvent.findViewById(R.id.tvEventListEvent);                 // event

                if (null != tvTmp) {
                    szTmp = application.getEventData().getDetailEvent(position);
                    tvTmp.setText(szTmp);
                }

                tvTmp = (TextView) rlEvent.findViewById(R.id.tvEventListTime);                  // time

                ImageView imgleft  = (ImageView) rlEvent.findViewById(R.id.ivLeftArrow);
                ImageView imgright = (ImageView) rlEvent.findViewById(R.id.ivRightArrow);

                if (null != tvTmp) {
                    szTmp = application.getEventData().getDetailTime(position);

                    if (szTmp.equals(application.getString(R.string.all_day))) {
                        szTmp = szTmp + "  ";                                                   // adjust position
                        tvTmp.setGravity(Gravity.RIGHT);
                        imgleft.setVisibility(View.INVISIBLE);
                        imgright.setVisibility(View.INVISIBLE);
                    } else {                                                                    // handle overday event
                        String[] tmpAry = szTmp.split(" - ");

                        try {
                            if (CalendarStart.before(CalSel) && (CalendarEnd.after(CalSel))) {
                                if (tmpAry.length >= 2) {
                                    szTmp = " - ";
                                }

                                tvTmp.setGravity(Gravity.CENTER);
                                imgright.setVisibility(View.VISIBLE);
                                imgleft.setVisibility(View.VISIBLE);
                            } else if (CalendarStart.before(CalSel)) {
                                if (tmpAry.length >= 2) {
                                    szTmp = " - " + tmpAry[1] + "  ";                           // adjust position
                                }

                                tvTmp.setGravity(Gravity.RIGHT);
                                imgleft.setVisibility(View.VISIBLE);
                                imgright.setVisibility(View.INVISIBLE);
                            } else if (CalendarEnd.after(CalSel)) {
                                if (tmpAry.length >= 2) {
                                    szTmp = "  " + tmpAry[0] + " - ";                           // adjust position
                                }

                                tvTmp.setGravity(Gravity.LEFT);
                                imgright.setVisibility(View.VISIBLE);
                                imgleft.setVisibility(View.INVISIBLE);
                            } else {
                                tvTmp.setGravity(Gravity.CENTER);
                                imgright.setVisibility(View.INVISIBLE);
                                imgleft.setVisibility(View.INVISIBLE);
                            }
                        } catch (IllegalArgumentException e) {}
                    }

                    tvTmp.setText(szTmp);
                }
            }

            return convertView;
        }
    }
}

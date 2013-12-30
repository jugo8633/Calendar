//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package com.hp.ij.calendar;


import android.app.Activity;

import android.os.Handler;

import android.widget.ProgressBar;
import android.widget.TextView;

public class CalendarLoadWnd extends CalendarWnd {
    private static final String TAG                = "CalendarLoadWnd";
    private static int          m_nPosition        = 0;
    private final int           m_nMainLayoutResId = R.layout.calendar_loading;
    private boolean             m_bTerm            = false;
    private boolean             m_bStop            = false;

    public CalendarLoadWnd(Activity active, Handler handler, int id) {
        super(active, handler, id);
        super.setLayoutResId(m_nMainLayoutResId, HIDE_VIEW, HIDE_VIEW);
    }

    @Override
    protected void onShow() {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onClose() {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onClick(int resId) {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onTouchDown(int resId) {

        // TODO Auto-generated method stub
    }

    @Override
    protected void onTouchUp(int resId) {

        // TODO Auto-generated method stub
    }

    public void runLoading() {
        final ProgressBar progressHorizontal = (ProgressBar) super.getApp().findViewById(R.id.pbLoading);

        m_bStop = false;
        new Thread() {
            public void run() {
                try {
                    for (int i = m_nPosition; i < 100; i++, m_nPosition++) {
                        progressHorizontal.setProgress(i);

                        if (m_bTerm) {
                            break;
                        }

                        if (m_bStop) {
                            progressHorizontal.setProgress(100);

                            break;
                        }

                        java.lang.Thread.sleep(10);
                    }
                } catch (Exception e) {
                    m_nPosition = 100;
                    e.printStackTrace();
                    sendAppMsg(WND_MSG, WND_EXCP, null);
                } finally {
                    if (m_bTerm) {
                        return;
                    }

                    m_nPosition = 100;
                    sendAppMsg(WND_MSG, WND_STOP, null);
                }
            }
        }.start();
    }

    public void setRunStop() {
        m_bStop = true;
    }

    public void initProgressBar() {
        final ProgressBar progressHorizontal = (ProgressBar) super.getApp().findViewById(R.id.pbLoading);

        if (null == progressHorizontal) {
            return;
        }

        progressHorizontal.setProgress(0);
        progressHorizontal.setSecondaryProgress(0);
        m_nPosition = 0;
    }

    public void setProgressPosition(int nPosition) {
        final ProgressBar progressHorizontal = (ProgressBar) super.getApp().findViewById(R.id.pbLoading);

        progressHorizontal.setProgress(nPosition);
    }

    public void setInfo(String szInfo) {
        TextView tvConnStatus = (TextView) super.getApp().findViewById(R.id.tvConnect);

        tvConnStatus.setText(szInfo);
    }

    public int getProgressPosition() {
        final ProgressBar progressHorizontal = (ProgressBar) super.getApp().findViewById(R.id.pbLoading);

        if (null == progressHorizontal) {
            return 0;
        }

        return progressHorizontal.getProgress();
    }
}

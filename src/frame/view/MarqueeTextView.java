//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



/**
 * @author jugo
 * @date 2009-12-03
 */
package frame.view;


import android.content.Context;

import android.graphics.Rect;

import android.text.Layout;
import android.text.TextPaint;

import android.util.AttributeSet;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;

import android.widget.Scroller;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
    private static final String TAG          = "MarqueeTextView";
    private int                 mRndDuration = 20000;
    private boolean             mPaused      = false;
    private int                 mndistance   = 0;
    private String              mszText      = null;
    private Scroller            mSlr;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSingleLine();
        setEllipsize(null);
    }

    /**
     * begin to scroll the text from the original position
     */
    public void startScroll() {
        int nViewWidth = getViewWidth();
        int nTextWidth = calculateScrollingLen();

        if (nViewWidth >= nTextWidth) {
            return;
        }

        setHorizontallyScrolling(true);
        mRndDuration = (nTextWidth * 100) / 2;
        mszText      = this.getText().toString();

        String szTmp = mszText + System.getProperty("line.separator") + System.getProperty("line.separator")
                       + System.getProperty("line.separator") + "_";

        this.setText(szTmp);
        mndistance = (int) Layout.getDesiredWidth(mszText, this.getPaint());
        mndistance += 20;
        szTmp      = mszText + System.getProperty("line.separator") + System.getProperty("line.separator")
                     + System.getProperty("line.separator") + System.getProperty("line.separator") + mszText;
        this.setText(szTmp);
        mPaused = true;
        resumeScroll();
    }

    public void setViewWidth(int nPix) {
        LayoutParams params = this.getLayoutParams();

        params.width = nPix;
        this.setLayoutParams(params);
    }

    /**
     * resume the scroll from the pausing point
     */
    public void resumeScroll() {
        if (!mPaused) {
            return;
        }

        setHorizontallyScrolling(true);

        if (null != mSlr) {
            mSlr = null;
        }

        // use LinearInterpolator for steady scrolling
        mSlr = new Scroller(this.getContext(), new LinearInterpolator());
        setScroller(mSlr);
        mSlr.startScroll(0, 0, mndistance, 0, mRndDuration);
        mPaused = false;
    }

    /**
     * calculate the scrolling length of the text in pixel
     *
     * @return the scrolling length in pixels
     */
    private int calculateScrollingLen() {
        TextPaint tp     = getPaint();
        Rect      rect   = new Rect();
        String    strTxt = getText().toString();

        tp.getTextBounds(strTxt, 0, strTxt.length(), rect);

        int scrollingLen = rect.width();    // + getWidth();

        rect = null;

        return scrollingLen;
    }

    /**
     * pause scrolling the text
     */
    public void pauseScroll() {
        if (null == mSlr) {
            return;
        }

        if (mPaused) {
            return;
        }

        mPaused = true;

        if (mSlr.isFinished()) {
            return;
        }

        mSlr.abortAnimation();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (null == mSlr) {
            return;
        }

        if (mSlr.isFinished() && (!mPaused)) {
            mPaused = true;
            this.resumeScroll();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if (hasWindowFocus) {
            resumeScroll();
        } else {
            pauseScroll();
        }
    }

    public int getRndDuration() {
        return mRndDuration;
    }

    public void setRndDuration(int duration) {
        this.mRndDuration = duration;
    }

    public boolean isPaused() {
        return mPaused;
    }

    private int getViewWidth() {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        int                    nWidth = params.width;

        return nWidth;
    }
}

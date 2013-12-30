//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.view;


import android.content.Context;

import android.os.Handler;
import android.os.Message;

import android.util.AttributeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import frame.event.EventHandler;

import frame.window.ThreadHandler;

public class MenuList extends RelativeLayout {
    private static final String TAG                 = "MenuList";
    public final int            ANIMATION_DOWN      = 0;
    public final int            ANIMATION_UP        = 1;
    private EventHandler        eventHandle         = null;
    private ViewFlipper         flipper             = null;
    private Context             mContext            = null;
    private int                 mnAnimationDuration = 300;
    private ThreadHandler       thdFlip             = null;
    private AnimationType       animationType       = new AnimationType();
    private Handler             updateHandler       = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0 :
                if (null != flipper) {
                    flipper.setVisibility(View.VISIBLE);
                }

                break;
            case 1 :
                if (null != flipper) {
                    flipper.setVisibility(View.GONE);
                }

                break;
            }
        }
    };

    public MenuList(Context context) {
        super(context);
        mContext = context;
        flipper  = new ViewFlipper(context);
    }

    public MenuList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        flipper  = new ViewFlipper(context);
    }

    public MenuList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        flipper  = new ViewFlipper(context);
    }

    public void setMenuView(int nResId, Handler handler, int nWidth, int nHeight) {
        if (null == mContext) {
            return;
        }

        if (null != handler) {
            eventHandle = new EventHandler(handler);
        }

        TextView tvTmp = new TextView(mContext);

        tvTmp.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout llmenu = new LinearLayout(mContext);

        llmenu.setLayoutParams(new LayoutParams(nWidth, nHeight));

        LayoutInflater layInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == layInflater.inflate(nResId, llmenu, true)) {
            return;
        }

        flipper.setLayoutParams(new LayoutParams(nWidth, nHeight));
        flipper.addView(tvTmp, 0);
        flipper.addView(llmenu, 1);
        flipper.setInAnimation(animationType.inFromDownAnimation(mnAnimationDuration));
        flipper.setOutAnimation(animationType.outToDownAnimation(mnAnimationDuration));
        flipper.setVisibility(GONE);
        flipper.buildDrawingCache();
        flipper.buildDrawingCache(true);

        if (this.getChildCount() > 0) {
            this.removeAllViews();
        }

        this.addView(flipper);
    }

    public void showMenu(boolean bShow) {
        if (bShow && (0 == flipper.getDisplayedChild())) {
            flipper.setVisibility(VISIBLE);
            flipper.showNext();
        }

        if (!bShow && (0 != flipper.getDisplayedChild())) {
            flipper.showNext();
            flipper.setVisibility(View.GONE);

            // ListViewGone();
        }
    }

    public void setAnimationDuration(int nDuration) {
        if ((0 >= nDuration) || (nDuration >= 10000)) {
            return;
        }

        mnAnimationDuration = nDuration;
    }

    public void setAnimation(int nAnimation) {
        switch (nAnimation) {
        case ANIMATION_UP :
            if (flipper != null) {
                flipper.setInAnimation(animationType.inFromDownAnimation(mnAnimationDuration));
                flipper.setOutAnimation(animationType.outToDownAnimation(mnAnimationDuration));
            } else {}

            break;
        case ANIMATION_DOWN :
            if (flipper != null) {
                flipper.setInAnimation(animationType.inFromTopAnimation(mnAnimationDuration));
                flipper.setOutAnimation(animationType.outToTopAnimation(mnAnimationDuration));
            } else {}

            break;
        default :
            if (flipper != null) {
                flipper.setInAnimation(animationType.inFromDownAnimation(mnAnimationDuration));
                flipper.setOutAnimation(animationType.outToDownAnimation(mnAnimationDuration));
            } else {}

            break;
        }
    }

    private void ListViewGone() {
        if (null != thdFlip) {
            thdFlip = null;
        }

        updateHandler.sendEmptyMessage(1);

/*
        thdFlip = new ThreadHandler(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    updateHandler.sendEmptyMessage(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thdFlip.start();
*/
    }

    public void setViewTouchEvent(View view) {
        if ((null == eventHandle) || (null == view)) {
            return;
        }

        eventHandle.registerViewOnTouch(view);
    }

    public boolean isShow() {
        if (null == flipper) {
            return false;
        }

        if (0 == flipper.getDisplayedChild()) {
            return false;
        } else {
            return true;
        }
    }
}

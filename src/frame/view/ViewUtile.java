//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.view;


import android.app.Activity;

import android.widget.ImageView;
import android.widget.TextView;

import frame.window.WndBase;

public class ViewUtile {
    private Activity mActivity = null;

    public ViewUtile(Activity activity) {
        mActivity = activity;
    }

    public void setTextViewSelected(int nResId, boolean bSelected) {
        if (!checkValide()) {
            return;
        }

        TextView tv = null;

        tv = (TextView) mActivity.findViewById(nResId);

        if (null != tv) {
            if (bSelected) {
                tv.setTextColor(WndBase.COLOR_FOCUS);
            } else {
                tv.setTextColor(WndBase.COLOR_WHITE);
            }
        }

        tv = null;
    }

    public void setImageSrc(int nResId, int nDrawableId) {
        if (!checkValide()) {
            return;
        }

        ImageView iv = null;

        iv = (ImageView) mActivity.findViewById(nResId);

        if (null != iv) {
            iv.setImageResource(nDrawableId);
        }

        iv = null;
    }

    private boolean checkValide() {
        if (null == mActivity) {
            return false;
        }

        return true;
    }
}

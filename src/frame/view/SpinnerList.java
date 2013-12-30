//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



/**
 * @author jugo
 * @description spinner list class
 * @use guide
 * spinListFrom = new SpinnerList(
 *                               activity,
 *                               R.layout.spinner_list,
 *                               baseLayout,
 *                               R.id.tvSpinnerEdit,
 *                               R.id.flipperSpinnerList);
 *               spinListFrom.setMargin(243, 0, 73, 0, R.id.rlSpinnerListMain);
 *               spinListFrom.initListView(
 *                               R.id.lvSpinnerMenu,
 *                               arrayData,
 *                               R.layout.spinner_list_item,
 *                               R.id.tvSpinnerItem);
 */
package frame.view;


import java.util.ArrayList;

import android.app.Activity;

import android.content.Context;

import android.graphics.drawable.ColorDrawable;

import android.os.Handler;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.RelativeLayout.LayoutParams;

public class SpinnerList {
    private static final String    TAG                  = "SpinnerList";
    private static int             mnCurSelected        = -1;
    public final int               ANIMATION_DOWN       = 0;
    public final int               ANIMATION_UP         = 1;
    private ViewFlipper            flipper              = null;
    private String[]               listData             = null;
    private ListView               listView             = null;
    private Handler                mHandler             = null;
    private int                    m_nAnimationDuration = 500;
    private int                    mnEditResId          = -1;
    private int                    mnListId             = -1;
    private int                    mnListItemId         = -1;
    RelativeLayout                 mrlParent            = null;
    private Thread                 thdViewUpdate        = null;
    private Activity               theAct               = null;
    private boolean                mbselectable         = true;
    private ArrayList<SpinnerList> marrSpinner          = new ArrayList<SpinnerList>();
    private OnClickListener        itemClickListener    = new OnClickListener() {
        public void onClick(View v) {
            mnCurSelected = v.getId();

            String szCaption = listData[mnCurSelected];

            setEditText(szCaption);

            Message msg = new Message();

            msg.arg1 = mnCurSelected;
            msg.arg2 = mnEditResId;
            mHandler.sendMessage(msg);
        }
    };
    private OnClickListener clickLister = new OnClickListener() {
        public void onClick(View v) {
            if (!mbselectable) {
                return;
            }

            int nId = flipper.getDisplayedChild();

            if (0 == nId) {
                Message msg = new Message();

                msg.arg1 = -1;
                msg.arg2 = -1;
                mHandler.sendMessage(msg);
                updateHandler.sendEmptyMessage(0);
            } else {
                ListViewGone();
            }

            flipper.showNext();
        }
    };
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0 :
                setInvalidView(true);
                mrlParent.bringToFront();
                flipper.setVisibility(View.VISIBLE);

                break;
            case 1 :
                setInvalidView(false);
                flipper.setVisibility(View.GONE);

                break;
            }
        }
    };

    public SpinnerList(Activity act, int nResId, RelativeLayout rlParent, int nEditResId, int nFlipperId,
                       Handler handler) {
        theAct      = act;
        mnEditResId = nEditResId;
        mrlParent   = rlParent;
        mHandler    = handler;

        LayoutInflater layInflater = (LayoutInflater) theAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == layInflater.inflate(nResId, rlParent, true)) {

            // Log.e(TAG, "LayoutInflater fail");
            return;
        }

        TextView tvEdit = (TextView) theAct.findViewById(nEditResId);

        if (null != tvEdit) {
            tvEdit.setOnClickListener(clickLister);
        } else {

            // Log.e(TAG, "TextView invalid");
            return;
        }

        flipper = (ViewFlipper) theAct.findViewById(nFlipperId);

        if (flipper != null) {
            flipper.setInAnimation(inFromTopAnimation());
            flipper.setOutAnimation(outToTopAnimation());
        } else {

            // Log.e(TAG, "flipper is invalid");
        }

        flipper.setVisibility(View.GONE);
    }

    public void initListView(int nListViewId, String[] pData, int nListId, int nListItemId) {
        if (listView != null) {
            listView = null;
        }

        mnListId     = nListId;
        mnListItemId = nListItemId;
        setListData(pData);
        listView = (ListView) theAct.findViewById(nListViewId);

        ColorDrawable divider = new ColorDrawable();

        listView.setDivider(divider);

        try {
            listView.setAdapter(new ListDataAdapter(theAct));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListData(String[] pData) {
        listData = pData;
    }

    public void setMargin(int nMarginLeft, int nMarginRight, int nMarginTop, int nMarginBottom, int nLayoutId) {
        LayoutParams rlparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        rlparams.leftMargin   = nMarginLeft;
        rlparams.rightMargin  = nMarginRight;
        rlparams.topMargin    = nMarginTop;
        rlparams.bottomMargin = nMarginBottom;

        RelativeLayout rlView = (RelativeLayout) theAct.findViewById(nLayoutId);

        rlView.setLayoutParams(rlparams);
    }

    public void setAnimationDuration(int nDuration) {
        if ((0 >= nDuration) || (nDuration >= 10000)) {
            return;
        }

        m_nAnimationDuration = nDuration;
    }

    public void setAnimation(int nAnimation) {
        switch (nAnimation) {
        case ANIMATION_UP :
            if (flipper != null) {
                flipper.setInAnimation(inFromDownAnimation());
                flipper.setOutAnimation(outToDownAnimation());
            } else {

                // Log.e(TAG, "flipper is invalid");
            }

            break;
        case ANIMATION_DOWN :
            if (flipper != null) {
                flipper.setInAnimation(inFromTopAnimation());
                flipper.setOutAnimation(outToTopAnimation());
            } else {

                // Log.e(TAG, "flipper is invalid");
            }

            break;
        default :
            if (flipper != null) {
                flipper.setInAnimation(inFromTopAnimation());
                flipper.setOutAnimation(outToTopAnimation());
            } else {

                // Log.e(TAG, "flipper is invalid");
            }

            break;
        }
    }

    private Animation inFromTopAnimation() {
        Animation inFromBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.02f);

        inFromBottom.setDuration(m_nAnimationDuration);
        inFromBottom.setInterpolator(new AccelerateInterpolator());

        return inFromBottom;
    }

    private Animation outToTopAnimation() {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -0.0f,
                                    Animation.RELATIVE_TO_PARENT, -1.0f);

        outToBottom.setDuration(m_nAnimationDuration);
        outToBottom.setInterpolator(new AccelerateInterpolator());

        return outToBottom;
    }

    private Animation inFromDownAnimation() {
        Animation inFromBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 1.0f,
                                     Animation.RELATIVE_TO_PARENT, 0.0f);

        inFromBottom.setDuration(m_nAnimationDuration);
        inFromBottom.setInterpolator(new AccelerateInterpolator());

        return inFromBottom;
    }

    private Animation outToDownAnimation() {
        Animation outToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
                                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.02f,
                                    Animation.RELATIVE_TO_PARENT, 1.0f);

        outToBottom.setDuration(m_nAnimationDuration);
        outToBottom.setInterpolator(new AccelerateInterpolator());

        return outToBottom;
    }

    public int getSelected() {
        return mnCurSelected;
    }

    public void setSelected(int nIndex) {
        if (null == listData) {
            return;
        }

        if ((0 > nIndex) || (nIndex > listData.length)) {
            return;
        }

        String szCaption = listData[nIndex];

        if (null == szCaption) {
            return;
        }

        setEditText(szCaption);
    }

    public void setSelected(int nIndex, int init) {
        if (null == listData) {
            return;
        }

        if ((0 > nIndex) || (nIndex > listData.length)) {
            return;
        }

        String szCaption = listData[nIndex];

        if (null == szCaption) {
            return;
        }

        setEditText(szCaption, init);
    }

    public void setEditText(String szCaption) {
        TextView tvTmp = (TextView) theAct.findViewById(mnEditResId);

        if (null != tvTmp) {
            tvTmp.setText(szCaption);
        }

        updateHandler.sendEmptyMessage(1);
        flipper.showNext();
    }

    public void setEditText(String szCaption, int init) {
        TextView tvTmp = (TextView) theAct.findViewById(mnEditResId);

        if (null != tvTmp) {
            tvTmp.setText(szCaption);
        }
    }

    private void ListViewGone() {
        if (null != thdViewUpdate) {
            thdViewUpdate = null;
        }

        thdViewUpdate = new Thread() {
            public void run() {
                try {
                    sleep(500);
                    updateHandler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thdViewUpdate.start();
    }

    public void setViewInvalid(SpinnerList spinnerList) {
        marrSpinner.add(spinnerList);
    }

    public void releaseViewInvalid() {
        marrSpinner.clear();
    }

    public void setSelectAble(boolean bAble) {
        mbselectable = bAble;
    }

    private void setInvalidView(boolean bInvalid) {
        if (null == marrSpinner) {
            return;
        }

        for (int i = 0; i < marrSpinner.size(); i++) {
            if (bInvalid) {
                marrSpinner.get(i).setSelectAble(false);
            } else {
                marrSpinner.get(i).setSelectAble(true);
            }
        }
    }

    public void hideDropList() {
        int nId = flipper.getDisplayedChild();

        if (0 != nId) {
            ListViewGone();
        }
    }

    public class ListDataAdapter extends BaseAdapter {
        private Context        m_context;
        private LayoutInflater m_inflater;

        public ListDataAdapter(Context c) {
            m_context  = c;
            m_inflater = LayoutInflater.from(this.m_context);
        }

        public int getCount() {
            int nCount = 0;

            if (null != listData) {
                nCount = listData.length;
            }

            return nCount;
        }

        public Object getItem(int nPosition) {
            return null;
        }

        public long getItemId(int nPosition) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView != null) {
                convertView = null;
            }

            convertView = m_inflater.inflate(mnListId, null);

            TextView tvItem = (TextView) convertView.findViewById(mnListItemId);

            tvItem.setId(position);
            tvItem.setText(listData[position]);
            tvItem.setOnClickListener(itemClickListener);

            return convertView;
        }
    }
}

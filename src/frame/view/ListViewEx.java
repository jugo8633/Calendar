//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.view;


import android.content.Context;

import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;

public class ListViewEx extends ListView {
    private static final String TAG           = "ListViewEx";
    OnTouchListener             touchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // TODO Auto-generated method stub
            int nAction = event.getAction();

            switch (nAction) {
            case MotionEvent.ACTION_MOVE :
                showScrollBar(true);

                break;
            case MotionEvent.ACTION_DOWN :
                showScrollBar(true);

                break;
            case MotionEvent.ACTION_UP :
                showScrollBar(false);

                break;
            }

            return false;
        }
    };
    OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            // TODO Auto-generated method stub
            showScrollBar(true);

            return false;
        }
    };

    public ListViewEx(Context context) {
        super(context);

        // TODO Auto-generated constructor stub
        initListView();
    }

    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
        initListView();
    }

    public ListViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // TODO Auto-generated constructor stub
        initListView();
    }

    private void initListView() {
        setOnTouchListener(touchListener);
        setOnItemLongClickListener(itemLongClickListener);
        showScrollBar(false);
    }

    public void showScrollBar(boolean bShow) {
        setVerticalScrollBarEnabled(bShow);
    }
}

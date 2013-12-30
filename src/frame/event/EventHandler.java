//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.event;


import android.app.Instrumentation;

import android.os.Handler;
import android.os.Message;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class EventHandler extends EventMessage {
    private static final String TAG             = "EventHandler";
    private Handler             m_handler       = null;
    private boolean             m_bEventConsume = false;
    OnTouchListener             listenerOnTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int nTouch = EVENT_HANDLE_ON_TOUCH;

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                nTouch = EVENT_HANDLE_ON_TOUCH_DOWN;

                break;
            case MotionEvent.ACTION_MOVE :
                nTouch = EVENT_HANDLE_ON_TOUCH_MOVE;

                break;
            case MotionEvent.ACTION_CANCEL :
                nTouch = EVENT_HANDLE_ON_TOUCH_CANCEL;

                break;
            case MotionEvent.ACTION_UP :
                nTouch = EVENT_HANDLE_ON_TOUCH_UP;

                break;
            case MotionEvent.ACTION_OUTSIDE :
                nTouch = EVENT_HANDLE_ON_TOUCH_OUTSIDE;

                break;
            default :
                nTouch = event.getAction();

                break;
            }

            sendMsg(nTouch, v.getId(), EVENT_HANDLE_ON_TOUCH);

            if (m_bEventConsume) {
                return true;
            } else {
                return false;
            }
        }
    };
    OnClickListener listenerOnClick = new OnClickListener() {
        public void onClick(View v) {
            sendMsg(EVENT_HANDLE_ON_CLICK, v.getId(), EVENT_HANDLE_ON_CLICK);
        }
    };

    public EventHandler() {}

    public EventHandler(Handler handler) {
        m_handler = handler;

        if (m_handler != null) {
            sendMsg(0, 0, EVENT_HANDLE_CREATED);
        }
    }

    public void init(Handler handler) {
        m_handler = handler;

        if (m_handler != null) {
            sendMsg(0, 0, EVENT_HANDLE_CREATED);
        }
    }

    public void registerViewOnClick(View v) {
        v.setOnClickListener(listenerOnClick);
    }

    public void registerViewOnTouch(View v) {
        v.setOnTouchListener(listenerOnTouch);
    }

    public void setEventConSume(boolean bConSume) {
        m_bEventConsume = bConSume;
    }

    private void sendMsg(int arg1, int arg2, int what) {
        if (m_handler == null) {
            return;
        }

        Message msg = new Message();

        msg.what = what;    // event...
        msg.arg1 = arg1;    // click or press...
        msg.arg2 = arg2;    // widget id
        m_handler.sendMessage(msg);
    }

    public void sendHomeKeyEvent() {
        Thread thd = new Thread() {
            @Override
            public void run() {

                // TODO Auto-generated method stub
                Instrumentation mInstr = new Instrumentation();
                KeyEvent        key    = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME);
                KeyEvent        key2   = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME);

                mInstr.sendKeySync(key);
                mInstr.sendKeySync(key2);
                key    = null;
                key2   = null;
                mInstr = null;
                super.run();
            }
        };

        thd.start();
    }
}

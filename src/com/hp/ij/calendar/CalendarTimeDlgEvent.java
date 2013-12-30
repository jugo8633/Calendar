package com.hp.ij.calendar;


import java.util.ArrayList;

import android.app.Activity;

import frame.event.EventMessage;

public class CalendarTimeDlgEvent implements CalendarEvent {
    private static final String TAG         = "CalendarTimeDlgEvent";
    private CalendarApplication application = null;

    public CalendarTimeDlgEvent(Activity actMain) {
        application = (CalendarApplication) actMain.getApplication();
    }

    public int handleEvent(int nMsg, Object objData) {

        // TODO Auto-generated method stub
        switch (nMsg) {
        case EventMessage.WND_SHOW :
            break;
        case EventMessage.WND_STOP :
            break;
        case EventMessage.WND_EXCP :
            break;
        case EventMessage.WND_BTN_OK :
            if (null != objData) {
                ArrayList<Integer> arData  = (ArrayList<Integer>) objData;
                int                nResId  = arData.get(0);
                int                nHour   = arData.get(1);
                int                nMinute = arData.get(2);
                int                nNoon   = arData.get(3);
                int                nRunWnd = application.getRunWnd();

                switch (nRunWnd) {
                case EventMessage.RUN_NEWEVENTWND :
                    updateNewEventTime(nResId, nHour, nMinute, nNoon);

                    break;
                case EventMessage.RUN_EDITEVENTWND :
                    updateEditEventTime(nResId, nHour, nMinute, nNoon);

                    break;
                }
            }

            break;
        case EventMessage.WND_FINISH :
            return EventMessage.WND_FINISH;
        }

        return 0;
    }

    private void updateEditEventTime(int nResId, int nHour, int nMinute, int nNoon) {
        CalendarNewEventWnd editEventWnd = ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_EDITEVENTWND));

        updateEventTime(editEventWnd, nResId, nHour, nMinute, nNoon);
    }

    private void updateNewEventTime(int nResId, int nHour, int nMinute, int nNoon) {
        CalendarNewEventWnd newEventWnd = ((CalendarNewEventWnd) application.getWindow(EventMessage.RUN_NEWEVENTWND));

        updateEventTime(newEventWnd, nResId, nHour, nMinute, nNoon);
    }

    private void updateEventTime(CalendarNewEventWnd eventWnd, int nResId, int nHour, int nMinute, int nNoon) {
        switch (nResId) {
        case R.id.btnNewEventTimeFrom :
            eventWnd.setTimeDataFrom(nHour, nMinute, nNoon);

            break;
        case R.id.btnNewEventTimeTo :
            eventWnd.setTimeDataTo(nHour, nMinute, nNoon);

            break;
        }

        eventWnd.invalidView(eventWnd.UPDATE_TIME);
    }
}

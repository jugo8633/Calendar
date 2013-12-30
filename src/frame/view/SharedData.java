//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.



package frame.view;


import android.app.Activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Handler;

import frame.window.WndBase;

public class SharedData extends WndBase {
    private SharedPreferences preference      = null;
    private String            mszFileName     = null;
    private final int         CURRENT_VERSION = 1;

    public SharedData(Activity active, Handler handler, int nId) {
        super(active, handler, nId);
    }

    public void initPreferences(String szFileName) {
        if (null == szFileName) {
            return;
        }

        mszFileName = szFileName;
        loadPreference();

        // preference = super.getApp().getSharedPreferences(szFileName, 0);
    }

    private void loadPreference() {
        int mVersion = 0;
        int mode     = Activity.MODE_PRIVATE;

        preference = super.getApp().getSharedPreferences(mszFileName, mode);
        mVersion   = preference.getInt("VERSION", 0);

        switch (mVersion) {
        case 1 :    // 代表V00.00.01的取法
            break;
        case 2 :    // 代表V00.00.02的取法
            break;
        default :
            break;
        }
    }

    private void saveVersionPreference(Editor editor) {
        editor.putInt("VERSION", CURRENT_VERSION);
    }

    public String getValue(String szKey) {
        if ((null == preference) || (null == szKey)) {
            return null;
        }

        String szValue = null;

        szValue = preference.getString(szKey, null);

        return szValue;
    }

    public void setValue(String szKey, String szValue) {
        if ((null == szKey) || (null == szValue) || (null == preference)) {
            return;
        }

        Editor editor = null;

        editor = preference.edit();

        if (null == editor) {
            return;
        }

        saveVersionPreference(editor);
        editor.putString(szKey, szValue);
        editor.commit();
    }

    public void closeWindow() {

        // TODO Auto-generated method stub
        preference = null;
    }

    public void showWindow(boolean show) {

        // TODO Auto-generated method stub
    }
}

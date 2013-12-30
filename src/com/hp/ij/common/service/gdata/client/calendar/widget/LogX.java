//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar.widget;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * It's a debug class for output more detail informations.
 *
 * @author Luke Liu
 * @version 1.0
 */
public class LogX {

	final private static String TAG = "androidx";

	public static void d(String s) {
//		Log.d(TAG, s);
	}

	public static void i(String s) {
//		Log.i(TAG, s);
	}

	public static void e(String s) {
//		Log.e(TAG, s);
	}

	public static void e(Throwable e) {
//		Log.e(TAG, "", e);
	}

	public static void e(String msg, Throwable e) {
//		Log.e(TAG, msg, e);
	}
	
	public static void dWithTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date dateNow = new Date();
//		Log.d(TAG, "Time: " + dateFormat.format(dateNow));
	}

}

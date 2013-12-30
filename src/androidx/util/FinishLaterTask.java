//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import android.app.Activity;
import android.content.Intent;

public class FinishLaterTask implements GUITask {
	private Activity activity;
	private int resultCode;
	private Intent data;
	private long millisBeforeDismiss;

	public FinishLaterTask(Activity activity, int resultCode, Intent data, long millisBeforeDismiss) {
		this.activity = activity;
		this.resultCode = resultCode;
		this.data = data;
		this.millisBeforeDismiss = millisBeforeDismiss;
	}

	public FinishLaterTask(Activity activity, int resultCode, long millisBeforeDismiss) {
		this(activity, resultCode, null, millisBeforeDismiss);
	}
	
	public FinishLaterTask(Activity activity, long millisBeforeDismiss) {
		this(activity, Activity.RESULT_OK, millisBeforeDismiss);
	}
	
	public void executeNonGuiTask() throws Exception {
		if (millisBeforeDismiss > 0) {
			Thread.sleep(millisBeforeDismiss);
		}
	}

	public void after_execute() {
		if (data != null) {
			activity.setResult(resultCode, data);
		} else {
			activity.setResult(resultCode);
		}
	}

	public void onFailure(Throwable t) {
		// TODO Auto-generated method stub
		activity.setResult(Activity.RESULT_CANCELED);
	}

}

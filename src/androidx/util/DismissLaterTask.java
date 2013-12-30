//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import android.app.Dialog;
import android.util.Log;

public class DismissLaterTask implements GUITask {
	private Dialog dialog;
	private long millisBeforeDismiss;
	
	public DismissLaterTask(Dialog dialog, long millisBeforeDismiss) {
		this.dialog = dialog;
		this.millisBeforeDismiss = millisBeforeDismiss;
	}
	
	public void executeNonGuiTask() throws Exception {
		Thread.sleep(millisBeforeDismiss);
	}

	public void after_execute() {
		dialog.dismiss();
	}

	public void onFailure(Throwable t) {
//		Log.e("androidx", "Exception!", t);
	}
}

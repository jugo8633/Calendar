//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

public class DialogUtil {
	private DialogUtil() {
	}

	public static void showAlertDialog(Context context, int resTitle, int resMessage, int resIcon, long millisToShow) {
		Dialog alert = (new AlertDialog.Builder(context).setTitle(
				resTitle).setIcon(resIcon)
				.setMessage(resMessage)).create();
		alert.show();
		// dismiss the alert dialog after 2 seconds
		GUITaskQueue.getInstance().addTask(new DismissLaterTask(alert, millisToShow));
	}

  public static void showAlertDialog(Context context, String title, String message, int resIcon, long millisToShow) {
    AlertDialog alert = (new AlertDialog.Builder(context).setIcon(resIcon)).create();
    alert.setTitle(title);
    alert.setMessage(message);
    alert.show();
    // dismiss the alert dialog after 2 seconds
    GUITaskQueue.getInstance().addTask(new DismissLaterTask(alert, millisToShow));
  }

}

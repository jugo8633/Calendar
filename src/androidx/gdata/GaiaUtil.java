//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.gdata;


import com.google.gwt.user.client.rpc.AsyncCallback;

import android.content.Context;
import androidx.util.GUITaskQueue;
import androidx.util.ProgressIndicator;

public class GaiaUtil {
  private GaiaUtil() {
  }

  public static void doLogin(String service, String username, String password, ProgressIndicator indicator,
      AsyncCallback<GaiaSession> callback, Context context) {
    GUITaskQueue.getInstance().addTask(indicator, new GaiaLoginTask(service, username, password, callback, context));
  }
}
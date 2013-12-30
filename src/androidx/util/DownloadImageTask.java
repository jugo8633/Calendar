//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.net.UrlX;
import androidx.util.GUITask;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class DownloadImageTask implements GUITask {
	private static final int MAX_IMAGE_BYTES = 2000000;
	private URL url;
	private AsyncCallback<Bitmap> resultReceiver;
	private Bitmap bitmap;
	
	public DownloadImageTask(URL url, AsyncCallback<Bitmap> resultReceiver) {
		this.url = url;
		this.resultReceiver = resultReceiver;
	}
	
	public void executeNonGuiTask() throws Exception {
		byte[] imageBytes = UrlX.doGetAndReturnBytes(url, MAX_IMAGE_BYTES);
		bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		if (bitmap == null) {
			throw new IOException(url.toString() + " is not an image");
		}
	}

	public void onFailure(Throwable t) {
		resultReceiver.onFailure(t);
	}

	public void after_execute() {
		resultReceiver.onSuccess(bitmap);
	}
}


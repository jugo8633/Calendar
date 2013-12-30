//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.callbacks;


import android.graphics.Bitmap;
import android.widget.ImageView;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An async callback that applies a bitmap to an ImageView
 * (or an error image in case of failure)
 * 
 */
public class ImageViewCallback implements AsyncCallback<Bitmap> {
  private ImageView imageView;
  private int errorImageResource;

  public ImageViewCallback(ImageView _imageView, int _errorImageResource) {
    imageView = _imageView;
    errorImageResource = _errorImageResource;
  }

  public void onSuccess(Bitmap result) {
    imageView.setImageBitmap(result);
  }

  public void onFailure(Throwable t) {
    imageView.setImageResource(errorImageResource);
  }

}

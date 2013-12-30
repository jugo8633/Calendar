//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.util;


import com.google.gwt.user.client.rpc.AsyncCallback;

public final class AsyncCallbackPair<T> implements AsyncCallback<T> {
	private AsyncCallback<T> firstResultReceiver;
	private AsyncCallback<T> secondResultReceiver;
	
	public AsyncCallbackPair(AsyncCallback<T> firstResultReceiver, AsyncCallback<T> secondResultReceiver) {
		this.firstResultReceiver = firstResultReceiver;
		this.secondResultReceiver = secondResultReceiver;
	}
	
	public void onFailure(Throwable t) {
		firstResultReceiver.onFailure(t);
		secondResultReceiver.onFailure(t);
	}

	public void onSuccess(T result) {
		firstResultReceiver.onSuccess(result);
		secondResultReceiver.onSuccess(result);
	}
}

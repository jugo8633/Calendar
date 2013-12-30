//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.google.gwt.user.client.rpc;


/**
 * Constructs a interface for provide two methods for callback on success and failure
 * 
 * @author Luke Liu
 */
public interface AsyncCallback<T> {
void onFailure(Throwable t);
void onSuccess(T result);
}

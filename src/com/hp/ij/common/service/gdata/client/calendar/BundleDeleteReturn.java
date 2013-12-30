//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import java.io.Serializable;

/**
 * A Bundle return from service to activity, indicate whether delete event succeed or fail.
 * 
 * @author Luke Liu
 *
 */
public class BundleDeleteReturn implements Serializable {

	private boolean boolDeleteSuccess;
	
	private int mIntSC;
	
	public BundleDeleteReturn(boolean boolDeleteSuccess) {
		this.boolDeleteSuccess = boolDeleteSuccess;
	}
	
	public BundleDeleteReturn(boolean boolDeleteSuccess, int intSC) {
		this.boolDeleteSuccess = boolDeleteSuccess;
		setmIntSC(intSC);
	}

	public void setBoolDeleteSuccess(boolean boolDeleteSuccess) {
		this.boolDeleteSuccess = boolDeleteSuccess;
	}

	public boolean isBoolDeleteSuccess() {
		return boolDeleteSuccess;
	}

	public void setmIntSC(int mIntSC) {
		this.mIntSC = mIntSC;
	}

	public int getmIntSC() {
		return mIntSC;
	}
	
}

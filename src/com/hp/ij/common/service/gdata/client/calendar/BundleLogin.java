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
 * A Bundle return from service to activity, indicate whether user log in succeed or fail.
 * 
 * @author Luke Liu
 *
 */
public class BundleLogin implements Serializable {
	
	/**
	 * true: login success, false: login fail
	 */
	private boolean blnLogin;
	
	/**
	 * HTTP result code
	 */
	private int intResultCode;
	
	public BundleLogin(boolean blnLogin) {
		this.blnLogin = blnLogin;
	}

	public boolean getLogin() {
		return blnLogin;
	}

	public void setIntResultCode(int intResultCode) {
		this.intResultCode = intResultCode;
	}

	public int getIntResultCode() {
		return intResultCode;
	}
	
}

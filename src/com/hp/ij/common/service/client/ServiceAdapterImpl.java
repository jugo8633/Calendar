//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.client;


import android.app.Activity;
import com.hp.ij.common.service.baseservice.IBaseService;

/**
 * Generate a ServiceAdapter
 * 
 * @author Luke Liu
 *
 */
public class ServiceAdapterImpl {
	
	public static IBaseService getService(Activity activity) {
		ServiceAdapter serviceAdapter = new ServiceAdapter(activity);
		return serviceAdapter.getService();
	}

}

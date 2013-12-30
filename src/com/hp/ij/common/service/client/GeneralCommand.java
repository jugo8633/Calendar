//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.client;


/**
 * Generate command to encapsulate object and method, also includes toggle to execute.
 * 
 * @author Luke Liu
 *
 */
public class GeneralCommand implements ICommand {

	private String mStrAppName = null;
	private String[] mStrArguments;
	
	public GeneralCommand(String strAppName, String[] strsArgument) {
		this.mStrAppName = strAppName;
		this.mStrArguments = strsArgument;
	}
	
	public void execute() {
		// TODO Auto-generated method stub
	}

	public String getAppName() {
		return mStrAppName;
	}

	public String[] getArgument() {
		// TODO Auto-generated method stub
		return mStrArguments;
	}

}

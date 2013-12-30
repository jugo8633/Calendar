//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.client;


public class IntentCommand {
	
	/**
	 * Intent action 
	 */
	public static final String INTENT_ACTION = "com.hp.service.gclient";
	
	/**
	 * Intent action from client
	 */
	public static final String INTENT_ACTION_CLIENT = "com.hp.service.gclient.client";
	
	/**
	 * GAIA service;
	 */
	public static final int GOOGLE_CALENDAR_LOG_AUTH = 1;
	
	public static final int GOOGLE_CALENDAR_RETRIEVE_CALENDAR = 2;
	
	public static final int GOOGLE_CALENDAR_RETRIEVE_EVENT = 3;
	
	public static final int GOOGLE_CALENDAR_RETRIEVE_EVENT_FREE = 4;
	
	public static final int GOOGLE_CALENDAR_RETRIEVE_SINGLE_EVENT = 5;
	
	public static final int GOOGLE_CALENDAR_ADD_EVENT = 6;
	
	public static final int GOOGLE_CALENDAR_ADD_RECURRENCE_EVENT = 7;

	public static final int GOOGLE_CALENDAR_DELETE_EVENT = 9;
	
	public static final int GOOGLE_CALENDAR_SET_CALENDAR = 10;
	
	public static final int GOOGLE_CALENDAR_UPDATE_EVENT = 11; 
	
	public static final int GOOGLE_CALENDAR_UPDATE_ALARM = 12; 
	
	public static final int GOOGLE_CALENDAR_LOGOUT = 13;
	
	public static final int GOOGLE_CALENDAR_REMINDER = 14;
	
	public static final int GOOGLE_CALENDAR_CLIENT = 15;
}

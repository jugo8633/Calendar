package com.hp.ij.common.service.baseservice;

import com.hp.ij.common.service.baseservice.IRemoteServiceCallback;

/**
 * 	
 *
 */
interface IBaseService {
 	
	int invokeMethod(String strAppName, in String[] strsArgument);

	/**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
    void registerCallback(IRemoteServiceCallback cb);
    
    /**
     * Remove a previously registered callback interface.
     */
    /*
    void unregisterCallback(IRemoteServiceCallback cb);
    */
	
}
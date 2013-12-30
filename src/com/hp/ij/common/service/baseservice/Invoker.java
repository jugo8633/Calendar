//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


/**
 * Invoker, hold commands, setCommand, request command to be executed.
 * 
 * @author Luke Liu
 *
 */
public class Invoker {
	
	/**
	 * Slot for store command.
	 */
	ICommand commandSlot;
	
	/**
	 * Initialize slot to store NoCommand;
	 */
	public Invoker() {
		commandSlot = new NoCommand();
	}
	
	/**
	 * Store Command to slot.
	 *  
	 * @param intSlot position of the slot.
	 * @param iCommand commands.
	 */
	public void setCommand(ICommand iCommand) {
		commandSlot = iCommand;
	}

	/**
	 * Execute Command to be executed.
	 */
	public void executeCommand() {
		commandSlot.execute();
	}
	
}

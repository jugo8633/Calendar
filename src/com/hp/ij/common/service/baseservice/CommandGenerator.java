//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;

/**
 * Use to generate the appropriate Command.
 * 
 * @author Luke Liu
 *
 */
public class CommandGenerator {
	
	public static ICommand genCommand(int intToken, String strMethod, String[] strArguments, BaseService baseService) {
		ICommand iCommand = new NoCommand();
		if(strMethod.compareTo("Login") == 0) {
			iCommand = new GaiaLoginCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Retrieve_Calendar") == 0) {
			iCommand = new RetrieveCalendarCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Retrieve_Event") == 0) {
			iCommand = new RetrieveEventCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Retrieve_Event_Free") == 0) {
			iCommand = new RetrieveEventFreeCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Retrieve_Single_Event") == 0) {
			iCommand = new RetrieveSingleEventCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Add_Event") == 0) {
			iCommand = new AddEventCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Add_Recurrence_Event") == 0) {
			iCommand = new AddRecurrenceEventCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Delete_Event") == 0) {
			iCommand = new DeleteEventCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Set_Calendar") == 0) {
			iCommand = new SetCalendarCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Logout") == 0) {
			iCommand = new LogoutCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Update_Event") == 0) {
			iCommand = new UpdateEventCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Application_Request") == 0) {
			iCommand = new ApplicationRequestCommand(strArguments, intToken, baseService);
		} else if (strMethod.compareTo("Application_Request_Widget") == 0) {
			iCommand = new ApplicationRequestWidgetCommand(strArguments, intToken, baseService);
		}	
		
		return iCommand;
	}

}

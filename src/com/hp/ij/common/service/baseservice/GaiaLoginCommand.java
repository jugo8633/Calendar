//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import java.io.IOException;
import androidx.LogX;
import androidx.gdata.GaiaLoginTask;
import androidx.gdata.GaiaSession;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.BundleLogin;
import com.hp.ij.common.service.gdata.client.calendar.CalendarState;
import android.content.Intent;
import android.os.Bundle;

/**
 * Google Authentication for Installed Application 
 * 
 * @author Luke Liu
 *
 */
public class GaiaLoginCommand implements ICommand, AsyncCallback<GaiaSession> {

	private int intToken;
	private GaiaLoginTask gaiaLoginTask;
	private BaseService baseService;

	private GaiaSession gaiaSession;
	
	public GaiaLoginCommand(String[] strArguments, int intToken, BaseService baseService) {
		this.gaiaLoginTask = new GaiaLoginTask(GaiaSession.SERVICE_CALENDAR, strArguments[0], strArguments[1], this, baseService);
		this.intToken = intToken;
		this.baseService = baseService;
		
		CalendarState.setStrUsername(strArguments[0]);
	}
	
	public void execute() {
		try {
			gaiaLoginTask.executeNonGuiTask();
			gaiaLoginTask.after_execute();
		} catch (IOException iOException) {
			gaiaLoginTask.onFailure(iOException);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onFailure(Throwable t) {
		BundleLogin bundleLogin = new BundleLogin(false);
		bundleLogin.setIntResultCode(Integer.parseInt(t.getMessage()));
		Bundle bundleSend = new Bundle();
		bundleSend.putSerializable("Data" , bundleLogin);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_LOG_AUTH);
		intentSend.putExtras(bundleSend);
		
		baseService.sendBroadcast(intentSend);
		LogX.d("false");
	}

	public void onSuccess(GaiaSession result) {
		CalendarState.setGaiaSession(result);
		
		BundleLogin bundleLogin = new BundleLogin(true);
		Bundle bundleSend = new Bundle();
		bundleSend.putSerializable("Data" , bundleLogin);
		
		Intent intentSend = new Intent(IntentCommand.INTENT_ACTION);
		intentSend.setFlags(IntentCommand.GOOGLE_CALENDAR_LOG_AUTH);
		intentSend.putExtras(bundleSend);
		
		baseService.sendBroadcast(intentSend);
		LogX.d(result.getToken());
	}

}

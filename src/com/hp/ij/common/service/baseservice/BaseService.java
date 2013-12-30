//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.baseservice;


import java.util.Random;
import com.hp.ij.common.service.baseservice.IBaseService;
import com.hp.ij.common.service.baseservice.IRemoteServiceCallback;
import com.hp.ij.common.service.client.IntentCommand;
import com.hp.ij.common.service.gdata.client.calendar.BundleLogin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.LogX;
 
/**
 * End point for access Service.
 * 
 * @author Luke Liu
 *
 */
public class BaseService extends Service {

	public class BaseServiceImpl extends IBaseService.Stub {
		
		/**
		 * Return token > 0, simultaneously generate Command object
		 */
		public int invokeMethod(final String strMethodName, final String[] strArguments)
				throws RemoteException {
			final int intToken = getRandom();
			Thread threadExecuteReceiver = new Thread() {
				
				public void run() {
					Invoker invoker = new Invoker();
					invoker.setCommand(CommandGenerator.genCommand(intToken, strMethodName, strArguments, BaseService.this));
					invoker.executeCommand();
				}
				
			};
			
			threadExecuteReceiver.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			threadExecuteReceiver.start();
			
			return intToken;
		}

		public void registerCallback(IRemoteServiceCallback cb)
				throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		/*
		public void unregisterCallback(IRemoteServiceCallback cb)
				throws RemoteException {
			// TODO Auto-generated method stub
		}
		*/
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		LogX.i("onBind( ) called in BaseService.");
		registerIntent();
		return new BaseServiceImpl();
	}
	
	/**
	 * Generate a random int as a token.
	 * 
	 * @return int token
	 */
	private int getRandom() {
		Random random = new Random();
		int intReturn = random.nextInt();
		while(intReturn <= 0) intReturn = random.nextInt();
		return intReturn;
	}

	/**
	 * Register an IntentFilter, can be requested by other Intent.
	 */
	private void registerIntent() {
		this.registerReceiver(BroadcastReceiverFromAgent, new IntentFilter(IntentCommand.INTENT_ACTION_CLIENT));
	}
	
	public BroadcastReceiver BroadcastReceiverFromAgent = new BroadcastReceiver() {

		String strMethodName = "Application_Request";
		String[] strArguments;

		public void onReceive(Context context, Intent intent) {
			final int intToken = getRandom();

			if(intent.getAction().compareTo(IntentCommand.INTENT_ACTION_CLIENT) == 0) {
				switch(intent.getFlags()) {
				case IntentCommand.GOOGLE_CALENDAR_CLIENT:
					strArguments = intent.getStringArrayExtra("Data");
					break;
				}
			}
			
			Thread threadExecuteReceiver = new Thread() {
				
				public void run() {
					Invoker invoker = new Invoker();
					invoker.setCommand(CommandGenerator.genCommand(intToken, strMethodName, strArguments, BaseService.this));
					invoker.executeCommand();
				}
				
			};
			
			threadExecuteReceiver.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			threadExecuteReceiver.start();
		}
		
	};

}

//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.client;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.LogX;
import com.hp.ij.common.service.baseservice.IBaseService;

/**
 * Provide interface interacts with BaseService(Agent class).
 * 
 * @author Luke Liu
 *
 */
public class ServiceAdapter {
	
	/**
	 * Reference to BaseService, Remote service.
	 */
	private IBaseService mBaseService = null;
	
	private Context mContext = null;
	
	private boolean boolRegisterReceiver;
	
	/**
	 * Get service
	 * 
	 * @param activity
	 */
	public ServiceAdapter(Context context) {
		context.bindService(new Intent(IBaseService.class.getName()), serviceConnection, Context.BIND_AUTO_CREATE);
		this.mContext = context;
		mContext.bindService(new Intent(IBaseService.class.getName()), serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * Release service
	 */
	public void releaseService() {
		this.mContext.unbindService(serviceConnection);
	}
	
	/**
	 * Create the unique BaseService.
	 * 
	 * @param activity, which can use BaseService.
	 * @return unique BaseService.
	 */
	public IBaseService getService() {
		while(mBaseService == null) {
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return mBaseService;
	}

	/**
	 * Make sure obtain BaseService. 
	 * @return
	 */
	public boolean getConnect() {
		while(mBaseService == null) {
			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return true;
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mBaseService = IBaseService.Stub.asInterface(service);
			LogX.i("Base Service started");
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mContext.unbindService(serviceConnection);
		}
		
	};
	
	public IBaseService getBaseService() {
		return mBaseService;
	}
	
	/**
	 * Invoke BaseService's invokeMethod
	 * 
	 * @param strAppName method name
	 * @param strsArgument arguments
	 * @return token, 0 represent false, other positive represent token used to find message in BoardcastReceiver
	 */
	public int invokeMethod(String strMethodName, String[] strsArgument) {
		int intReturnToken = 0;
		try {
			intReturnToken = mBaseService.invokeMethod(strMethodName, strsArgument);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return intReturnToken;
	}
	
	public int exeCommand(ICommand iCommand) {
		int intReturnToken = 0;
		try {
			intReturnToken = mBaseService.invokeMethod(iCommand.getAppName(), iCommand.getArgument());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return intReturnToken;
	}
	
}

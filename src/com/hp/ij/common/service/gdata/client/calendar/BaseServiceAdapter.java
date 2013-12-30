//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package com.hp.ij.common.service.gdata.client.calendar;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import com.hp.ij.common.service.baseservice.IBaseService;
import com.hp.ij.common.service.baseservice.IRemoteServiceCallback;
import androidx.LogX;

/**
 * The GoogleService class extends the basic GData {@link Service}
 * abstraction to add support for authentication and cookies.
 */
public class BaseServiceAdapter {
	
	// remote service 
	private IBaseService mIBaseService = null;
	
	public BaseServiceAdapter() {}
	
	/*
	 * Get a reference to the remote service, call bindService() rather than startService()
	 */
	public void bindService(Activity activityClient) {
		
		activityClient.bindService(new Intent(IBaseService.class.getName()), serviceConnectionCallBack, Context.BIND_AUTO_CREATE);
		
	}
	
	/*
	 * 
	 */
	private ServiceConnection serviceConnectionCallBack = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
			mIBaseService = IBaseService.Stub.asInterface(service);
            // We want to monitor the service for as long as we are
            // connected to it.
            try {
            	mIBaseService.registerCallback(mCallback);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            
            // As part of the sample, tell the user what happened.
            //Toast.makeText(BaseServiceAdapter.this, R.string.remote_service_connected, Toast.LENGTH_SHORT).show();

            
			/*
			String[] strsArg = {"authentication", "noArg"};
			try {
				mIBaseService.invokeMethod("Calendar", 1234, strsArg);
			} catch (RemoteException remoteException) {
				// TODO Auto-generated catch block
				remoteException.printStackTrace();
			}
			*/
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	};

	
    // ----------------------------------------------------------------------
    // Code showing how to deal with callbacks.
    // ----------------------------------------------------------------------
    
    /**
     * This implementation is used to receive callbacks from the remote
     * service.
     */
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
        /**
         * This is called by the remote service regularly to tell us about
         * new values.  Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */
        public void valueChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
            LogX.i("what:" + BUMP_MSG + " value: " + value);
            
        }
    };
    
    private static final int BUMP_MSG = 1;
    
    private String strCallbackText; 
    private Handler mHandler = new Handler() {
        @Override 
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BUMP_MSG:
                	strCallbackText = "Received from service: " + msg.arg1;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };

}

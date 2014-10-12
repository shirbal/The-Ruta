package com.hardwarehandlers;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PowerHandler {

	/** Power management controller */
	private WakeLock mWakeLock;
	
	private Activity mCurrentActivity = null;
	
	public PowerHandler(Activity activity)
	{
		mCurrentActivity = activity;
	}
	
	public void acquire()
	{
		mWakeLock.acquire();
	}
	
	public void release()
	{
		if (mWakeLock.isHeld()) {
			mWakeLock.release();
		}
	}
	
	public void init()
	{
		PowerManager pManager = (PowerManager) mCurrentActivity.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		
	}
}

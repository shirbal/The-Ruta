package com.hardwarehandlers;

import java.util.LinkedList;
import java.util.List;

import com.infrastructure.ISensorEvents;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorHandler implements SensorEventListener{

	private List<ISensorEvents> listeners;
	
	/** X sensor location */
	private float x;
	/** Y sensor location */
	private float y;
	/** Z sensor location */
	private float z;
	private Boolean first;
	/** Indicator if the phone was stolen */
	static Boolean sendStolen = false;
	
	/** Sensor manager */
	private SensorManager mSensorManager;
	/** Sensor instance */
	private Sensor mSensor;
	
	private Activity mCurrentActivity = null;
	
	public SensorHandler(Activity activity)
	{
		mCurrentActivity = activity;
		init();
	}
	
	public void start()
	{
		mSensorManager.registerListener(this, mSensor,SensorManager.SENSOR_ACCELEROMETER);
	}
	
	public void stop()
	{
		mSensorManager.unregisterListener(this);
	}
	
	public void RegisterCallback(ISensorEvents callback)
	{
		listeners.add(callback);
	}
	
	private void init()
	{
		mSensorManager = (SensorManager) mCurrentActivity.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		listeners = new LinkedList<ISensorEvents>();
	}
	
	public void dispose(){}
	
	/**
	 * Called when the sensor location being changes. It stops the recording and
	 * sends e-mail to the user that the phone was moved.
	 * 
	 * @param sensor
	 *            event
	 */
	public void onSensorChanged(SensorEvent event) {
		if (first) {
			// initial the first place taken by the sensor
			x = (event.values[0]);
			y = (event.values[1]);
			z = (event.values[2]);
			first = false;
		} else {
			// check if the sensor moved
			if ((event.values[0] - x > 5) || (event.values[1] - y > 5)
					|| (event.values[2] - z > 5)) {
				mSensorManager.unregisterListener(this);
				if (!sendStolen) {
					// if moved we should inform the user about it by e-mail
//					new SendMailStolen().execute(new EmailObject(
//							"The phone was moved from his initial position",
//							currentUser.getEmail()));
					sendStolen = true;
				}
				// stop recording because of theft
				notifyDeviceIsMoved();
			}
		}
	}

	private void notifyDeviceIsMoved() {
		for(ISensorEvents callback : listeners)
		{
			callback.onDeviceMoved();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
}

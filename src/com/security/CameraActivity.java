package com.security;


import com.hardwarehandlers.AudioHandler;
import com.hardwarehandlers.PowerHandler;
import com.hardwarehandlers.SensorHandler;
import com.image.CameraPreview;
import com.image.PreviewFramesHandler;
import com.infrastructure.ISensorEvents;
import com.security.R;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * The main activity of the recording algorithm Responsible for taking control
 * on the camera and enabling recording.
 * location
 * @extends Activity
 * @implements OnClickListener
 * @implements SensorEventListener
 */
public class CameraActivity extends Activity implements OnClickListener , ISensorEvents{

	/** Stop Button */
	Button stopButton;
	/** camera instance */
	private Camera mCamera;
	/** Our surface of the preview */
	private SurfaceView mSurfaceView;
	/** Preview Size */
	private Size mPreviewSize;
	/** Media Type Image definition */
	public static final int MEDIA_TYPE_IMAGE = 1;
	/** Media Type Video definition */
	public static final int MEDIA_TYPE_VIDEO = 2;
	/** Our Frame Layout of the preview */
	public FrameLayout mFrameLayout;
	/** Preview callback */
	public PreviewCallback mPreviewCallback;

	private SensorHandler mSensorHandler;
	
	private AudioHandler mAudioHandler;
	
	private PowerHandler mPowerHandler;

	/**
	 * The "constructor" of CameraActivity. Runs the first time the activity is
	 * called. Used for initialization.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cameraview);
		
		tryGetCameraInstance();
		initializeIfCameraAvilable();
	}

	/**
	 * 
	 */
	private void initializeIfCameraAvilable() {
		if(mCamera != null)
		{
			initializePreviewView();
			initializeHandlers();
			initializeCaptureButton();
		}
	}

	/**
	 * 
	 */
	private void initializeCaptureButton() {
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(this);
	}

	/**
	 * 
	 */
	private void initializeHandlers() {
		mSensorHandler = new SensorHandler(this);
		mAudioHandler = new AudioHandler(this);
		mPowerHandler = new PowerHandler(this);
	}

	/**
	 * 
	 */
	private void initializePreviewView() {
		mSurfaceView = new CameraPreview(this, mCamera);
		mFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);
		mFrameLayout.addView(mSurfaceView);
	}

	/**
	 * 
	 */
	private void tryGetCameraInstance() {
		// Create an instance of Camera
		// TODO: remove this counter to other place
		int counter = 5;
		while (mCamera == null && counter > 0) {
			mCamera = getCameraInstance();
			counter --;
		}
	}

	/**
	 * Called when the Back button pressed. The method enabling the ringemRecorderr back,
	 * stops the recording, enabling the phone go to sleep and goes back to the
	 * previous window.
	 */
	@Override
	public void onBackPressed() {
		mAudioHandler.unmute();
		// returning the ability of the phone to go to sleep
		mPowerHandler.release();
		// stop the preview
		mCamera.stopPreview();
		super.onBackPressed();
		finish();
	}

	/**
	 * Called when the CameraActivity start. It starts after OnCreate is called
	 * or when the activity comes back from pause. It re-initial all the needed
	 * fields and configuration.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// gets the parameters for the camera
		Camera.Parameters params = mCamera.getParameters();
		if (params != null) {
			mPreviewSize = params.getPreviewSize();
		}
		stopButton = (Button) findViewById(R.id.button_stop);
		stopButton.setOnClickListener(this);
		// initials the power manager
		
		mPowerHandler.acquire();
		
		mPreviewCallback = new PreviewFramesHandler(mPreviewSize, mCamera, mSurfaceView);
	}

	/**
	 * Called when the the activity goes to pause. The method enabling the
	 * ringer back, stops the recording, unregisters the sensor listener and
	 * goes back to the previous window.
	 */
	@Override
	protected void onPause() {
		// enabling the ringer back
		mAudioHandler.unmute();
		super.onPause();
		// returning the ability of the phone to go to sleep
		mPowerHandler.release();
		// stop the preview
		mCamera.stopPreview();
		
		mSensorHandler.stop();
		this.setResult(0, new Intent().putExtra("IsStolen",false ));
		super.onBackPressed();
		finish();
	}

	/** A safe way to get an instance of the Camera object. */
	private static Camera getCameraInstance() {
		Camera c = null;
		try {
			// try to open camera instance
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			Log.e("CameraActivity.getCameraInstance" , e.getMessage());
		}
		return c; // returns null if camera is unavailable
	}

	/** Called when some button is pressed */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_capture:
			startPreviewing();
			break;

		case R.id.button_stop:
			stopPreviewing();
			break;
		}
	}

	/**
	 * 
	 */
	private void stopPreviewing() {
		//TODO: put password when trying to stop the recording
		mAudioHandler.unmute();
		// Deactivating sensor
		mSensorHandler.stop();
		// stop receiving preview callback
		mCamera.setPreviewCallback(null);
	}
	
	/**
	 * 
	 */
	private void startPreviewing() {
		// disabling the ringer in security mode
		mAudioHandler.mute();
		// activating sensor
		mSensorHandler.start();
		mCamera.setPreviewCallback(mPreviewCallback);
	}



	@Override
	public void onDeviceMoved() {
		mAudioHandler.unmute();
		mPowerHandler.release();
		mSensorHandler.stop();
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		this.setResult(0, new Intent().putExtra("IsStolen",true ));
		finish();
	}

}

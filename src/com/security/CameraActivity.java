package com.security;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.security.R;

//import NewObjects.EmailObject;
//import SendingEmail.SendMailStolen;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.parse.ParseUser;
//import com.security.R.id;

/**
 * The main activity of the recording algorithm Responsible for taking control
 * on the camera and enabling recording.
 * 
 * @extends Activity
 * @implements OnClickListener
 * @implements SensorEventListener
 */
public class CameraActivity extends Activity implements OnClickListener,
		SensorEventListener {

	/** Stop Button */
	Button stopButton;
	/** camera instance */
	private Camera mCamera;
	/** Our surface of the preview */
	private SurfaceView mPreview;
	/** Preview Size */
	private Size previewSize;
	/** Media Type Image definition */
	public static final int MEDIA_TYPE_IMAGE = 1;
	/** Media Type Video definition */
	public static final int MEDIA_TYPE_VIDEO = 2;
	/** Our Frame Layout of the preview */
	public FrameLayout preview;
	/** Preview callback */
	public PreviewCallback pc;
	/** The media recorder for the video */
	private MediaRecorder mRecorder;
	/** the output file after capturing a video */
	public File videoFile;
	/** The user */
	public ParseUser currentUser;
	/** Power management for disabling the phone go sleep */
	PowerManager pm;
	/** Power management controller */
	PowerManager.WakeLock wl;
	/** Location manager */
	private LocationManager locationManger;
	/** Location provider */
	private String locationProvider;
	/** Geographic coder */
	private Geocoder geoCoder;
	/** List of addresses */
	private List<Address> addList;
	/** Sensor manager */
	private SensorManager mSensorManager;
	/** Sensor instance */
	private Sensor mSensor;
	/**
	 * audio management for disabling the phone from ringing while on security
	 * mode
	 */
	AudioManager audio;
	/** X sensor location */
	public float x;
	/** Y sensor location */
	public float y;
	/** Z sensor location */
	public float z;
	public Boolean first;
	/** Indicator if the phone was stolen */
	static Boolean sendStolen = false;

	/**
	 * The "constructor" of CameraActivity. Runs the first time the activity is
	 * called. Used for initialization.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cameraview);
		// initiates the user information from the DB
		currentUser = ParseUser.getCurrentUser();
		// Create an instance of Camera

		mCamera = getCameraInstance();
		while (mCamera == null) {
			mCamera = getCameraInstance();
		}

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);

		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(this);
		// Enabling sensor things
		first = true;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// initial audio manager
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

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
				mSensorManager.unregisterListener(CameraActivity.this);
				if (!sendStolen) {
					// if moved we should inform the user about it by e-mail
//					new SendMailStolen().execute(new EmailObject(
//							"The phone was moved from his initial position",
//							currentUser.getEmail()));
					sendStolen = true;
				}
				// stop recording because of theft
				FrameClass.canRecord = false;
				FrameClass.forceStopRec = true;
				if (FrameClass.recordingFlag == true)
					return;

				FrameClass.canRecord = false;
				if (wl.isHeld())
					wl.release();
				mCamera.stopPreview();
				SecurityCamActivity.stolenFlag = true;
				finish();
			}
		}
	}

	/**
	 * Called when the Back button pressed. The method enabling the ringer back,
	 * stops the recording, enabling the phone go to sleep and goes back to the
	 * previous window.
	 */
	@Override
	public void onBackPressed() {
		// enabling the ringer back
		audio.setRingerMode(2);
		if (FrameClass.recordingFlag == true) {
			return;
		}
		// stop recording
		FrameClass.canRecord = false;
		// returning the ability of the phone to go to sleep
		if (wl.isHeld()) {
			wl.release();
		}
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
			previewSize = params.getPreviewSize();
		}
		stopButton = (Button) findViewById(R.id.button_stop);
		stopButton.setOnClickListener(this);
		currentUser = ParseUser.getCurrentUser();
		// initials the power manager
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		// gps location initialize

		locationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		geoCoder = new Geocoder(this, new Locale("en_IL"));

		locationProvider = LocationManager.NETWORK_PROVIDER;

		Location location = locationManger
				.getLastKnownLocation(locationProvider);

		try {
			
			if (location != null)
				addList = geoCoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);

		
		} catch (IOException e) {
			

			// e.printStackTrace();
			Log.e("get address", e.getMessage());
		}
		
		String fileLoc=null;
		if (addList != null) {
			fileLoc = addList.get(0).getThoroughfare() + "-"
					+ addList.get(0).getLocality();
		}

	
		wl.acquire();
		pc = new FrameClass(previewSize, mCamera, mRecorder, mPreview, fileLoc);
		
	}

	/**
	 * Called when the the activity goes to pause. The method enabling the
	 * ringer back, stops the recording, unregisters the sensor listener and
	 * goes back to the previous window.
	 */
	@Override
	protected void onPause() {
		// enabling the ringer back
		audio.setRingerMode(2);
		super.onPause();
		// stop recording
		FrameClass.canRecord = false;
		FrameClass.forceStopRec = true;
		// returning the ability of the phone to go to sleep
		if (wl.isHeld()) {
			wl.release();
		}
		// stop the preview
		mCamera.stopPreview();
		mSensorManager.unregisterListener(CameraActivity.this);
		sendStolen = false;
		super.onBackPressed();
		finish();
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			// try to open camera instance
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			return null;
		}
		return c; // returns null if camera is unavailable
	}

	/** Called when some button is pressed */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_capture:
			// disabling the ringer in security mode
			audio.setRingerMode(0);
			// enabling recording
			FrameClass.forceStopRec = false;
			FrameClass.canRecord = true;
			// activating sensor
			mSensorManager.registerListener(CameraActivity.this, mSensor,
					SensorManager.SENSOR_ACCELEROMETER);
			mCamera.setPreviewCallback(pc);
			break;

		case R.id.button_stop:
			// disabling recording
			FrameClass.canRecord = false;
			FrameClass.forceStopRec = true;
			// disactivating sensor
			mSensorManager.unregisterListener(CameraActivity.this);
			break;
		}
	}

	public void releaseMediaRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset(); // clear recorder configuration
			mRecorder.release(); // release the recorder object
			mRecorder = null;
			mCamera.lock(); // lock camera for later use
			FrameClass.recordingFlag = false;
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

}

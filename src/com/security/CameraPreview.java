package com.security;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class
 * 
 * @extends SurfaceView
 * @implements SurfaceHolder.Callback
 *
 */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	/**Surface Holder for the preview*/
	private SurfaceHolder mHolder;
	/**Camera instance*/
	private Camera mCamera;
	/**CameraPreview constructor
	 * 
	 * @param context instance
	 * @param camera instance
	 */
	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**Called after the surface for the preview has been created.
	 * @param surface holder
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder); //tells the camera where to draw the preview
			mCamera.startPreview();
		} catch (IOException e) {
			return;
		}
	}

	/**Needed only for implementation*/
	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	/**Called when the surface was changed.
	 * The method stops the previous preview and starts a new one.
	 * @param surface holder
	 * @param format
	 * @param widht
	 * @param height
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
		}
	}
}
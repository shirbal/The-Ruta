package com.hardwarehandlers;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceView;


public interface IVideoService {

	MediaRecorder mRecorder = null;
	
	Camera mCamera = null;
	
	SurfaceView mPreviewSurface = null;
	
	String currentVideoRecorderedPath = "";
	
	/**
	 * 
	 */
	public void start();
	
	/**
	 * 
	 * @return
	 */
	public boolean prepare();
	
	/**Release the media recorder.
	 * The method releases the media recorder and the camera 
	 * so others will be able to use it. 
	 */
	public void disposeRecorder();
	
	/**
	 * 
	 * @return
	 */
	public String getCurrentVideoFilePath();
}

package com.hardwarehandlers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceView;

public class VideoService implements IVideoService{

	private MediaRecorder mRecorder = null;
	private Camera mCamera = null;
	private SurfaceView mPreviewSurface = null;
	
	private String currentVideoRecorderedPath = "";
	
	public VideoService( Camera camera, SurfaceView previewSurface )
	{
		mCamera = camera;
		mPreviewSurface = previewSurface;
	}
	
	/**
	 * 
	 */
	public void start()
	{
		if(mRecorder != null)
		{
			mRecorder.start();	
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean prepare() {
		mRecorder = new MediaRecorder();

		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();

		mRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setVideoSize(640, 480);
		mRecorder.setVideoFrameRate(10);
		mRecorder.setVideoEncodingBitRate(500000);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

		
		mRecorder.setOutputFile(getOutputMediaFile(2).toString());

		// Step 5: Set the preview output
		mRecorder.setPreviewDisplay(mPreviewSurface.getHolder().getSurface());

		// Step 6: Prepare configured MediaRecorder
		try {
			mRecorder.prepare();
		} catch (IllegalStateException e) {
			disposeRecorder();
			return false;
		} catch (IOException e) {
			disposeRecorder();
			return false;
		}
		return true;
	}
	
	/**
	 * Release the media recorder.
	 * The method releases the media recorder and the camera 
	 * so others will be able to use it. 
	 */
	public void disposeRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset(); // clear recorder configuration
			mRecorder.release(); // release the recorder object
			mRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}
	
	public String getCurrentVideoFilePath()
	{
		return currentVideoRecorderedPath;
	}
	
	/**Returns the file that the recorded video will be saved in.
	 * 
	 * @param type of file (Video/Image)
	 * @return the file to right to
	 */
	private File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"CameraSecurity");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US)
				.format(new Date());
		File mediaFile;
		if (type == 1)
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} 
		else if (type == 2)
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
			currentVideoRecorderedPath = "VID_" + timeStamp + ".mp4";
		} else {
			return null;
		}

		return mediaFile;
	}
	
}

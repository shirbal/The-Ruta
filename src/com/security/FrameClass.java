package com.security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceView;

import com.dataobjects.Param;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.workers.CluodUploadClass;

/**The most important class of the application.
 * This class is responsible for taking frames and comparing them,
 * and take the result and choose whether to start or stop recording.
 * After it stops its responsible to save the recorded video file. 
 * @implements PreviewCallback
 *
 */
public class FrameClass implements PreviewCallback {
	/**Preview size*/
	private Size previewSize;
	/**First frame*/
	private byte[] pic1 = null;
	/**Second frame*/
	private byte[] pic2 = null;
	/**The result of the frame check*/
	public static Boolean result = true;
	/**Counter that counts number of equal frames*/
	public static int SameFrame = 0;
	/**Flag indicates recording*/
	public static Boolean recordingFlag = false;
	/**Flag indicates if recording is possible*/
	public static Boolean canRecord = true;
	/**Flag indicates if forced stop recording is needed.*/
	public static Boolean forceStopRec =false;
	/**Camera instance*/
	public Camera mCamera = null;
	/**Surface Preview*/
	private SurfaceView mPreview;
	/**Media recorder*/
	private MediaRecorder mRecorder;
	/**String holds the filename of the recorded video*/
	private String fileName;
	/**Current user*/
	ParseUser currentUser;
	/**DB object*/
	ParseObject currentObj;
	/**String holds the location*/
	private String location;

	/**FrameClass constructor
	 * initializes all the fields needed
	 * @param preview size
	 * @param Camera instance
	 * @param Media Recorder
	 * @param Surface Preview
	 * @param location
	 */
	public FrameClass(Size size, Camera mCamera, MediaRecorder mRecorder,
			SurfaceView mPreview, String location) {
		this.previewSize = size;
		this.mCamera = mCamera;
		this.mRecorder = mRecorder;
		this.mPreview = mPreview;
		currentUser = ParseUser.getCurrentUser();
		currentObj = null;
		this.location = location;
		result = true;
		SameFrame = 0;
		canRecord = true;
		forceStopRec =false;
		
	}

	/**Copy one frame to the first frame
	 * 
	 * @param frame
	 */
	private void copyFrame(byte[] a) {
		if (a != null)
			pic1 = a;
	}

	/**The main function.
	 * This method called every frame taken by the phone.
	 * Each time a frame is taken, this frame is compared to the previous frame
	 * and if the difference is bigger then 10% in the last 10 frames,
	 * the frames considered different and recording starts.
	 * If you are already recording and the difference is smaller then 10%,
	 * the recording stops.
	 * @param frame to compare
	 * @param camera instance
	 */
	public void onPreviewFrame(byte[] data, Camera camera) {

		//setting up the video encoding
		YuvImage tempImg = new YuvImage(data, ImageFormat.NV21,
				previewSize.width, previewSize.height, null);
		
		ByteArrayOutputStream tempStream = new ByteArrayOutputStream(
				previewSize.width * previewSize.height);
		Rect rec = new Rect(0, 0, previewSize.width, previewSize.height);
		tempImg.compressToJpeg(rec, 100, tempStream);
		Bitmap bitmap = BitmapFactory.decodeByteArray(tempStream.toByteArray(),
				0, tempStream.size());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		
		copyFrame(pic2);//copy the frame to first frame
		pic2 = data;
		if (pic1 != null && pic2 != null) {
			result = compareFrames(pic1, pic2);//compare between the frames
		}
		//if frames are different and no one is recording and OK to record 
		if (result == false && !recordingFlag && canRecord) {
		
			if (prepareVideoRecorder()) {
				// Camera is available and unlocked, MediaRecorder is
				// prepared,
				// now you can start recording
				
				mRecorder.start();

				// inform the user that recording has started

				
				recordingFlag = true;
			} else {
				// prepare didn't work, release the camera
				releaseMediaRecorder();
				recordingFlag = false;
				// inform user
			}
		}
		//if 10 last frames are equal you are recording or you were forced to stop
		if ((result == true && recordingFlag && SameFrame > 10) || forceStopRec) {
			// stop recording and release camera
			
			forceStopRec= false;
			recordingFlag = false;

			releaseMediaRecorder(); // release the MediaRecorder object
			
			String newVideoPath = Environment
					.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_PICTURES).getAbsolutePath();//set the path to save the video

			newVideoPath += "/" + "CameraSecurity" + "/" + fileName;
			File fileToUpload = new File(newVideoPath);
			
			if(fileToUpload.length() <  10000000) {
				new CluodUploadClass().execute(new Param(fileToUpload,location));//upload the video file to the cloud		
			} else {
				return;
			}
		}
	}

	/**Compare 2 frames
	 * 
	 * @param first frame
	 * @param second frame
	 * @return if equal or not
	 */
	public Boolean compareFrames(byte[] p1, byte[] p2) {
		if (p1 == null || p2 == null)
			return true;
		int diff = 0;
		for (int i = 0; i < p1.length; i++) {
			if ((p1[i] - p2[i]) > 10 || (p1[i] - p2[i]) < -10)//if the pixel is different in more then 10 colors
				diff++;
		}
		if (diff == 0) {
			SameFrame++;//sums the same frames
			return true;
		}

		if (p1.length / diff < 10) {//checks if the difference is more then 10%
			SameFrame = 0;
			return false;
		}
		SameFrame++;
		return true;
	}

	/**Release the media recorder.
	 * The method releases the media recorder and the camera 
	 * so others will be able to use it. 
	 */
	public void releaseMediaRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset(); // clear recorder configuration
			mRecorder.release(); // release the recorder object
			mRecorder = null;
			mCamera.lock(); // lock camera for later use
			recordingFlag=false;
		}
	}

	/**Preparing the video record to record
	 * 
	 * @return if the recorder is ready or not
	 */
	public boolean prepareVideoRecorder() {
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
		mRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

		// Step 6: Prepare configured MediaRecorder
		try {
			mRecorder.prepare();
		} catch (IllegalStateException e) {
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	/**Returns the file that the recorded video will be saved in.
	 * 
	 * @param type of file (Video/Image)
	 * @return the file to right to
	 */
	public File getOutputMediaFile(int type) {
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
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == 1) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == 2) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
			fileName = "VID_" + timeStamp + ".mp4";
		} else {
			return null;
		}

		return mediaFile;
	}

}

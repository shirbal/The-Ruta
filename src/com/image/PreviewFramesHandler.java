package com.image;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceView;

import com.hardwarehandlers.IVideoService;
import com.hardwarehandlers.VideoService;
import com.infrastructure.ObjectFactory;
import com.infrastructure.users.IUserService;

/**
 * The most important class of the application.
 * This class is responsible for taking frames and comparing them,
 * and take the result and choose whether to start or stop recording.
 * After it stops its responsible to save the recorded video file. 
 * @implements PreviewCallback
 *
 */
public class PreviewFramesHandler implements PreviewCallback {
	/**Preview size*/
	private Size mPreviewSize;
	/**First frame*/
	private byte[] pic1 = null;
	/**Second frame*/
	private byte[] pic2 = null;
	/**Counter that counts number of equal frames*/
	private  int mEqualFramesCounter = 0;
	
	private boolean mIsRecording = false;
	
	private IVideoService mVideoService = null;
	
	private IUserService mUserService = null;

	/**FrameClass constructor
	 * initializes all the fields needed
	 * @param preview size
	 * @param Camera instance
	 * @param Media Recorder
	 * @param Surface Preview
	 * @param location
	 */
	public PreviewFramesHandler(Size size, Camera camera,
			SurfaceView previewSurface) {
		this.mPreviewSize = size;
		mVideoService = new VideoService(camera, previewSurface);
		intializeCurrentUser();
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
				mPreviewSize.width, mPreviewSize.height, null);
		
		ByteArrayOutputStream tempStream = new ByteArrayOutputStream(
				mPreviewSize.width * mPreviewSize.height);
		Rect rec = new Rect(0, 0, mPreviewSize.width, mPreviewSize.height);
		tempImg.compressToJpeg(rec, 100, tempStream);
		Bitmap bitmap = BitmapFactory.decodeByteArray(tempStream.toByteArray(),
				0, tempStream.size());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		
		copyFrame(pic2);//copy the frame to first frame
		pic2 = data;
		boolean compareResult = false;
		if (pic1 != null && pic2 != null) {
			compareResult = areFramesEqual(pic1, pic2);//compare between the frames
		}
		//if frames are different and no one is recording and OK to record 
		if (compareResult == false && mIsRecording == false) {
		
			if (mVideoService.prepare() == true) {
				// Camera is available and unlocked, MediaRecorder is
				// prepared,
				// now you can start recording
				mIsRecording = true;
				mVideoService.start();

				// inform the user that recording has started

			} else {
				// prepare didn't work, release the camera
				mVideoService.disposeRecorder();
				// inform user
			}
		}
		//if 10 last frames are equal you are recording or you were forced to stop
		if ((compareResult == true && mIsRecording == true && mEqualFramesCounter > 10)) {
			mEqualFramesCounter = 0;
			// stop recording and release camera
			mIsRecording = false;
			mVideoService.disposeRecorder(); // release the MediaRecorder object
			if(mUserService != null)
			{
				mUserService.upload(mVideoService.getCurrentVideoFilePath());
			}
		}
	}

	/**
	 * Compare 2 frames
	 * 
	 * @param first frame
	 * @param second frame
	 * @return if equal or not
	 */
	private Boolean areFramesEqual(byte[] p1, byte[] p2) {
		if (p1 == null || p2 == null)
			return true;
		int diff = 0;
		for (int i = 0; i < p1.length; i++) {
			if ((p1[i] - p2[i]) > 10 || (p1[i] - p2[i]) < -10)//if the pixel is different in more then 10 colors
				diff++;
		}
		if (diff == 0) {
			mEqualFramesCounter++;//sums the same frames
			return true;
		}

		if (p1.length / diff < 10) {//checks if the difference is more then 10%
			mEqualFramesCounter = 0;
			return false;
		}
		mEqualFramesCounter++;
		return true;
	}
	
	/**
	 * 
	 */
	private void intializeCurrentUser()
	{
		mUserService = (IUserService)ObjectFactory.instance().getInstance("IUserService");
	}

	/**Copy one frame to the first frame
	 * 
	 * @param frame
	 */
	private void copyFrame(byte[] a) {
		if (a != null)
			pic1 = a;
	}

}

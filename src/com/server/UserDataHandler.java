package com.server;

import com.infrastructure.UploadThreadPool;
import com.workers.UploadingRunnable;

public class UserDataHandler {
	
	/**
	 * Upload file located in parameter filePath
	 * @param fileName: File name (video name) to upload
	 */
	public void upload(String fileName)
	{
		UploadThreadPool.instance().startNew(new UploadingRunnable(fileName));
	}
	
	/**
	 * Returns true if there are uploading tasks, False otherwise
	 * @return
	 */
	public boolean isUploading()
	{
		return UploadThreadPool.instance().getUploadersCount() > 0;
	}
}

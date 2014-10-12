package com.workers;

import java.io.File;

import com.infrastructure.ObjectFactory;
import com.infrastructure.users.IUserService;

import android.os.AsyncTask;
import android.os.Environment;

/**
 * Class for background thread which will delete all recorded videos from phone
 * when exiting from the application
 * The thread runs asynchronously, and is called when the application is being closed
 *@type String the path to the directory where all files are saved
 *@type Integer - Thread progress type
 *@type Boolean - Thread result type
 */
public class CacheCleanerTask extends AsyncTask<Void, Integer, Void> {

	/**
	 * This is the main function of the thread
	 * which is called when executing the thread
	 * @param  params params[0] is the path to the directory where all files are saved
	 * @return 
	 */
	@Override
	protected Void doInBackground(Void... params) {
		IUserService userService = (IUserService)ObjectFactory.instance().getInstance("IUserService");
		if(userService != null)
		{
			while(userService.isUploading())
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		String filePath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getPath()
				+ File.separator + "CameraSecurity";
		File security = new File(filePath);
		//checks if directory exists
		if(security.exists() && security.isDirectory()) {
			File[] videoList = security.listFiles();
			//going over all the files and delete all
			for (File file : videoList) {
				file.delete();
			}
			security.delete();
		}
		return null;
	}

}

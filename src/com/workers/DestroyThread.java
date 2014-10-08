package com.workers;

import java.io.File;

import android.os.AsyncTask;

/**
 * Class for background thread which will delete all recorded videos from phone
 * when exiting from the application
 * The thread runs asynchronously, and is called when the application is being closed
 *@type String the path to the directory where all files are saved
 *@type Integer - Thread progress type
 *@type Boolean - Thread result type
 */
public class DestroyThread extends AsyncTask<String, Integer, Boolean> {

	/**
	 * This is the main function of the thread
	 * which is called when executing the thread
	 * @param  params params[0] is the path to the directory where all files are saved
	 * @return True if all deleted , False otherwise
	 */
	@Override
	protected Boolean doInBackground(String... params) {
		if(params[0] == null) {
			return false;
		}
		File security = new File(params[0]);
		//checks if directory exists
		if(security.exists() && security.isDirectory()) {
			File[] videoList = security.listFiles();
			//going over all the files and delete all
			for (File file : videoList) {
				file.delete();
			}
			security.delete();
		}
		return true;
	}

}

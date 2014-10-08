package com.dataobjects;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Hold a static method which returns an instance of ProgressDialog
 * To be used when using "loading" function and wants
 * to prevent from user to do something while loading
 */
public class ProgressObject {
	/** Returns an instance of ProgressDialog
	 *@Param c The context which used
	 *@param message The message which is displayed while progress bar is running
	 *@param style Value: 0 Creates a ProgressDialog with a circular, spinning progress bar. This is the default.
	 *				Value: 1 Creates a ProgressDialog with a horizontal progress bar.
	 */
	public static ProgressDialog getNewProgressBar(Context c, String message, int style) {
		ProgressDialog progressDialog = new ProgressDialog(c);
		progressDialog.setProgressStyle(style);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		return progressDialog;
	}
}

package com.workers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.dataobjects.Param;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * Class for background thread which will upload videos to
 * cloud if needed.
 * Converts the file to byte array. After converting
 * it save the byte[] to parse object and upload the object to cloud.
 * Finally it sends email alert to the user
 * The thread runs asynchronously
 *@type {@link @param}
 *@type Integer - Thread progress type
 *@type Boolean - Thread result type
 */
public class UploadingRunnable implements Runnable {
	
	private String mFileToUploadPath = "";
	
	// counter for own purpose use
	private int countOfTry = 0;
	
	public UploadingRunnable(String filePath)
	{
		mFileToUploadPath = filePath;
	}
	
	/**
	 * 
	 */
	public void run() {
		String newVideoPath = Environment
				.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES).getAbsolutePath();//set the path to save the video

		newVideoPath += "/" + "CameraSecurity" + "/" + mFileToUploadPath;
		File fileToUpload = new File(newVideoPath);
		if(fileToUpload.length() <  10000000)
		{
			Param param = new Param(fileToUpload,"");//location)
			String loc = null;
			if (param.getFileLoc() == null)
			{
				loc = "No Location Available";
			}
			else
			{
				loc= param.getFileLoc();
			}
			boolean result = false;
			// try 3 times to upload the file
			while(countOfTry < 3 && result == false)
			{
				result = uploadFile(param.getFile(), loc);
				countOfTry++;
			}
		}
	}

	/**
	 * The function of the Background thread, which
	 * uploads the given recorded video to the cloud
	 * 
	 * @param obj The file to upload
	 * @param location The GPS location of the recorded video
	 * @return True if uploaded , False otherwise
	 */
	private Boolean uploadFile(File obj, String location) {
		// counting the number of upload tries;
		countOfTry++;

		// gets the current user
		ParseUser currentUser = ParseUser.getCurrentUser();

		// gets the name of the file
		String fileName = obj.getName();

		byte[] fileAsByteArrayToUpload = null;

		//Try to get the byte[] from the video file
		//which was saved on the phone after it been recorded
		try {
			fileAsByteArrayToUpload = org.apache.commons.io.IOUtils
					.toByteArray(new BufferedInputStream(new FileInputStream(
							obj)));
		} catch (FileNotFoundException e2){
			return false;
		} catch (IOException e2) {
			return false;
		}

		// creating parseFile for uploading
		ParseFile fileToUpload = new ParseFile(fileName,
				fileAsByteArrayToUpload);
		// Try to upload the parsefile
		try {
			// first need to save the file
			fileToUpload.save();
		} catch (ParseException e) {
			return false;
		}

		// creating new parseObject to upload the file
		//to FILES directory in cloud
		// and initialize its attributes
		ParseObject parseObj = new ParseObject("Files");
		parseObj.put("email", currentUser.getEmail());
		parseObj.put("fileName", fileName);
		parseObj.put("file", fileToUpload);
		parseObj.put("Gps", location);

		// Now uploading the the parseOject to cloud
		try {
			parseObj.save();
		} catch (ParseException e) {
			Log.d("Uploading file", e.getMessage());
			e.printStackTrace();
			return false;
		}
		
//		ParseFile videoToPlay = (ParseFile) parseObj.get("file");
//		//gets the url of the uploaded file.
//		//This url will be send to the user by mail
//		String url = videoToPlay.getUrl();
//		Uri uri = Uri.parse(url);
		//creating a new instance of email object
//		EmailAlertRecording emailToSend = new EmailAlertRecording(
//				"Video recorded", currentUser.getEmail(), uri);
		//sending the mail with background thread
		//new SendMailRecording().execute(emailToSend);
		return true;
	}

	/**
	 * This is the main function of the thread
	 * which is called when executing the thread
	 * @param arg {@link @param}
	 * @return True if uploaded , False otherwise
	 */

}

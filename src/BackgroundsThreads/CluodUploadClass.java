package BackgroundsThreads;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//import NewObjects.EmailAlertRecording;
import NewObjects.Param;
//import SendingEmail.SendMailRecording;
//import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
public class CluodUploadClass extends AsyncTask<Param, Integer, Boolean> {
	// static counter for own purpose use
	static int countOfTry = 0;

	/**
	 * This function gets the video file which was created and
	 * saved on the phone and ready to be upload to phone and
	 * converts it to byte array so it can be uploaded to cloud
	 * 
	 * @param file The file which is saved on the
	 * phone and will be uploaded to cloud
	 * @return The byte array which will be saved on the cloud
	 * @throws IOException throws in case an error reading occurs
	 */

	@SuppressWarnings("resource")
	public byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
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
		} catch (FileNotFoundException e2) {
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
	@Override
	protected Boolean doInBackground(Param... arg) {
		String loc=null;
		if (arg[0].getFileLoc() == null){
			loc = "No Location Available";
		}else{
			loc= arg[0].getFileLoc();
		}
		Boolean result = uploadFile(arg[0].getFile(), loc);
		if (result) {
			countOfTry = 0;
			return result;
		} else {
			if (countOfTry < 2) {
				return uploadFile(arg[0].getFile(), arg[0].getFileLoc());
			}
			return false;
		}
	}
}

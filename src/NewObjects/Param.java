package NewObjects;

import java.io.File;

/**
 * Param object is used when transferring arguments to
 * the thread which will upload the recorded video
 * The Param class holds the File to upload and the relevant 
 * GPS location of the phone
 */
public class Param {
	/** The file to upload */
	private File file;
	/** The GPS location of the recorded video */
	private String fileLoc;
	
	/** C'tor */
	public Param(File file, String fileLoc){
		this.file = file;
		this.fileLoc = fileLoc;
	}
	
	public File getFile(){
		return this.file;
	}
	
	public String getFileLoc(){
		return this.fileLoc;
	}
}

package NewObjects;

import android.net.Uri;


/**
 * This class is represents email object which will be send to the user
 * when video was recorded and uploaded to cloud.
 * Holds the url of the uploaded video
 * @extends  @EmailObject
 *
 */
public class EmailAlertRecording extends EmailObject {
	/** The uploaded video Url */
	private Uri videoUrl;

	/** C'tor */
	public EmailAlertRecording(String emailBody, String userEmail, Uri videoUrl) {
		super(emailBody, userEmail);
		this.videoUrl = videoUrl;
	}

	public Uri getVideoUrl() {
		return this.videoUrl;
	}

}

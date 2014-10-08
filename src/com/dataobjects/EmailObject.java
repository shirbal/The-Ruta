package com.dataobjects;

/**
 * This class represents an email object which will be
 * used to send to the user in case video  was
 * recorded or phone was stolen
 */
public class EmailObject {
	
	/** The body of the mail */
	private String emailBody;
	/** The user email address */
	private String userEmail;

	/** C'tor */
	public EmailObject(String emailBody, String userEmail) {
		this.emailBody = emailBody;
		this.userEmail = userEmail;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	public String getEmailBody() {
		return this.emailBody;
	}
}

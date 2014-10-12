package com.infrastructure.users;

import com.dataobjects.LoginResult;
import com.dataobjects.SignupResult;

public interface IUserService {

	/**
	 * 
	 * @param username
	 * @param userEmail
	 * @param userPassword
	 * @return
	 */
	public SignupResult signUp(String username, String userEmail, String userPassword);
	
	/** 
	 * 
	 * @param username
	 * @param userPassword
	 * @return
	 */
	public LoginResult logIn(String username, String userPassword);
	
	/**
	 * 
	 * @return
	 */
	public boolean logOut();
	
	
	/**
	 * 
	 * @param filePath
	 */
	public void upload(String filePath);
	
	/**
	 * 
	 * @return
	 */
	public boolean isUploading();
}

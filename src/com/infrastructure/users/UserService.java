package com.infrastructure.users;


import com.dataobjects.LoginResult;
import com.dataobjects.SignupResult;
import com.enums.InputParametersEnum;
import com.enums.LoginResultEnum;
import com.enums.SignupResultEnum;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.server.UserDataHandler;

public class UserService implements IUserService{

	private ParseUser mCurrentUser;
	
	public UserService()
	{
		mCurrentUser = new ParseUser();
		mDataHandler = new UserDataHandler();
	}
	
	private UserDataHandler mDataHandler = null;
	
	/** 
	 * Method Name: signIn
	 * Description: Try to sign in a new user.First validate all three parameters.
	 * 				If all is validate, than try to sign in.
	 * 
	 * Parameters:  username = User name, must be not empty
	 * 				userEmail = The user email, must be a valid address
	 * 				userPassword = password, must be  8 characters
	 * 
	 * 
	 * Return Value: SignintResult= Success if all is good. Otherwise according to the wrong parameter
	 * Create By:
	 * Date:
	 * Last update by:
	 * Last Data update:
	 * */
	public SignupResult signUp(String username, String userEmail, String userPassword)
	{
		SignupResult signResult = new SignupResult();
		if(isUsernameValid(username) == InputParametersEnum.UsernameInvalid)
		{
			signResult.InputResult = InputParametersEnum.UsernameInvalid;
			signResult.Result = SignupResultEnum.Parameter_Fail;
		}
		else
		{
			if(isEmailValid(userEmail) == InputParametersEnum.EmailInvalid)
			{
				signResult.InputResult = InputParametersEnum.EmailInvalid;
				signResult.Result = SignupResultEnum.Parameter_Fail;
			}
			else
			{
				if(isPasswordValid(userPassword) == InputParametersEnum.PasswordInvalid)
				{
					signResult.InputResult = InputParametersEnum.PasswordInvalid;
					signResult.Result = SignupResultEnum.Parameter_Fail;
				}
				else
				{
					mCurrentUser.setUsername(username);
					mCurrentUser.setPassword(userPassword);
					mCurrentUser.setEmail(userEmail);
					trySignUp(signResult);
				}
			}
		}

		return signResult;
	}
	
	/**
	 * 
	 */
	public LoginResult logIn(String username, String userPassword)
	{
		LoginResult loginResult = new LoginResult();
		if(isUsernameValid(username) == InputParametersEnum.UsernameInvalid)
		{
			loginResult.InputResult = InputParametersEnum.UsernameInvalid;
			loginResult.Result = LoginResultEnum.Parameter_Fail;
		}
		else
		{
			if(isPasswordValid(userPassword) == InputParametersEnum.PasswordInvalid)
			{
				loginResult.InputResult = InputParametersEnum.PasswordInvalid;
				loginResult.Result = LoginResultEnum.Parameter_Fail;
			}
			else
			{
				// Parameter are o.k
				trylogIn(loginResult,username,userPassword);
			}
		}
		return loginResult;
	}
	
	/**
	 * 
	 */
	public boolean logOut()
	{
		boolean logoutResult = true;
		if( mCurrentUser != null)
		{
				ParseUser.logOut();
		}
		return logoutResult;
	}
	
	/**
	 * 
	 */
	public void upload(String filePath)
	{
		mDataHandler.upload(filePath);
	}
	
	/**
	 * 
	 */
	public boolean isUploading()
	{
		return mDataHandler.isUploading();
	}
	
	/**
	 * 
	 * @param result
	 */
	private void trySignUp(SignupResult result) {
		try {
			mCurrentUser.signUp();
		} catch (ParseException ex) {
			ex.printStackTrace();
			result.Message = ex.getMessage();
			result.Result = SignupResultEnum.Parameter_Fail;
		}
	}

	/**
	 * 
	 * @param userPassword
	 * @return
	 */
	private InputParametersEnum isPasswordValid(String userPassword) {
		InputParametersEnum res = InputParametersEnum.PasswordValid;
		// TODO: complete logic
		return res;
	}

	
	/**
	 * 
	 * @param userEmail
	 * @return
	 */
	private InputParametersEnum isEmailValid(String userEmail) {
		InputParametersEnum res = InputParametersEnum.EmailValid;
		// TODO: complete logic
		return res;
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	private InputParametersEnum isUsernameValid(String username) {
		InputParametersEnum res = InputParametersEnum.UsernameValid;
		// TODO: complete logic
		return res;
	}
	
	/**
	 * 
	 * @param loginResult
	 * @param username
	 * @param userPassword
	 */
	private void trylogIn(LoginResult loginResult, String username, String userPassword) {
		try {
			ParseUser user = ParseUser.logIn(username, userPassword);
			if(user != null)
			{
				this.mCurrentUser = user;
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
			loginResult.Message = ex.getMessage();
			loginResult.Result = LoginResultEnum.Parse_Fail;
		}
	}
	
}

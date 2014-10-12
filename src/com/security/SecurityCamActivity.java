package com.security;


import com.security.R;
import com.workers.CacheCleanerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dataobjects.LoginResult;
import com.dataobjects.ProgressObject;
import com.enums.InputParametersEnum;
import com.enums.LoginResultEnum;
import com.infrastructure.ObjectFactory;
import com.infrastructure.users.IUserService;
import com.infrastructure.users.UserService;
import com.parse.Parse;

/**The main (first run) class of the application
 * its initializes the cloud and let you choose if you want to login, register or
 * to retrieve lost password
 * 
 * @extends Activity
 * @implements OnClickListener
 *
 */
public class SecurityCamActivity extends Activity implements OnClickListener {
	/**Login button*/
	Button logIn;
	/**UserName and Password fields*/
	EditText editUser, editPass;
	/**Text view fields*/
	TextView forgotPass, signUp;
	/**String that holds the UserName and the Password*/
	/**Dialog*/
	AlertDialog.Builder loginAlertDialog;
	/**progress dialog*/
	ProgressDialog loginDialog;
	
	IUserService userHandler = null;

	/**The "constructor" of SecurityCamActivity.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "wQbQt0bXRdUK7iGQO5MpxvI2QuLukmU4r8BWnd5F",
				"MU5X5m6IbcAz4JncTaLpocATTgpkNQqsvfhvCTLJ");//initializes the parse account
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		initializeViewBinding();

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InitializeFactoryObject();
		initializeDialogs();
		initializeUser();
	}
	
	private void InitializeFactoryObject() {
		ObjectFactory.instance().register("IUserHandler", new UserService());
	}
	/**
	 * 
	 */
	private void initializeDialogs() {
		loginDialog = ProgressObject.getNewProgressBar(this, "Logging In...",
				ProgressDialog.STYLE_SPINNER);
		loginAlertDialog = new AlertDialog.Builder(this);
	}
	/**
	 * 
	 */
	private void initializeViewBinding() {
		signUp = (TextView) findViewById(R.id.loginSign);
		logIn = (Button) findViewById(R.id.loginlogin);
		forgotPass = (TextView) findViewById(R.id.loginForgotPass);
		editUser = (EditText) findViewById(R.id.loginUser);
		editPass = (EditText) findViewById(R.id.loginPass);
	}

	/**
	 * 
	 */
	private void initializeUser() {
		userHandler = (UserService) ObjectFactory.instance().getInstance("UserHandler");
	}
	/**Called when the CameraActivity start.
	 * It starts after OnCreate is called or when the activity comes back from pause.
	 * It re-initial all the needed fields and configuration.  
	 */
	@Override
	protected void onStart() {
		super.onStart();
		signUp.setOnClickListener(this);
		logIn.setOnClickListener(this);
		forgotPass.setOnClickListener(this);
	}

	/**Called when back button pressed.
	 * Sets a dialog and asks if you really want to quit.
	 * If you quit it deletes all the videos recorded to the phone.
	 */
	@Override
	public void onBackPressed() {

		loginAlertDialog.setMessage("Are you sure you want to quit?");
		loginAlertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				//TODO: maybe change this to ask the user if he wants to deletes the file
				deleteRecordedFilesFromPhone();
				onDestroy();
				finish();
			}
		});
		loginAlertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		loginAlertDialog.show();
	}

	/**Called when some button is pressed*/
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.loginlogin:
			//login the user
			loginUser();

			break;
		case R.id.loginSign:
			startActivity(new Intent("com.security.signup"));
			break;

		case R.id.loginForgotPass:
			startActivity(new Intent("com.security.forgot_password"));
			break;
		}
	}

	/**Login the user to the application.
	 * Checks if the information is correct, and if it is,
	 * connects the user to the application
	 * @param userName
	 * @param password
	 */
	private void loginUser() {
		loginDialog.show();
		String userName = editUser.getText().toString();
		String password = editPass.getText().toString();
		LoginResult result  =  userHandler.logIn(userName, password);
		if(result.Result == LoginResultEnum.Parameter_Fail && 
				result.InputResult == InputParametersEnum.UsernameInvalid)
		{
			showErrorDialog("Illegal Username\n");
		}
		if(result.Result == LoginResultEnum.Parameter_Fail && 
				result.InputResult == InputParametersEnum.PasswordInvalid)
		{
			showErrorDialog("Illegal Password\n");
		}
		if(result.Result == LoginResultEnum.Parse_Fail)
		{
			showErrorDialog(result.Message + "\n");
		}
		else
		{
			startActivity(new Intent("com.security.FIRSTPAGE"));
			loginDialog.dismiss();
		}
	}
	
	/**
	 * 
	 * @param message
	 */
	private void showErrorDialog(String message)
	{
		loginDialog.dismiss();
		loginAlertDialog.setMessage(message
				+ "Please try again");
		loginAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int which) {
						dialog.cancel();
					}
				});
		loginAlertDialog.show();
	}
	
	
	/**Called when the activity resumes to run.*/
	@Override
	protected void onResume() {
		super.onResume();
		editPass.setText("");
		editUser.setText("");
	}

	/**Deletes all the recorded videos from the phone.
	 * All the data stored in the cloud so its
	 * Unnecessary to keep it on the phone.
	 */
	private void deleteRecordedFilesFromPhone() {
		new CacheCleanerTask().execute();
	}
}
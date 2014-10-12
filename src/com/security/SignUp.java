package com.security;


import com.security.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.dataobjects.ProgressObject;
import com.dataobjects.SignupResult;
import com.enums.InputParametersEnum;
import com.enums.SignupResultEnum;
import com.infrastructure.ObjectFactory;
import com.infrastructure.users.IUserService;

/**This class is responsible for signing a new user to the application
 * 
 * @extends Activity
 * @implements OnClickListener
 *
 */
public class SignUp extends Activity implements OnClickListener {
	/**Alert sign*/
	AlertDialog.Builder alertsign;
	/**Sign button*/
	Button sign;
	/**UserName, Password and email fields*/
	EditText usernameEdit, passwordEdit, emailEdit;
	/**progress bar*/
	ProgressDialog signUpDialog;

	IUserService userHandler = null;
	/**The "constructor" of SecurityCamActivity.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signup);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initializeViewBinding();
		initializeMembers();
	}

	/**
	 * 
	 */
	private void initializeMembers() {
		alertsign = new AlertDialog.Builder(this);
		signUpDialog = ProgressObject.getNewProgressBar(this, "Signing in...",
				ProgressDialog.STYLE_SPINNER);
		userHandler = (IUserService) ObjectFactory.instance().getInstance("UserHandler");
	}

	/**
	 * 
	 */
	private void initializeViewBinding() {
		sign = (Button) findViewById(R.id.signupSign);
		usernameEdit = (EditText) findViewById(R.id.signupUser);
		passwordEdit = (EditText) findViewById(R.id.signupPass);
		emailEdit = (EditText) findViewById(R.id.signupEmail);
	}

	/**Called when the CameraActivity start.
	 * It starts after OnCreate is called or when the activity comes back from pause.
	 * It re-initial all the needed fields and configuration.  
	 */
	@Override
	protected void onStart() {
		super.onStart();
		sign.setOnClickListener(this);
	}

	/**Called when the activity resumes to run.*/
	@Override
	protected void onResume() {
		super.onResume();
		resetViews();
	}

	/**
	 * 
	 */
	private void resetViews() {
		usernameEdit.setText("");
		passwordEdit.setText("");
		emailEdit.setText("");
	}

	/**Called when some button is pressed*/
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.signupSign:
				String username = usernameEdit.getText().toString();
				String password = passwordEdit.getText().toString();
				String email = emailEdit.getText().toString();
				signUp(username, password, email);
		}
	}

	/**
	 * @param username
	 * @param password
	 * @param email
	 */
	private void signUp(String username, String password, String email) {
		signUpDialog.show();
		if(userHandler != null)
		{
			SignupResult result =  userHandler.signUp(username, email, password);
			if(result.Result == SignupResultEnum.Parameter_Fail &&
					result.InputResult == InputParametersEnum.UsernameInvalid	)
			{
				onParameterIlegal("Illegal userName\n");
			}
			if(result.Result == SignupResultEnum.Parameter_Fail &&
					result.InputResult == InputParametersEnum.EmailInvalid	)
			{
				onParameterIlegal("Illegal e-mail\n");
			}
			if(result.Result == SignupResultEnum.Parameter_Fail &&
					result.InputResult == InputParametersEnum.PasswordInvalid	)
			{
				onParameterIlegal("Illegal password\n");
			}
			
			if(result.Result == SignupResultEnum.Parse_Fail)
			{
				onParameterIlegal(result.Message + "\n");
			}
			else {
				finish();
				signUpDialog.dismiss();
			}
		}
		else {
			finish();
			signUpDialog.dismiss();
		}

	}

	/**
	 * 
	 */
	private void onParameterIlegal(String message) {
		signUpDialog.dismiss();
		alertsign.setMessage(message + "Please try again");
		alertsign.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.cancel();

					}
				});
		alertsign.show();
	}
}

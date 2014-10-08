package com.security;

import com.security.R;
import NewObjects.ProgressObject;
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

import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
	//Strings that holds the UserName, Password and email
	String username, password, email;
	/**The user*/
	ParseUser user;
	/**progress bar*/
	ProgressDialog signUpDialog;

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
		sign = (Button) findViewById(R.id.signupSign);
		usernameEdit = (EditText) findViewById(R.id.signupUser);
		passwordEdit = (EditText) findViewById(R.id.signupPass);
		emailEdit = (EditText) findViewById(R.id.signupEmail);
		user = new ParseUser();
		alertsign = new AlertDialog.Builder(this);
		signUpDialog = ProgressObject.getNewProgressBar(this, "Signing in...",
				ProgressDialog.STYLE_SPINNER);
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
		username = "";
		password = "";
		email = "";
		usernameEdit.setText("");
		passwordEdit.setText("");
		emailEdit.setText("");
	}

	/**Called when some button is pressed*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signupSign:
			username = usernameEdit.getText().toString();
			password = passwordEdit.getText().toString();
			email = emailEdit.getText().toString();

			user.setUsername(username);
			user.setPassword(password);
			user.setEmail(email);

			if (username.length() == 0 || username == null) {//checks if the username is OK
				alertsign.setMessage("Illegal userName\n" + "Please try again");
				alertsign.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				alertsign.show();
			} else if (password.length() == 0 || password == null) {//checks if the password is OK 
				alertsign.setMessage("Illegal password\n" + "Please try again");
				alertsign.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				alertsign.show();
			} else if (email.length() == 0 || email == null) {//checks if the email is OK
				alertsign.setMessage("Illegal e-mail\n" + "Please try again");
				alertsign.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				alertsign.show();
			} else {
				signUpDialog.show();
				user.signUpInBackground(new SignUpCallback() {//signup to the application

					@Override
					public void done(com.parse.ParseException e) {
						if (e == null) {
							finish();
							signUpDialog.dismiss();
						} else {
							signUpDialog.dismiss();
							alertsign.setMessage(e.getMessage() + "\n"
									+ "Please try again");
							alertsign.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									});
							alertsign.show();
						}
					}
				});
			}
		}
	}
}

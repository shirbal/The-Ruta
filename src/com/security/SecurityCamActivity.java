package com.security;

import java.io.File;

import com.security.R;
import com.workers.DestroyThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dataobjects.ProgressObject;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

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
	String userName, password;
	/**The user*/
	ParseUser user;
	/**Dialog*/
	AlertDialog.Builder d;
	/**progress dialog*/
	ProgressDialog loginDialog;
	/**flag indicates if the phone was stolen*/
	static Boolean stolenFlag = false;

	/**The "constructor" of SecurityCamActivity.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "wQbQt0bXRdUK7iGQO5MpxvI2QuLukmU4r8BWnd5F",
				"MU5X5m6IbcAz4JncTaLpocATTgpkNQqsvfhvCTLJ");//initializes the parse account
		user = new ParseUser();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		d = new AlertDialog.Builder(this);
		signUp = (TextView) findViewById(R.id.loginSign);
		logIn = (Button) findViewById(R.id.loginlogin);
		forgotPass = (TextView) findViewById(R.id.loginForgotPass);
		editUser = (EditText) findViewById(R.id.loginUser);
		editPass = (EditText) findViewById(R.id.loginPass);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		loginDialog = ProgressObject.getNewProgressBar(this, "Logging In...",
				ProgressDialog.STYLE_SPINNER);
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

		d.setMessage("Are you sure you want to quit?");
		d.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				deleteRecordedFilesFromPhone();
				onDestroy();
				finish();
			}
		});
		d.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		d.show();
	}

	/**Called when some button is pressed*/
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.loginlogin:
			userName = editUser.getText().toString();
			password = editPass.getText().toString();
			//checks if the information received is OK
			if (userName.length() == 0 || password.length() == 0
					|| userName == null || password == null) {

				d.setMessage("Illegal Username or Password\n"
						+ "Please try again");
				d.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				d.show();
				break;
			}
			loginDialog.show();
			//login the user
			loginUser(userName, password);

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
	private void loginUser(String userName, String password) {
		ParseUser.logInInBackground(userName, password, new LogInCallback() {

			@Override
			public void done(ParseUser arg0, ParseException e) {
				if (arg0 != null) {
					startActivity(new Intent("com.security.FIRSTPAGE"));
					loginDialog.dismiss();
				} else {
					loginDialog.dismiss();
					d.setMessage(e.getMessage() + "\n" + "Please signup");
					d.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					d.show();
				}
			}
		});
	}
	/**Called when the activity resumes to run.*/
	@Override
	protected void onResume() {
		super.onResume();
		stolenFlag = false;
		userName = "";
		password = "";
		editPass.setText("");
		editUser.setText("");
	}

	/**Deletes all the recorded videos from the phone.
	 * All the data stored in the cloud so its
	 * Unnecessary to keep it on the phone.
	 */
	private void deleteRecordedFilesFromPhone() {
		String str = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getPath()
				+ File.separator + "CameraSecurity";
		new DestroyThread().execute(str);
	}
}
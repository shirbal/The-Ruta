package com.security;

import com.security.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**The ForgotPassword activity is responsible for getting a new password
 * when you forgot yours
 * @extends Activity
 * @implements OnClickListener
 *
 */
public class ForgotPassword extends Activity implements OnClickListener {

	/**Forgot Password button*/
	Button forgot;
	/**Email input field*/
	EditText emailEdit;
	/**Email*/
	String email;
	/**Alert sign*/
	AlertDialog.Builder alertsign;
	
	/**The "constructor" of ForgotPassword.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forgot_password);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		forgot = (Button) findViewById(R.id.resetPass);
		emailEdit = (EditText) findViewById(R.id.email);
		alertsign = new AlertDialog.Builder(this);
	}

	/**Called when the ForgotPassword start.
	 * It starts after OnCreate is called or when the activity comes back from pause.
	 * It re-initial all the needed fields and configuration.  
	 */
	@Override
	protected void onStart() {
		super.onStart();
		forgot.setOnClickListener(this);
	}

	/**Called when ForgotPassword button is pressed
	 * Checks the email, and if its valid, sends an email
	 * to the users with instructions on how to reset the password.
	 * @param view
	 */
	public void onClick(View v) {
		email = emailEdit.getText().toString();//gets the email from the field
		if (email == null || email.length() == 0) {
			alertsign.setMessage("Illegal e-mail\n" + "Please try again");
			alertsign.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();

						}
					});
			alertsign.show();
		} else {
			ParseUser.requestPasswordResetInBackground(email,
					new RequestPasswordResetCallback() {

						@Override
						public void done(com.parse.ParseException e) {
							if (e == null) {
								finish();
							} else {
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

package com.security;

import com.security.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.ParseUser;

/**This class is responsible for the first tab displayed after login.
 * Its shows you information and gives you the option to signout.
 * @extends Activity
 * @implements OnClickListener
 *
 */
public class WellcomeTab extends Activity implements OnClickListener {
	/**Alert dialog*/
	AlertDialog.Builder d;
	/**Signout button*/
	Button signout;
	/**The user*/
	ParseUser currentUser;

	/**The "constructor" of SecurityCamActivity.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hometab);
		signout = (Button) findViewById(R.id.welcomeSignOut);
		d = new AlertDialog.Builder(this);

	}

	/**Called when the CameraActivity start.
	 * It starts after OnCreate is called or when the activity comes back from pause.
	 * It re-initial all the needed fields and configuration.  
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if(SecurityCamActivity.stolenFlag) {
			finish();
		}
		signout.setOnClickListener( this);
		currentUser = ParseUser.getCurrentUser();
		
	}
	
	/**Called when the activity resumes to run.*/
	@Override
	protected void onResume() {
		super.onResume();
		if(SecurityCamActivity.stolenFlag) {
			finish();
		}
	}

	/**Called when some button is pressed*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.welcomeSignOut:
			d.setMessage("Are you sure you want to SignOut?");
			d.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					currentUser.deleteInBackground();//signout the user from the application
				
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

	}

	/**Called when back button pressed
	 * If the Stolen flag is on, then the phone is stolen and the application should logout and exit.
	 * 
	 */
	@SuppressWarnings("static-access")
	@Override
	public void onBackPressed() {
		if(SecurityCamActivity.stolenFlag) {
			currentUser.logOut();
			finish();
			return;
		}
		d.setMessage("Are you sure you want to quit?");
		d.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				currentUser.logOut();//logout the user
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
	


}

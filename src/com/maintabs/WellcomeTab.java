package com.maintabs;

import com.security.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.infrastructure.ObjectFactory;
import com.infrastructure.users.IUserService;
import com.infrastructure.users.UserService;

/**This class is responsible for the first tab displayed after login.
 * Its shows you information and gives you the option to signout.
 * @extends Activity
 * @implements OnClickListener
 *
 */
public class WellcomeTab extends Activity implements OnClickListener {
	/**Alert dialog*/
	AlertDialog.Builder mAlertDialog;
	/**Signout button*/
	Button signout;
	
	IUserService mUserHandler = null;

	/**The "constructor" of SecurityCamActivity.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hometab);
		signout = (Button) findViewById(R.id.welcomeSignOut);
		mAlertDialog = new AlertDialog.Builder(this);
		mUserHandler = (UserService) ObjectFactory.instance().getInstance("UserHandler");

	}

	/**Called when the CameraActivity start.
	 * It starts after OnCreate is called or when the activity comes back from pause.
	 * It re-initial all the needed fields and configuration.  
	 */
	@Override
	protected void onStart() {
		super.onStart();
		signout.setOnClickListener( this);
		
	}
	
	/**Called when the activity resumes to run.*/
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		boolean isStolen = data.getBooleanExtra("IsStolen", false);
		if(isStolen)
		{
			this.setResult(0, new Intent().putExtra("IsStolen",false ));
			finish();
		}
	}

	/**Called when some button is pressed*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.welcomeSignOut:
			mAlertDialog.setMessage("Are you sure you want to SignOut?");
			mAlertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					backToLoginScreen();
				}
			});
			mAlertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			mAlertDialog.show();
		}

	}

	/**Called when back button pressed
	 * If the Stolen flag is on, then the phone is stolen and the application should logout and exit.
	 * 
	 */
	@Override
	public void onBackPressed() {
		mAlertDialog.setMessage("Are you sure you want to quit?");
		mAlertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				backToLoginScreen();
			}
		});
		mAlertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		mAlertDialog.show();

	}
	/**
	 * 
	 */
	private void backToLoginScreen() {
		if(mUserHandler != null)
		{
			mUserHandler.logOut();	
		}
		finish();
	}

}

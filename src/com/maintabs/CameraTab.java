package com.maintabs;

import com.security.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**Responsible for the Camera tab
 * It starts the video recorder
 * @extends Activity
 * @implements OnClickListener
 */
public class CameraTab extends Activity implements OnClickListener {
	/**Open button*/
	Button open;
	/**Static String*/
	private final String text = "Press on open to move \n  to surveillance screen \n"
			+ "and than press start    \n    to activate the \n surveillance camera";
	/**TextView*/
	private TextView instruction;

	/**The "constructor" of CameraTab.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameratab);
		open = (Button) findViewById(R.id.openCamera);
		instruction = (TextView) findViewById(R.id.cameraTabMessage);
		open.setOnClickListener(this);
		instruction.setText(text); //show the text on the screen
	}

	/**Called when open button pressed.
	 * Start the activity for recording.
	 * @param view 
	 */
	public void onClick(View v) {
		startActivity(new Intent("android.intent.action.Camera"));
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

}

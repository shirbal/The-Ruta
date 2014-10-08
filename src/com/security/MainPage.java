package com.security;

import com.security.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

/**
 * This Class activity is the main activity of the application
 * It creates the three tabs: Home tab, Camera tab and Watch tab
 * The Home tab is the "First Page" which give a little explanation of how to use the camera 
 * The Camera tab is for the @CameraActivity
 * The Watch tab is for the video manager where the user can watch and manage his recorded videos (@WatchTab)
 *@extends @TabActivity
 */
public class MainPage extends TabActivity {
	
	/** Reusable Intent for each tab */
	Intent intent; 
	/** The tab host of the page */
	TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.firstpage);
		//gets the host tab from layout
		tabHost= getTabHost();
	    TabHost.TabSpec spec;

	    //sets the first tab - The home tab
		spec = tabHost.newTabSpec("HomeTab");
		intent = new Intent().setClass(this, WellcomeTab.class);
		spec.setContent(intent);
		spec.setIndicator("Home"); // title for the tab
		tabHost.addTab(spec);

	    //sets the second tab - The camera tab
		spec = tabHost.newTabSpec("Camera");
		intent = new Intent().setClass(this, CameraTab.class);
		spec.setContent(intent);
		spec.setIndicator("Camera"); // title for the tab
		tabHost.addTab(spec);

	    //sets the third tab - The watch tab
		spec = tabHost.newTabSpec("WatchTab");
		intent = new Intent().setClass(this, WatchTab.class);
		spec.setContent(intent);
		spec.setIndicator("Manager"); // title for the tab
		tabHost.addTab(spec);

		//sets the first tab to be open to home tab
		tabHost.setCurrentTab(0);
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		tabHost.setCurrentTab(0);
	}
}

package com.security;

import java.io.File;
import java.io.FileOutputStream;
//import java.util.List;

import com.security.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
//import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.VideoView;

import com.dataobjects.ProgressObject;
//import com.parse.FindCallback;
//import com.parse.GetDataCallback;
//import com.parse.ParseException;
//import com.parse.ParseFile;
import com.parse.ParseObject;
//import com.parse.ParseQuery;
import com.parse.ParseUser;
//import com.parse.ProgressCallback;


/**
 * This activity class is a basically a video manager
 * It use a video player for the user to watch the recorded video from the cloud
 * The user can manage all his uploaded files.
 * He can watch, download to phone old video or delete video from cloud.
 *@extends @Activity
 *@implements @OnClickListener
 */
public class WatchTab extends Activity implements OnClickListener {
	/**Progress bar*/
	ProgressDialog listProgress;
	/**Stop, sort by Date and sort by Name buttons*/
	Button stopButton, byDate, byName;
	/**Video view*/
	VideoView videoView;
	/**The parse objects*/
	ParseObject parseObjectHelper;
	/**The url to the recorded video*/
	String url;
	/**The user*/
	ParseUser currentUserSort;
	/**The text view for the video displayed*/
	TextView currentlyDisplayed;
	/**Watch layout*/
	LinearLayout watchLayout;

	/**The "constructor" of SecurityCamActivity.
	 * Runs the first time the activity is called.
	 * Used for initialization.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watchertab);
		initialAllViews();
		currentUserSort = ParseUser.getCurrentUser();
		createListProgress();
	}

	/**Called when the CameraActivity start.
	 * It starts after OnCreate is called or when the activity comes back from pause.
	 * It re-initial all the needed fields and configuration.  
	 */
	@Override
	protected void onStart() {
		super.onStart();
		stopButton.setOnClickListener(this);
		byName.setOnClickListener(this);
		byDate.setOnClickListener(this);
	}

	/**Called when a back button was pressed*/
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		videoView.pause();
		deleteAllViewFromLayouts();
		finish();
		startActivity(new Intent("com.security.FIRSTPAGE"));
	}

	/**Called when the activity resumes to run.*/
	@Override
	protected void onResume() {
		super.onResume();
		refreshVideoListView();
	}

	/**Called when some button is pressed*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.WatchTabStopButton:
			if (videoView.isPlaying()) {
				videoView.pause();
				stopButton.setText("Resume");
				currentlyDisplayed.setVisibility(View.INVISIBLE);
			} else {
				videoView.start();
				stopButton.setText("Pause");
				currentlyDisplayed.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.WatchTabSortByNameButton:
			deleteAllViewFromLayouts();
			createTextList("fileName");
			break;

		case R.id.WatchTabSortByDateButton:
			deleteAllViewFromLayouts();
			createTextList("createdAt");
		}
	}

	/**
	 * Private helper Function: builds the list of all videos from cloud
	 * The format of the list is for example:
	 * " 1. fileName location
	 * 		Play Download Delete "
	 *  for each line it create five @TextView elements
	 *  It gets all the information from the cloud using query. 
	 * The play, Download and Delete @TextView are clickable
	 */
	private void createTextList(String param) {
//		ParseQuery querySort = new ParseQuery("Files");
//		querySort.whereEqualTo("email", currentUserSort.getEmail());
//		querySort.orderByAscending(param);	
		//find all the specific user videos
//		querySort.findInBackground(new FindCallback() {
//
//			@Override
//			public void done(List<ParseObject> files, ParseException arg1) {			
//				//if the user has no videos on cloud it creates
//				//a special TextView to display a message on screen
//				if (files.size() == 0) {
//					LinearLayout.LayoutParams linearParams = new LayoutParams(
//							LinearLayout.LayoutParams.WRAP_CONTENT,
//							LinearLayout.LayoutParams.WRAP_CONTENT);
//					linearParams.gravity = Gravity.CENTER;
//					final TextView noVideoText = new TextView(WatchTab.this);
//					noVideoText.setText("No videos on cloud");
//					noVideoText.setTextSize(20);
//					noVideoText.setPadding(30, 0, 0, 0);
//					noVideoText.setTextColor(Color.rgb(32, 32, 32));
//					noVideoText.setLayoutParams(linearParams);
//					watchLayout.addView(noVideoText);
//					return;
//				}
//				//the i will represent the line number
//				int i = 1;
//				//create layout parameters
//				LinearLayout.LayoutParams linearParams = new LayoutParams(
//						LinearLayout.LayoutParams.MATCH_PARENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//
//				//iterating over all returned objects from the cloud
//				for (ParseObject parseObject : files) {
//					if (parseObject.getString("fileName").equals("log.txt")) {
//						continue;
//					}
//					final ParseFile videoToPlay = (ParseFile) parseObject
//							.get("file");
//
//					//for each line it create a differnt layout
//					//each line is divided to tree lines
//					//first line is: " 1. filename location "
//					//second line is: "Play Download Delete"
//					//third line is: " ---------" just a seperator between lines
//					LinearLayout firstLine = new LinearLayout(WatchTab.this);
//					firstLine.setOrientation(LinearLayout.HORIZONTAL);
//					firstLine.setPadding(0, 0, 3, 0);
//					firstLine.setLayoutParams(linearParams);
//
//					LinearLayout secondLine = new LinearLayout(WatchTab.this);
//					secondLine.setOrientation(LinearLayout.HORIZONTAL);
//					secondLine.setLayoutParams(linearParams);
//
//					LinearLayout thirdLine = new LinearLayout(WatchTab.this);
//					thirdLine.setOrientation(LinearLayout.HORIZONTAL);
//					thirdLine.setLayoutParams(linearParams);
//
//					//From here defining the first row
//					
//					// create the name row - This is the name of the video
//					final TextView nameTextView = new TextView(WatchTab.this);
//					nameTextView.setText(""
//							+ i
//							+ ". "
//							+ createFinalName(parseObject.get("fileName")
//									.toString()));
//					nameTextView.setTextSize(20);
//					nameTextView.setPadding(0, 0, 6, 7);
//					nameTextView.setTextColor(Color.rgb(32, 32, 32));
//
//					// create the Location row - This is the GPS location
//					TextView locationText = new TextView(WatchTab.this);
//					String locationTemp = new String();
//					locationTemp = parseObject.getString("Gps");
//					if (locationTemp == null) {
//						locationText.setText("No location");
//					} else {
//						locationText.setText(locationTemp);
//					}
//					locationText.setTextSize(20);
//					locationText.setPadding(0, 0, 0, 5);
//					locationText.setTextColor(Color.rgb(32, 32, 32));
//
//					firstLine.addView(nameTextView, 0);
//					firstLine.addView(locationText, 1);
//
//					//From here defining the second row
//					
//					// create the play button row - the play will be clickable
//					TextView playText = new TextView(WatchTab.this);
//					playText.setText("Play");
//					playText.setTextSize(20);
//					playText.setPadding(5, 0, 15, 4);
//					playText.setTextColor(Color.rgb(32, 32, 32));
//					playText.setClickable(true);
//
//					// create the Download button row - the Download will be clickable
//					TextView downloadText = new TextView(WatchTab.this);
//					downloadText.setText("Download");
//					downloadText.setTextSize(20);
//					downloadText.setPadding(0, 0, 15, 4);
//					downloadText.setTextColor(Color.rgb(32, 32, 32));
//					downloadText.setClickable(true);
//
//					// create the delete button row - the delete will be clickable
//					TextView deleteText = new TextView(WatchTab.this);
//					deleteText.setText("Delete");
//					deleteText.setTextSize(20);
//					deleteText.setPadding(0, 0, 15, 4);
//					deleteText.setTextColor(Color.rgb(32, 32, 32));
//					deleteText.setClickable(true);
//
//					//from this part of the method it will define for each text view
//					// (play, download and delete) its lister
//					
//					// click listener for playing the video
//					//it returns only the url to watch the video from the server
//					//is not downloading the video to the phone
//					playText.setOnClickListener(new OnClickListener() {
//						public void onClick(View v) {
//							final ProgressDialog playDialog = ProgressObject
//									.getNewProgressBar(WatchTab.this,
//											"Loading video...",
//											ProgressDialog.STYLE_SPINNER);
//							playDialog.show();
//							url = videoToPlay.getUrl();
//							Uri uri = Uri.parse(url);
//							if (videoView.isPlaying()) {
//								videoView.stopPlayback();
//							}
//							videoView.setVideoURI(uri);
//							videoView.start();
//							stopButton.setText("Pause");
//							stopButton.setVisibility(Button.VISIBLE);
//							currentlyDisplayed.setText(nameTextView.getText());
//							currentlyDisplayed.setVisibility(View.VISIBLE);
//							Thread newt = new Thread(new Runnable() {
//								public void run() {
//									while (!videoView.isPlaying()) {
//									}
//									try {
//										Thread.sleep(500);
//									} catch (InterruptedException e) {
//									}
//									playDialog.dismiss();
//								}
//							});
//							newt.start();
//						}
//					});
//
//					// click listener for downloading the video
//					//pressing the download button will download the video the phone
//					downloadText.setOnClickListener(new OnClickListener() {
//						public void onClick(View v) {
//							final ProgressDialog downloadDialog = ProgressObject
//									.getNewProgressBar(WatchTab.this,
//											"Downloading...",
//											ProgressDialog.STYLE_HORIZONTAL);
//							downloadDialog.show();
//							videoToPlay.getDataInBackground(
//									new GetDataCallback() {
//										@Override
//										public void done(byte[] video,
//												ParseException arg1) {
//											if(arg1 == null) {
//												saveVideoToPhone(video,
//														videoToPlay.getName());
//											}
//											downloadDialog.dismiss();
//										}
//									}, new ProgressCallback() {
//										@Override
//										public void done(Integer progress) {
//											downloadDialog
//													.setProgress(progress);
//										}
//									});
//						}
//					});
					
//					// click listener for deleteing the video
//					deleteText.setOnClickListener(new OnClickListener() {
//						public void onClick(View arg0) {
//							final ProgressDialog deleteDialog = ProgressObject
//									.getNewProgressBar(WatchTab.this,
//											"Deleting...",
//											ProgressDialog.STYLE_SPINNER);
//							deleteDialog.show();
//							ParseQuery query = new ParseQuery("Files");
//							String otherTemp = (String) nameTextView.getText();
//							otherTemp = otherTemp.substring(3);
//							final String temp = "VID_" + otherTemp + ".mp4";
//							query.whereEqualTo("email",
//									currentUserSort.getEmail());
//							query.findInBackground(new FindCallback() {
//								@Override
//								public void done(List<ParseObject> arg,
//										ParseException arg1) {
//									// search for the requested object to delete
//									for (ParseObject Object : arg) {
//										int result = Object.getString(
//												"fileName").compareTo(temp);
//
//										if (result == 0) {
//											// delete all current video from
//											// view and refresh the table;
//											try {
//												Object.delete();
//												deleteDialog.dismiss();
//											} catch (ParseException e) {
//												deleteDialog.dismiss();
//											}
//											refreshVideoListView();
//										} 
//									}
//								}
//							});
//						}
//					});
//
//					secondLine.addView(playText, 0);
//					secondLine.addView(downloadText, 1);
//					secondLine.addView(deleteText, 2);
//
//					//From here defining the third row
//					
//					TextView lineSeperator = new TextView(WatchTab.this);
//					lineSeperator.setText("----------------------------");
//					lineSeperator.setTextSize(20);
//					lineSeperator.setPadding(0, 0, 0, 4);
//					lineSeperator.setTextColor(Color.rgb(32, 32, 32));
//
//					thirdLine.addView(lineSeperator, 0);
//
//					watchLayout.addView(firstLine);
//					watchLayout.addView(secondLine);
//					watchLayout.addView(thirdLine);
//					i++;
//				}
//			}
		//});
	}

	/**Private helper Function to return video name without file type .mp4
	 * 
	 * @param video name
	 * @return video name without filetype
	 */
//	private StringBuilder getStringWithoutMp4(String str) {
//		StringBuilder newStr = new StringBuilder();
//		newStr.append(str.substring(0, 19));
//		return newStr;
//	}

	/**Private helper Function to return video name without VID prefix
	 * 
	 * @param video name
	 * @return video name without VID prefix
	 */
//	private String getStringWithoutVID(StringBuilder str) {
//		StringBuilder newStr = new StringBuilder();
//		newStr.append(str.substring(4));
//		return new String(newStr.toString());
//	}

	/**Private helper Function to return video name without VID prefix and .mp4
	 * suffix
	 * @param video name
	 * @return video name without VID prefix of file type
	 */
//	private String createFinalName(String str) {
//		return new String(getStringWithoutVID(getStringWithoutMp4(str)));
//	}

	/**Private helper Function to delete all view from all layouts*/
	private void deleteAllViewFromLayouts() {
		watchLayout.removeAllViewsInLayout();
		watchLayout.removeAllViews();
	}

	/**Private helper function: initial all button, layouts*/
	private void initialAllViews() {
		stopButton = (Button) findViewById(R.id.WatchTabStopButton);
		byName = (Button) findViewById(R.id.WatchTabSortByNameButton);
		byDate = (Button) findViewById(R.id.WatchTabSortByDateButton);
		videoView = (VideoView) findViewById(R.id.videoWatch);
		stopButton.setVisibility(Button.INVISIBLE);
		currentlyDisplayed = (TextView) findViewById(R.id.watchTabCurrentlydisplayed);
		watchLayout = (LinearLayout) findViewById(R.id.watcherLayout);
	}

	/**Refreshes the video list*/
	private void refreshVideoListView() {
		listProgress.show();
		deleteAllViewFromLayouts();
		createTextList("fileName");
		listProgress.dismiss();
	}

	/**Saves the video recorded to the phone
	 * 
	 * @param downloadedVideo
	 * @param fileNameToDownload
	 */
	public void saveVideoToPhone(byte[] downloadedVideo,
			String fileNameToDownload) {

		String root = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).toString();
		File myDir = new File(root + "/Downloaded Videos");
		if (!myDir.exists()) {
			myDir.mkdirs();
		}

		File file = new File(myDir, fileNameToDownload);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(downloadedVideo);
			out.close();

		} catch (Exception e) {
			return;
		}
	}

	/**Creates a progress bar*/
	private void createListProgress() {
		listProgress = ProgressObject.getNewProgressBar(WatchTab.this, "Loading...",
				ProgressDialog.STYLE_SPINNER);
	}

}

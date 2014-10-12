package com.hardwarehandlers;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;

public class AudioHandler {
	
	private static int MUTE_MODE = 0;
	private static int UNMUTE_MODE = 2;
	
	/**
	 * audio management for disabling the phone from ringing while on security
	 * mode
	 */
	private AudioManager mAudiomanager;
	
	private Activity mCurrentActivity = null;
	
	public AudioHandler(Activity activity)
	{
		mCurrentActivity = activity;
		init();
	}
	
	public void unmute()
	{
		// enabling the ringer back
		mAudiomanager.setRingerMode(UNMUTE_MODE);
	}
	
	public void mute()
	{
		mAudiomanager.setRingerMode(MUTE_MODE);
	}
	
	private void init()
	{
		// initial audio manager
		mAudiomanager = (AudioManager) mCurrentActivity.getSystemService(Context.AUDIO_SERVICE);
	}
}

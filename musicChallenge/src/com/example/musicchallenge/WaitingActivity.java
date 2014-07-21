package com.example.musicchallenge;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;



public class WaitingActivity extends SherlockActivity {
	
	// Context to use in OnClickListener View 
	private Context mContext;
	// SharedPreference file name
	public static final String PREFS_NAME = "MyPrefsFile";
	public boolean played = false;
	
	public MediaPlayer mPlayer = new MediaPlayer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting);
		
		mContext = this;
		

	}
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(mContext, GameActivity.class);
        mContext.startActivity(i);
	}

}

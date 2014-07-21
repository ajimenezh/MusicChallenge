package com.example.musicchallenge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * 
 * Class to listen to the song and guess the answer.
 * Once you listen to the song for the first time, you cannott
 * listen to it again.
 *
 */
public class ListenerActivity extends SherlockActivity {
	
	// Context to use in OnClickListener View 
	private Context mContext;
	// SharedPreference file name
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//True if the song has already been played
	private boolean played = false;
	
	//Phone number.
	private String phone;
	
	private MediaPlayer mPlayer = new MediaPlayer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_listener);
		
		mContext = this;
		
		final ImageView play = (ImageView) findViewById(R.id.play_image);
		
		final SongItem obj = getIntent().getParcelableExtra("SongItem");
		phone = getIntent().getStringExtra("number");
		if (getIntent().getIntExtra("play", 0)==0) {
			//play.setImageDrawable(null);
			//played = true;
		}
		
		play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				

				play.setImageDrawable(null);
				
				//if (!played) {
					
					played = true;
					
					mPlayer.reset();
					
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					
					try {

						mPlayer.setDataSource(obj.getPreviewUrl());
						
						try {
							mPlayer.prepare();
							
							mPlayer.start();
						} catch (IllegalStateException e) {
						} catch (IOException e) {
						}
					} catch (IllegalArgumentException e) {
					} catch (SecurityException e) {
					} catch (IllegalStateException e) {
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				//}
				
			}
		});
		
		Button sendBtn = (Button) findViewById(R.id.send_btn);
		
		sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String guessTitle = ((EditText) findViewById(R.id.title_guess)).getText().toString();
				String guessArtist = ((EditText) findViewById(R.id.artist_guess)).getText().toString();
				
				Intent intent = new Intent(mContext, ResultsActivity.class);
            	intent.putExtra("SongItem", obj);
            	intent.putExtra("guessTitle", guessTitle);
            	intent.putExtra("guessArtist", guessArtist);
            	intent.putExtra("number", phone);
            	
            	startActivity(intent);
				
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(mContext, GameActivity.class);
        mContext.startActivity(i);
	}

}

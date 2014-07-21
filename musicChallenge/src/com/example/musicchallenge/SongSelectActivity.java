package com.example.musicchallenge;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



public class SongSelectActivity extends Activity {
	
	// Context to use in OnClickListener View 
	//private Context mContext;
	// SharedPreference file name
	public static final String PREFS_NAME = "MyPrefsFile";
	public boolean isPlaying = false;
	
	public MediaPlayer mPlayer = new MediaPlayer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_selected);
		
		//mContext = this;
		
		final SongItem obj= getIntent().getParcelableExtra("SongItem");
		
		TextView songTitle = (TextView) findViewById(R.id.SongTitle2);
		songTitle.setText(obj.getSongTitle());
		
		TextView album = (TextView) findViewById(R.id.Album2);
		album.setText(obj.getSongAlbum());
		
		TextView artist = (TextView) findViewById(R.id.Artist2);
		artist.setText(obj.getSongArtist());
		
		ImageView play = (ImageView) findViewById(R.id.play_image);
		
		play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!mPlayer.isPlaying()) {
					
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
					
					isPlaying = true;
				}
				
			}
		});

	}

}

package com.example.musicchallenge;

import java.net.UnknownHostException;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * 
 * Class for the results of the song title and artist guess.
 * Gives the answer for the query and the number of points gained.
 *
 */
public class ResultsActivity extends SherlockActivity {
	
	// Context to use in OnClickListener View 
	private Context mContext;
	// SharedPreference file name
	private static final String PREFS_NAME = "MyPrefsFile";
	
	//Number of points gained. 3 for the title and 2 for the artist.
	private Integer points;
	
	//Phone number of the opponent.
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		
		mContext = this;
		
		phone = getIntent().getStringExtra("number");
		
		points = 0;
		
		final SongItem obj= getIntent().getParcelableExtra("SongItem");
		
		String titleGuess = getIntent().getStringExtra("guessTitle");
		String artistGuess = getIntent().getStringExtra("guessArtist");
		
		String title = obj.getSongTitle();
		String artist = obj.getSongArtist();
		
		((TextView) findViewById(R.id.guessed_artist)).setText(artistGuess);
		((TextView) findViewById(R.id.guessed_title)).setText(titleGuess);
		
		((TextView) findViewById(R.id.correct_artist)).setText(artist);
		((TextView) findViewById(R.id.correct_title)).setText(title);
		
		if (isEqual(titleGuess, title)) {
			((TextView) findViewById(R.id.title_check)).setText("Correct!");
			points += 3;
		} else {
			((TextView) findViewById(R.id.title_check)).setText("Wrong!");
		}
		
		if (isEqual(artistGuess, artist)) {
			((TextView) findViewById(R.id.artist_check)).setText("Correct!");
			points += 2;
		} else {
			((TextView) findViewById(R.id.artist_check)).setText("Wrong!");
		}
		
		Button btn = (Button) findViewById(R.id.button1);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				(new SendMessageDBPoints()).execute(phone);
				
				Intent i=new Intent(mContext, GameActivity.class);
		        startActivity(i);
				
			}
		});

	}

	/**
	 * Checker function.
	 * Currently only checks if the strings are equal in lower case.
	 * @param guess Guess string.
	 * @param s Real answer.
	 * @return Boolean. True if the strings are equal. False if they are different.
	 */
	private boolean isEqual(String guess, String s) {
		
		return ((String)guess.toLowerCase(Locale.ENGLISH)).equals(s.toLowerCase(Locale.ENGLISH));

	}
	
	/**
	 * 
	 * Send information to the database with the points update.
	 *
	 */
	private class SendMessageDBPoints extends AsyncTask<String, Integer, Boolean> {
		 
	    @Override
	    protected Boolean doInBackground(String... params) {
	    	
	    	String number = params[0];

		    SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
    	    int size = prefs.getInt("array" + "_size", 0);
    	    SharedPreferences.Editor editor = prefs.edit(); 

    	    for (int i=0; i<size; i++) {
    	    	String contact = prefs.getString("arrayNumber" + "_" + i, "");
    	    	if (number.equals(contact)) {
    	    		points += prefs.getInt("arrayPointYou" + "_" + i, 0);
        		    editor.putInt("arrayTurn" + "_" + i, 1);  
        		    editor.putInt("arrayPointYou" + "_" + i, points);  
    	    	}
    	    }
		    editor.commit(); 
	 
		    		    
		    try {
				
				MongoClient mongoClient = new MongoClient( "lennon.mongohq.com" , 10065);
				
				DB db = mongoClient.getDB("app23468779");
				
				@SuppressWarnings("deprecation")
				boolean auth = db.authenticate("Alex", ("12345qwerty").toCharArray());
				
				if (auth) {
					DBCollection coll = db.getCollection("games");
							
					
					try {
						JSONObject obj = new JSONObject();
						
						obj.put("phone", phone);
						
						DBObject query = (DBObject) com.mongodb.util.JSON.parse(obj.toString());
						
						JSONObject element = new JSONObject();
						
						obj = new JSONObject();
						
						obj.put("points", points);
						
						element.put("updates", obj);
						
						obj.put("$push", element);
						
						DBObject update = (DBObject) com.mongodb.util.JSON.parse(obj.toString());
										
						coll.update(query, update, true, false);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	 
	        return true;
	    }
	 
	    @Override
	    protected void onProgressUpdate(Integer... values) {
	 
	    }
	 
	    @Override
	    protected void onPreExecute() {

	    }
	 
	    @Override
	    protected void onPostExecute(Boolean result) {

	    }
	 
	    @Override
	    protected void onCancelled() {

	    }
	}

}
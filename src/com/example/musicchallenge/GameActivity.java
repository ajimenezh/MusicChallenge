package com.example.musicchallenge;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.musicchallenge.contacts.Contact;
import com.example.musicchallenge.contacts.Contacts;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;


/** This class list all the games opened.
 * For each game it shows the opponent, the points of each
 * player and an option to delete this game.
 */
public class GameActivity extends SherlockListActivity {
	
	//Global contexts.
	private Context mContext;
	
	//Name of the preferences file.
	public static final String PREFS_NAME = "MyPrefsFile";
	
	// This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;
    
    //Array to store all the games.
    private ArrayList<Game> games = new ArrayList<Game>();
    
    //Hashmap from the phone number to the name store in the phone.
    private HashMap<String,String> nameOfPhone = new HashMap<String,String>();
    
    private BaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_layout);
		
		mContext = this;
		
		adapter = new GamesAdapter(this, games);
		
		ListView myListView = (ListView) findViewById(android.R.id.list);
		
		myListView.setAdapter(adapter);		
				
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// Call the database here to not block the UI.
		(new LoadListGames()).execute();

	}
	
	private class LoadListGames extends AsyncTask<Void, Void, Void> {

		   ProgressDialog dialog = new ProgressDialog(GameActivity.this);

		   @Override
		    protected void onPreExecute() {
		        dialog.setMessage("Loading...");
		        dialog.show();
		        
		        super.onPreExecute();
		    }

		   protected Void doInBackground(Void... args) {

		        
			    SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				String val = settings.getString("phoneNumber", "");
				String phoneNumber = val;
		                
		        ReadContacts();
		  
		        UpdateInfoFromDB(phoneNumber);
			    
		        ReadGameInformation();
				

			    runOnUiThread(new Runnable() {
			        @Override
			        public void run() {
			        	if (adapter!=null) adapter.notifyDataSetChanged();
			       }
			   });
			    
			   
		    return null;
		   }

		   protected void onPostExecute(Void result) {
		     // do UI work here
		     if(dialog != null && dialog.isShowing()){
		       dialog.dismiss();
		     }

		  }
		}

	@Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        
        return true;
	}
	
	/**
	 * Read all the information stored in SharedPreferences for the games.
	 */
	public void ReadGameInformation() {

		SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
	    int size = prefs.getInt("array" + "_size", 0);
	    games.clear();
	    for (int i=0; i<size; i++) {
	    	String contact = prefs.getString("array" + "_" + i, "");
	    	String numberContact = prefs.getString("arrayNumber" + "_" + i, "");  
	    	Integer pointsYou = prefs.getInt("arrayPointYou" + "_" + i, 0);  
	    	Integer pointsOther = prefs.getInt("arrayPointOther" + "_" + i, 0);
	    	Integer turn = prefs.getInt("arrayTurn" + "_" + i, 1);  
	    	Integer play = prefs.getInt("arrayPlay" + "_" + i, 0);  
	    	
	    	Game game = new Game(contact, numberContact, pointsYou, pointsOther, turn);
	    	game.setPlay(play);
	    	games.add(game);
	    }
		
	}

	/**
	 * Connect with the database and update all the information.
	 * @param phoneNumber 
	 * 
	 */
	public void UpdateInfoFromDB(String phoneNumber) {
		
		String val = phoneNumber;
		
	    try {
			MongoClient mongoClient = new MongoClient( "lennon.mongohq.com" , 10065);
			
			DB db = mongoClient.getDB("app23468779");
			
			@SuppressWarnings("deprecation")
			boolean auth = db.authenticate("Alex", ("12345qwerty").toCharArray());
			
			if (auth) {
				DBCollection coll = db.getCollection("games");
						
				
				try {
					JSONObject obj = new JSONObject();
					
					obj.put("phone", val);
					
					Log.w("PhoneNumber", val);
					
					DBObject query = (DBObject) com.mongodb.util.JSON.parse(obj.toString());
		
					DBCursor cursor = coll.find(query);
					
					try {
					   while(cursor.hasNext()) {
						   DBObject obj2 = cursor.next();
						   
						   BasicDBList newGamesDB = (BasicDBList) obj2.get("games");					 
						   
						   BasicDBList updatesDB = (BasicDBList) obj2.get("updates");
						
						   			
						   if (newGamesDB!=null) {
							   
							   JSONArray newGames = new JSONArray(newGamesDB.toString());
							   
							   for (int i=0; i<newGames.length(); i++) {
								   
								   insertGame(newGames.getString(i));
							   
							   }
						   }
						   
						   if (updatesDB!=null) {
							   
							   JSONArray updates = new JSONArray(updatesDB.toString());
							   
							   for (int i=0; i<updates.length(); i++) {
								   
								   updateGame((JSONObject)updates.get(i));
								   
							   }
						   }
						   
						   coll.remove(query);
					   }
					} finally {
					   cursor.close();
					}
					
				} catch (JSONException e) {
					
					Log.d("ERROR", "JSON Exception");
		
				}
			}		
			
		} catch (UnknownHostException e) {
			
			Log.d("ERROR", "Error updating information from database");
		}
	}

	/**
	 * Update game with information for one of the two possible turns.
	 * @param info Information to update.
	 */
	private void updateGame(JSONObject info) {

		   String phone2;
			try {
				phone2 = info.getString("phone");
				
			   SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
				
			   int size = prefs.getInt("array" + "_size", 0);
			   SharedPreferences.Editor editor = prefs.edit();  
			   
			   for (int j=0; j<size; j++) {
				   String contact = prefs.getString("arrayNumber" + "_" + j, "");

				   if (phone2.equals(contact)) {
					   
					   if (info.has("points")) {
						   editor.putInt("arrayTurn" + "_" + j, 2);  
						   editor.putInt("arrayPointOther" + "_" + j, info.getInt("points"));
						   editor.commit(); 
					   }
					   else {
						   editor.putInt("arrayTurn" + "_" + j, 0); 
						   editor.putString("arrayURL" + "_" + j, info.getString("previewUrl")); 
						   editor.putString("arrayTitle" + "_" + j, info.getString("title")); 
						   editor.putString("arrayArtist" + "_" + j, info.getString("artist")); 
						   editor.putInt("arrayPlay" + "_" + j, 1);
						   editor.commit(); 
					   }

				   }
				   
			   }
			} catch (JSONException e) {

				Log.d("Error", "JSON Exception in updateGame()");
			}
		
	}

	/**
	 * Add a new game into SharedPreferences.
	 * @param phoneNumber The phone  number of the opponent.
	 */
	private void insertGame(String phoneNumber) {


		   SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
			
		   int size = prefs.getInt("array" + "_size", 0);
		   SharedPreferences.Editor editor = prefs.edit();  
		   editor.putInt("array" +"_size", size+1);    
		   if (nameOfPhone.containsKey(phoneNumber)) {
			   editor.putString("array" + "_" + size, nameOfPhone.get(phoneNumber));  
		   }
		   else {
			   editor.putString("array" + "_" + size, phoneNumber); 
		   }
		   editor.putString("arrayNumber" + "_" + size, phoneNumber);  
		   editor.putInt("arrayPointYou" + "_" + size, 0);  
		   editor.putInt("arrayPointOther" + "_" + size, 0);
		   editor.putInt("arrayTurn" + "_" + size, 1);  
		   editor.commit(); 
		
	}

	/** Read all contacts to populate nameOfPhone HashMap.
	 * 
	 */
	public void ReadContacts() {
		
		Contacts contacts = new Contacts(mContext);
		
		ArrayList<Contact> contactsList = contacts.getContacts();
		
		for (int i=0; i<contactsList.size(); i++) {
			nameOfPhone.put(contactsList.get(i).getPhoneNumber(), contactsList.get(i).getName());
		}
		
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	      // Handle item selection
	      switch (item.getItemId()) {
	      case R.id.contacts:

	    	  Intent i=new Intent(mContext, ContactsList.class);
	          startActivity(i);
	    	  
	          return true;
	      }
	      return false;
	}
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
		
		Integer turn = games.get(position).getTurn();
		
		Log.w("hello", ""+turn);
		
		if (turn==1) {
			Intent i=new Intent(mContext, SearchSong.class);
			i.putExtra("number", games.get(position).getNumber());
			i.putExtra("play", games.get(position).getPlay());
	        startActivity(i);
		}
		else if (turn==2) {
			Intent i=new Intent(mContext, WaitingActivity.class);
	        startActivity(i);
		}
		else if (turn==0) {
			Intent i=new Intent(mContext, ListenerActivity.class);
			
			SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  

			String previewUrl = prefs.getString("arrayURL" + "_" + position, ""); 
			String artist = prefs.getString("arrayTitle" + "_" + position, ""); 
			String title = prefs.getString("arrayArtist" + "_" + position, ""); 

			
			SongItem song = new SongItem();
			
			song.setPreviewUrl(previewUrl);
			song.setSongArtist(artist);
			song.setSongTitle(title);
			
			i.putExtra("SongItem", song);
			i.putExtra("number", games.get(position).getNumber());
			
	        startActivity(i);
		}

	}
	
	@Override
	public void onBackPressed() {
		System.exit(0);
		//startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
	}

}




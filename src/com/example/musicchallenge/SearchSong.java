package com.example.musicchallenge;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class SearchSong extends SherlockActivity {
	
	private Context mContext;
	public static final String PREFS_NAME = "MyPrefsFile";
	
	private ListView list;
	private SongListAdapter adapter;
    
	private ArrayList<SongItem> songs = new ArrayList<SongItem>();
    
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_song);
		
		Intent intent = getIntent();
		phone = intent.getStringExtra("number");
		
		list=(ListView)findViewById(R.id.listView1);
		
		// Getting adapter by passing xml data ArrayList
        adapter=new SongListAdapter(this, songs);
        list.setAdapter(adapter);

 
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	
            	JSONObject song = new JSONObject();
            	
            	try {
					song.put("previewUrl", songs.get(position).getPreviewUrl());
	            	song.put("artist", songs.get(position).getSongArtist());
	            	song.put("title", songs.get(position).getSongTitle());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	(new updateDB()).execute(phone, song.toString());
            	
            	Intent intent = new Intent(mContext, WaitingActivity.class);
            	//intent.putExtra("SongItem", songs.get(position));
            	startActivity(intent);
 
            }
        });
		
		mContext = this;
				
		Button searchbtn = (Button) findViewById(R.id.search_button);
		
		searchbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				EditText search = (EditText) findViewById(R.id.autoCompleteTextView1);

				String query = search.getText().toString();
				
				InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
				
				search.setText("");
				
				(new SearchQuery()).execute(query);
				
			}
			
		});

	}
	
	private class SearchQuery extends AsyncTask<String, Void, Void> {

		   ProgressDialog dialog = new ProgressDialog(mContext);

		   @Override
		    protected void onPreExecute() {
		        dialog.setMessage("Loading...");
		        dialog.show();
		        
		        super.onPreExecute();
		    }

		   protected Void doInBackground(String... args) {

		        
				String query = args[0];
				
				for (int i=0; i<query.length(); i++) {
					if (query.charAt(i)==' ') {
						query = query.substring(0, i) + "%20" + query.substring(i+1, query.length()); 
					}
				}

				
				String url = "https://api.spotify.com/v1/search?type=track&query=" + query;
				
				JSONObject response = getJSONfromURL(url);
				
				if (response!=null) {

					if (response.has("tracks")) {
						
						try {
							songs.clear();
							
							JSONArray array = ((JSONObject) response.getJSONObject("tracks")).getJSONArray("items");
							
							for (int i=0; i<array.length(); i++) {
								
								JSONObject obj = array.getJSONObject(i);
								
								if (obj.getString("type").equals("track")) {
									
									SongItem song = new SongItem(obj);
									
									songs.add(song);
									
								}
								
							}
							
							
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
					}
					
				}

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

	
	private class updateDB extends AsyncTask<String, Integer, Boolean> {
		 
	    @Override
	    protected Boolean doInBackground(String... params) {
	    	
	    	String number = params[0];
	    	JSONObject song = new JSONObject();
	    	try {
				song = new JSONObject(params[1]);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
		    SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
    	    int size = prefs.getInt("array" + "_size", 0);
    	    SharedPreferences.Editor editor = prefs.edit(); 
    	    Log.w("hello", number);
    	    for (int i=0; i<size; i++) {
    	    	String contact = prefs.getString("arrayNumber" + "_" + i, "");
    	    	if (number.equals(contact)) {
        		    editor.putInt("arrayTurn" + "_" + i, 2);  
    	    	}
    	    }
		    editor.commit(); 
	 
		    
		    SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			String val = settings.getString("phoneNumber", "");
			
			try {
				song.put("phone", val);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    
		    try {
				MongoClient mongoClient = new MongoClient( "lennon.mongohq.com" , 10065);
				
				DB db = mongoClient.getDB("app23468779");
				
				@SuppressWarnings("deprecation")
				boolean auth = db.authenticate("Alex", ("12345qwerty").toCharArray());
				
				DBCollection coll = db.getCollection("games");
						
				if (auth) {
					try {
						JSONObject obj = new JSONObject();
						
						obj.put("phone", phone);
						
						DBObject query = (DBObject) com.mongodb.util.JSON.parse(obj.toString());
						
						JSONObject element = new JSONObject();
						
						obj = new JSONObject();
						
						element.put("updates", song);
						
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


	public static JSONObject getJSONfromURL(String url){
	     InputStream is = null;
	     String result = "";
	     JSONObject json = null;
	      try{
	         HttpClient httpclient = new DefaultHttpClient();
	         HttpGet httpget = new HttpGet(url);
	         HttpResponse response = httpclient.execute(httpget);
	         HttpEntity entity = response.getEntity();
	         is = entity.getContent();
	         
		      try{
		         BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		         StringBuilder sb = new StringBuilder();
		         String line = null;
		         while ((line = reader.readLine()) != null) {
		             sb.append(line + "\n");
		         }
		         is.close();
		         result=sb.toString();
		         
			     try{
			         json = new JSONObject(result);

			     }catch(JSONException e){
			    	 
			     }
		     } catch(Exception e){
		    	 
		     }
	     }catch(Exception e){
	    	 
	     }



	      return json;
	 }
	
	@Override
	public void onBackPressed() {
		Intent i=new Intent(mContext, GameActivity.class);
        mContext.startActivity(i);
	}
	
}
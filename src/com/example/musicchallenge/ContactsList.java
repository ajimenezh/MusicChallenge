package com.example.musicchallenge;
 
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockListActivity;
import com.example.musicchallenge.contacts.Contact;
import com.example.musicchallenge.contacts.Contacts;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 
public class ContactsList extends SherlockListActivity {

	// This is the Adapter being used to display the list's data
	SimpleCursorAdapter mAdapter;
	Context mContext;
	public static final String PREFS_NAME = "MyPrefsFile";
	
	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
	    ContactsContract.Data.DISPLAY_NAME};
	
	// This is the select criteria
	static final String SELECTION = "((" + 
	    ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
	    ContactsContract.Data.DISPLAY_NAME + " != '' ))";
	
	ArrayList<String> values = new ArrayList<String>();
	ArrayList<String> numbers = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		Contacts contacts = new Contacts(mContext);
		ArrayList<Contact> conctactsList = contacts.getContacts();
		
		for (int i=0; i<conctactsList.size(); i++) {
			String name = conctactsList.get(i).getName();
			String phoneNo = conctactsList.get(i).getPhoneNumber();
			values.add(name);
			if (phoneNo.charAt(0)=='+') {
           	 phoneNo = phoneNo.substring(3, phoneNo.length());
            }
            numbers.add(phoneNo);
		}
		
        
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        
        setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick (ListView l, View v, int position, long id) {
		
		Log.w("helo", ""+values.get(position));
		
		String number = (String)numbers.get(position);
    	String name = (String)values.get(position);
    	
    	(new SendMessageNewGame()).execute((String)numbers.get(position));
		
		Boolean exist = false;
		
		SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, 0);  
	    int size = prefs.getInt("array" + "_size", 0);
	    for (int i=0; i<size; i++) {
	    	String contact = prefs.getString("array" + "_" + i, "");
	    	if (contact.equals(name)) {
	    		exist = true;
	    		break;
	    	}
	    }
	    
	    if (!exist) {
		    SharedPreferences.Editor editor = prefs.edit();  
		    editor.putInt("array" +"_size", size+1);    
		    editor.putString("array" + "_" + size, name);  
		    editor.putString("arrayNumber" + "_" + size, number);  
		    editor.putInt("arrayPointYou" + "_" + size, 0);  
		    editor.putInt("arrayPointOther" + "_" + size, 0);
		    editor.putInt("arrayTurn" + "_" + size, 2);  
		    editor.commit(); 
			
			(new SendMessageNewGame()).execute((String)numbers.get(position));
	    }
	    Intent i=new Intent(mContext, GameActivity.class);
        startActivity(i);

	}
	
	/**
	 * 
	 * Send message of new game to the database. Later the other person will update 
	 * its app with this entry.
	 *
	 */
	private class SendMessageNewGame extends AsyncTask<String, Integer, Boolean> {
		 
	    @Override
	    protected Boolean doInBackground(String... params) {
	    	
	    	String number = params[0];
	 
		    
		    SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			String val = settings.getString("phoneNumber", "");
		    
		    try {				
				MongoClient mongoClient = new MongoClient( "lennon.mongohq.com" , 10065);
				
				DB db = mongoClient.getDB("app23468779");
				
				@SuppressWarnings("deprecation")
				boolean auth = db.authenticate("Alex", ("12345qwerty").toCharArray());
				
				DBCollection coll = db.getCollection("games");
						
				if (auth) {
					try {
						JSONObject obj = new JSONObject();
						
						obj.put("phone", number);
						
						DBObject query = (DBObject) com.mongodb.util.JSON.parse(obj.toString());
						
						JSONObject element = new JSONObject();
						
						obj = new JSONObject();
						
						element.put("games", (String) val);
						
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
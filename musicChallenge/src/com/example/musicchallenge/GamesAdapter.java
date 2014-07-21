package com.example.musicchallenge;

import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Custom adapter for the game list.
 *
 */
public class GamesAdapter extends BaseAdapter {


    private List<Game> games;
    private Context context;
    
	public static final String PREFS_NAME = "MyPrefsFile";


    public GamesAdapter(Context context, List<Game> games){
        this.games = games;
        this.context = context;
    }


    @Override
    public int getCount() {
        return games.size();
    }


    @Override
    public Object getItem(int position) {
        return games.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 100;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

    	View rootView = LayoutInflater.from(context).inflate(R.layout.games_item, parent, false);

    	TextView title = (TextView) rootView.findViewById(R.id.person);
    	
    	title.setText(games.get(position).getPerson());
    	
    	TextView points = (TextView) rootView.findViewById(R.id.yourpoints);
    	
    	points.setText("You: " + games.get(position).getYourPoints());
    	
    	points = (TextView) rootView.findViewById(R.id.otherpoints);
    	
    	points.setText("Opponent: " + games.get(position).getOpponentPoints());
    	
    	ImageView btn = (ImageView) rootView.findViewById(R.id.cancel_image);
    	
    	btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
        		
        		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);  
        	    int size = prefs.getInt("array" + "_size", 0);
        	    int cnt = 0;
        	    SharedPreferences.Editor editor = prefs.edit(); 
        	    for (int i=0; i<size; i++) {
        	    	String contact = prefs.getString("array" + "_" + i, "");
        	    	if (!contact.equals(games.get(position).getPerson())) {
        	    		editor.putString("array" + "_" + cnt, prefs.getString("array" + "_" + i, ""));  
            		    editor.putString("arrayNumber" + "_" + cnt, prefs.getString("arrayNumber" + "_" + i, ""));  
            		    editor.putInt("arrayPointYou" + "_" + cnt, prefs.getInt("arrayPointYou" + "_" + i, 0));  
            		    editor.putInt("arrayPointOther" + "_" + cnt, prefs.getInt("arrayPointOther" + "_" + i, 0));
            		    editor.putInt("arrayTurn" + "_" + cnt, prefs.getInt("arrayTurn" + "_" + i, 0));  
            		    cnt++;
        	    	}
        	    }
        	    editor.putInt("array" +"_size", cnt);   
    		    editor.commit(); 
        	    

        	    Intent i=new Intent(context, GameActivity.class);
                context.startActivity(i);
            	
            }
    	});
    	
       
        return rootView;
    }
}

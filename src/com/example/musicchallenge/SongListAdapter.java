package com.example.musicchallenge;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SongListAdapter extends BaseAdapter {


    private List<SongItem> songs;
    private Context context;


    public SongListAdapter(Context context, List<SongItem> songs){
        this.songs = songs;
        this.context = context;
    }


    @Override
    public int getCount() {
        return songs.size();
    }


    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 100;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	View rootView = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);

    	TextView title = (TextView) rootView.findViewById(R.id.songTitle);
    	
    	title.setText(songs.get(position).getSongTitle());
    	
    	TextView artist = (TextView) rootView.findViewById(R.id.songArtist);
    	
    	artist.setText(songs.get(position).getSongArtist() + " - " + songs.get(position).getSongAlbum());

       
        return rootView;
    }
}

package com.example.musicchallenge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;



public class SongItem implements Parcelable {
	
	private String songTitle;
	private String artist;
	private String album;
	private String albumCover;
	private String artistImage;
	private String previewUrl;
	
	public SongItem() {
		
	}
	
	public SongItem(JSONObject obj) {
		
		try {
			this.songTitle = obj.getString("name");
			JSONArray artists = (JSONArray)obj.getJSONArray("artists");
			this.artist = (String) ((JSONObject) artists.get(0)).get("name");
			this.album = (String) ((JSONObject)obj.getJSONObject("album")).get("name");

			//this.albumCover = ((JSONObject)obj.getJSONObject("album")).getString("cover");
			//this.artistImage = ((JSONObject)obj.getJSONObject("artist")).getString("picture");
			
			this.previewUrl = obj.getString("preview_url");
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getSongTitle() {
		return this.songTitle;
	}

	public String getSongAlbum() {
		return this.album;
	}
	
	public String getSongArtist() {
		return this.artist;
	}
	
	public void setSongTitle(String title) {
		this.songTitle = title;
	}
	
	public void setSongArtist(String artist) {
		this.artist = artist;
	}
	
	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}
	
	//parcel part
	public SongItem(Parcel in){
		String[] data= new String[6];
	 
		in.readStringArray(data);
		this.songTitle = data[0];
		this.artist = data[1];
		this.album = data[2];

		this.albumCover = data[3];
		this.artistImage = data[4];
		
		this.previewUrl = data[5];

	}
	@Override
	public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
	}
	 
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	// TODO Auto-generated method stub
	 
		dest.writeStringArray(new String[]{this.songTitle, this.artist, this.album, this.albumCover, this.artistImage, this.previewUrl});
	}
	 
	public static final Parcelable.Creator<SongItem> CREATOR= new Parcelable.Creator<SongItem>() {
	 
	@Override
	public SongItem createFromParcel(Parcel source) {
	// TODO Auto-generated method stub
	return new SongItem(source);  //using parcelable constructor
	}
	 
	@Override
	public SongItem[] newArray(int size) {
	// TODO Auto-generated method stub
	return new SongItem[size];
	}
	};

	public String getPreviewUrl() {
		return this.previewUrl;
	}
	
}
package com.example.musicchallenge;

public class Game {

    private String person;
    private String number;
    private Integer yourPoints;
    private Integer opponentPoints;
    private Integer turn;
    private String previewUrl;
    private Integer play;

    public Game(String person, String number, Integer yourPoints, 
    		Integer opponentPoints, Integer turn) {
    	this.person = person;
    	this.number = number;
    	this.yourPoints = yourPoints;
    	this.opponentPoints = opponentPoints; 
    	this.turn = turn;
    	this.previewUrl = "";
    	this.play = 0;
    }
    
    public Game() {
    }


    public String getPerson() {
    	return this.person;
    }
    
    public String getNumber() {
    	return this.number;
    }
    
    public Integer getYourPoints() {
    	return this.yourPoints;
    }
    
    public Integer getOpponentPoints() {
    	return this.opponentPoints;
    }
    
    public void setPreviewUrl(String previewUrl) {
    	this.previewUrl = previewUrl;
    	return;
    }
    
    public String getPreviewUrl(String previewUrl) {
    	return this.previewUrl;
    }
    
    public Integer getTurn() {
    	return this.turn;
    }

	public void setPlay(Integer play) {
		this.play = play;
		
	}

	public Integer getPlay() {
		return this.play;
	}

}

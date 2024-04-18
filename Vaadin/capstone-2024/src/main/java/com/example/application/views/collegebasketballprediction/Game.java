package com.example.application.views.collegebasketballprediction;

import java.time.LocalDate;

public class Game {

    private String homeTeam; // home team name
    private String awayTeam; // away team name
    private String homeScore; // home team score
    private String awayScore; // away team score
    private String homePercent; // home team win percent
    private String awayPercent; // away team win percent
    private LocalDate gameDate; // date of game
    private String conference; // home team conference name

    private String location;

    public Game (String homeTeam, String awayTeam, String homeScore, String awayScore,
                 String homePercent, String awayPercent, LocalDate gameDate, String conference) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homePercent = homePercent;
        this.awayPercent = awayPercent;
        this.gameDate = gameDate;
        this.conference = conference;
        this.location = location;
    }
    // Getter for played location
    public String getLocation() {
        return location;
    }

    // Getter for home team name
    public String getHomeTeam()
    {
        return this.homeTeam;
    }

    // Getter for away team name
    public String getAwayTeam()
    {
        return this.awayTeam;
    }

    // Getter for the "@" symbol to make life easier when displaying
    public String getAt()
    {
        return "@";
    }

    // Getter for home team and away team names
    public String getTeams()
    {
        return homeTeam + " @ " + awayTeam;
    }

    // Getter for final game Score
    public String getScores()
    {
        return homeScore + " - " + awayScore;
    }

    // Getter for win percents
    public String getPercents()
    {
        return homePercent + " - " + awayPercent;
    }

    // Getter for the gameDate
    public LocalDate getGameDate() {
        return gameDate;
    }

    // Getter for Conference
    public String getConference() {
        return conference;
    }
}
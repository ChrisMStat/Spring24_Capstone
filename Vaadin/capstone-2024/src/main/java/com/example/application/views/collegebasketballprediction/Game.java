package com.example.application.views.collegebasketballprediction;

import java.time.LocalDate;

public class Game {

    private String homeTeam;

    private String awayTeam;

    private String homeScore;

    private String awayScore;

    private String homePercent;

    private String awayPercent;

    private LocalDate gameDate;

    private String conference;

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
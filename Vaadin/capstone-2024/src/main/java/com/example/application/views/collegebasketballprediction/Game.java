package com.example.application.views.collegebasketballprediction;

public class Game {

    private String homeTeam;

    private String awayTeam;

    private String homeScore;

    private String awayScore;

    private String homePercent;

    private String awayPercent;

    public Game (String homeTeam, String awayTeam, String homeScore, String awayScore,
                 String homePercent, String awayPercent) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homePercent = homePercent;
        this.awayPercent = awayPercent;
    }

    public String getHomeTeam()
    {
        return this.homeTeam;
    }

    public String getAwayTeam()
    {
        return this.awayTeam;
    }

    public String getAt()
    {
        return "@";
    }

    public String getTeams()
    {
        return homeTeam + " @ " + awayTeam;
    }

    public String getScores()
    {
        return homeScore + " - " + awayScore;
    }

    public String getPercents()
    {
        return homePercent + " - " + awayPercent;
    }
}

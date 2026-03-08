package com.sportsmanager.sports.football;

public class FootballMatch {

    private FootballTeam homeTeam;
    private FootballTeam awayTeam;
    private int homeScore;
    private int awayScore;

    public FootballMatch(FootballTeam homeTeam, FootballTeam awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public void playMatch() {
        homeScore = (int)(Math.random()*5);
        awayScore = (int)(Math.random()*5);
    }

    public String getScore() {
        return homeScore + " - " + awayScore;
    }
}
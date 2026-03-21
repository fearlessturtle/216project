package com.sportsmanager.core;

public class TeamStanding {

    private Team team;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;

    public TeamStanding(Team team, int wins, int draws, int losses, int goalsFor, int goalsAgainst) {
        this.team = team;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
    }

    public Team getTeam() {
        return team;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getPoints(int pointsPerWin, int pointsPerDraw) {
        return wins * pointsPerWin + draws * pointsPerDraw;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }
}

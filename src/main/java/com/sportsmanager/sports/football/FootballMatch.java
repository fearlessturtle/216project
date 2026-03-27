package com.sportsmanager.sports.football;

import com.sportsmanager.core.*;
import java.util.*;

public class FootballMatch implements Match {

    private Team homeTeam;
    private Team awayTeam;
    private int homeScore;
    private int awayScore;
    private boolean completed;
    private List<MatchEvent> events;

    public FootballMatch(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
        this.completed = false;
        this.events = new ArrayList<>();
    }

    public void play() {
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int[] getScore() {
        return new int[]{homeScore, awayScore};
    }

    public List<MatchEvent> getMatchEvents() {
        return new ArrayList<>(events);
    }

    public void applyTacticChange(Team team, Tactic newTactic) {
    }

    public void substitutePlayer(Team team, Player playerOut, Player playerIn) {
    }

    public void addObserver(MatchObserver observer) {
    }

    public void setHomeScore(int score) {
        this.homeScore = score;
    }

    public void setAwayScore(int score) {
        this.awayScore = score;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

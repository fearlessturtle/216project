package com.sportsmanager.core;

import java.util.List;

public interface Match {
    void play();

    Team getHomeTeam();
    Team getAwayTeam();
    int getHomeScore();
    int getAwayScore();
    boolean isCompleted();

    // Return both scores as [homeScore, awayScore]
    int[] getScore();

    List<MatchEvent> getMatchEvents();
    void applyTacticChange(Team team, Tactic newTactic);
    void substitutePlayer(Team team, Player playerOut, Player playerIn);
    void addObserver(MatchObserver observer);
}
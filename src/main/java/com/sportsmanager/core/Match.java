package com.sportsmanager.core;

import java.util.List;

public interface Match {
    void play();

    com.sportsmanager.core.Team getHomeTeam();
    com.sportsmanager.core.Team getAwayTeam();
    int getHomeScore();
    int getAwayScore();
    boolean isCompleted();


    String getScore();
    List<MatchEvent> getMatchEvents();
    void applyTacticChange(Team team, Tactic newTactic);
    void substitutePlayer(Team team, Player playerOut, Player playerIn);
    void addObserver(MatchObserver observer);
}

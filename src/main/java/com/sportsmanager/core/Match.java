package com.sportsmanager.core;

public interface Match {
    void play();
    void simulatePeriod();
    int getPeriodCount();

    com.sportsmanager.core.Team getHomeTeam();
    com.sportsmanager.core.Team getAwayTeam();
    int getHomeScore();
    int getAwayScore();
    boolean isCompleted();
}

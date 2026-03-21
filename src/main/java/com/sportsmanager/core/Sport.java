package com.sportsmanager.core;

import java.util.List;

public interface Sport {

    String getSportName();

    void generateLeague();

    void simulateWeek();

    void playMatch(Match match);

    List<Team> getTeams();

    List<TeamStanding> getLeagueTable();

    int getCurrentWeek();

    boolean isSeasonOver();

    int getPeriodCount();

    List<String> getPositions();
}

package com.sportsmanager.core;

import java.util.List;

public interface League {

    List<Team> getTeams();

    List<Match> getFixtures();

    List<TeamStanding> getStandings();

    List<Match> getNextMatches();

    void generateFixture();

    void updateStandings(Match match);

    boolean isSeasonOver();

    List<Team> applyTiebreaker(List<Team> teams);
}

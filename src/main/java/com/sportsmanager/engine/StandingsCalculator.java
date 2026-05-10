package com.sportsmanager.engine;

import com.sportsmanager.core.League;
import com.sportsmanager.core.Match;
import com.sportsmanager.core.TeamStanding;

import java.util.ArrayList;
import java.util.List;


public class StandingsCalculator {
    public void calculate(League league) {
        if (league == null) {
            return;
        }

        for (Match match : league.getFixtures()) {
            if (match != null && match.isCompleted()) {
                league.updateStandings(match);
            }
        }
    }

    public void updateAfterMatch(League league, Match match) {
        if (league == null || match == null || !match.isCompleted()) {
            return;
        }
        league.updateStandings(match);
    }

    public List<TeamStanding> getSortedStandings(League league) {
        if (league == null) {
            return List.of();
        }

        return new ArrayList<>(league.getStandings());
    }

    public TeamStanding getWinner(League league) {
        if (league == null || !league.isSeasonOver()) return null;
        List<TeamStanding> sorted = getSortedStandings(league);
        return sorted.isEmpty() ? null : sorted.get(0);
    }

    public int calculatePoints(TeamStanding standing) {
        if (standing == null) {
            return 0;
        }
        return standing.getPoints(2, 1);
    }
}

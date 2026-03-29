package com.sportsmanager.engine;

import com.sportsmanager.core.League;
import com.sportsmanager.core.Match;
import com.sportsmanager.core.TeamStanding;

import java.util.ArrayList;
import java.util.List;


public class StandingsCalculator {
    public void calculate(League league) {
        for (Match match : league.getFixtures()) {
            if (match.isCompleted()) {
                league.updateStandings(match);
            }
        }
    }

    public void updateAfterMatch(League league, Match match) {
        if (match.isCompleted()) {
            league.updateStandings(match);
        }
    }

    public List<TeamStanding> getSortedStandings(League league) {
        List<TeamStanding> sorted = new ArrayList<>(league.getStandings());

        sorted.sort((s1, s2) -> {
            int p1 = s1.getPoints(2, 1);
            int p2 = s2.getPoints(2, 1);
            if (p1 != p2) {
                return Integer.compare(p2, p1);
            }

            if (s1.getGoalDifference() != s2.getGoalDifference()) {
                return Integer.compare(
                        s2.getGoalDifference(),
                        s1.getGoalDifference()
                );
            }

            return Integer.compare(s2.getGoalsFor(), s1.getGoalsFor());
        });

        return sorted;
    }

    public TeamStanding getWinner(League league) {
        if (!league.isSeasonOver()) return null;
        List<TeamStanding> sorted = getSortedStandings(league);
        return sorted.isEmpty() ? null : sorted.get(0);
    }

    public int calculatePoints(TeamStanding standing) {
        return standing.getPoints(2, 1);
    }
}
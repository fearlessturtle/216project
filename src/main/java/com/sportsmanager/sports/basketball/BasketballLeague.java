package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.*;
import com.sportsmanager.engine.FixtureGenerator;
import java.util.*;

public class BasketballLeague extends AbstractLeague {

    private static final int NUM_TEAMS = 10;

    @Override
    public void generateFixture() {
        if (teams.size() != NUM_TEAMS) {
            throw new IllegalArgumentException("Basketball league requires exactly " + NUM_TEAMS + " teams");
        }

        fixtures.clear();

        FixtureGenerator generator = new FixtureGenerator();
        for (Team[] pair : generator.generatePairs(teams)) {
            fixtures.add(new BasketballMatch(pair[0], pair[1]));
        }
    }

    @Override
    public List<Team> applyTiebreaker(List<Team> tiedTeams) {
        if (tiedTeams.size() <= 1) return tiedTeams;

        List<Team> result = new ArrayList<>(tiedTeams);

        result.sort((t1, t2) -> {
            TeamStanding s1 = standings.get(t1);
            TeamStanding s2 = standings.get(t2);
            int p1 = s1 != null ? s1.getPoints(2, 1) : 0;
            int p2 = s2 != null ? s2.getPoints(2, 1) : 0;
            if (p1 != p2) return Integer.compare(p2, p1);

            int h2h = compareHeadToHead(t1, t2);
            if (h2h != 0) return h2h;

            int gd1 = s1 != null ? s1.getGoalDifference() : 0;
            int gd2 = s2 != null ? s2.getGoalDifference() : 0;
            if (gd1 != gd2) return Integer.compare(gd2, gd1);

            long c1 = coinTossScore(t1);
            long c2 = coinTossScore(t2);
            if (c1 != c2) {
                return Long.compare(c2, c1);
            }

            String name1 = t1 != null && t1.getName() != null ? t1.getName() : "";
            String name2 = t2 != null && t2.getName() != null ? t2.getName() : "";
            return name1.compareToIgnoreCase(name2);
        });

        return result;
    }

    private int compareHeadToHead(Team a, Team b) {
        int scA = 0, scB = 0;
        for (Match match : fixtures) {
            if (!match.isCompleted()) continue;
            Team home = match.getHomeTeam();
            Team away = match.getAwayTeam();
            int hs = match.getHomeScore();
            int as = match.getAwayScore();

            if (home != null && away != null && home.equals(a) && away.equals(b)) {
                if (hs > as) scA += 2;
                else if (hs == as) { scA += 1; scB += 1; }
                else scB += 2;
            } else if (home != null && away != null && home.equals(b) && away.equals(a)) {
                if (hs > as) scB += 2;
                else if (hs == as) { scA += 1; scB += 1; }
                else scA += 2;
            }
        }
        return Integer.compare(scB, scA);
    }
}

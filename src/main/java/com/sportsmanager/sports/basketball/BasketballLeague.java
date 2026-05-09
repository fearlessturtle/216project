package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.*;
import java.util.*;

public class BasketballLeague extends AbstractLeague {

    private static final int NUM_TEAMS = 10;

    @Override
    public void generateFixture() {
        if (teams.size() != NUM_TEAMS) {
            throw new IllegalArgumentException("Basketball league requires exactly " + NUM_TEAMS + " teams");
        }

        fixtures.clear();

        List<Team> rot = new ArrayList<>(teams);
        Team fixed = rot.remove(0);

        for (int leg = 0; leg < 2; leg++) {
            for (int round = 0; round < 9; round++) {
                Team h = leg == 0 ? fixed : rot.get(0);
                Team a = leg == 0 ? rot.get(0) : fixed;
                fixtures.add(new BasketballMatch(h, a));

                for (int i = 0; i < rot.size() / 2; i++) {
                    Team t1 = rot.get(i + 1 < rot.size() ? i + 1 : i);
                    Team t2 = rot.get(rot.size() - 1 - i);
                    if (!t1.equals(t2)) {
                        if (leg == 0) {
                            fixtures.add(new BasketballMatch(t1, t2));
                        } else {
                            fixtures.add(new BasketballMatch(t2, t1));
                        }
                    }
                }

                rotateTeams(rot);
            }
        }
    }

    private void rotateTeams(List<Team> list) {
        list.add(1, list.remove(list.size() - 1));
    }

    @Override
    public List<Team> applyTiebreaker(List<Team> tiedTeams) {
        if (tiedTeams.size() <= 1) return tiedTeams;

        List<Team> result = new ArrayList<>(tiedTeams);
        Random rng = new Random();

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

            return rng.nextInt(2) == 0 ? -1 : 1;
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

            if (home.equals(a) && away.equals(b)) {
                if (hs > as) scA += 2;
                else if (hs == as) { scA += 1; scB += 1; }
                else scB += 2;
            } else if (home.equals(b) && away.equals(a)) {
                if (hs > as) scB += 2;
                else if (hs == as) { scA += 1; scB += 1; }
                else scA += 2;
            }
        }
        return Integer.compare(scB, scA);
    }
}

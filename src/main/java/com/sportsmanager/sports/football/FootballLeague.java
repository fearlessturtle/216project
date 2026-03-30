package com.sportsmanager.sports.football;

import com.sportsmanager.core.*;
import java.util.*;

public class FootballLeague extends AbstractLeague {

    private static final int NUM_TEAMS = 20;
    private static final int WEEKS_PER_SEASON = 38;

    @Override
    public void generateFixture() {
        List<Team> teams = new ArrayList<>(this.teams);
        if (teams.size() != NUM_TEAMS) {
            throw new IllegalArgumentException("Football league requires exactly " + NUM_TEAMS + " teams");
        }

        fixtures.clear();

        for (int half = 0; half < 2; half++) {
            for (int round = 0; round < NUM_TEAMS - 1; round++) {
                for (int i = 0; i < NUM_TEAMS / 2; i++) {
                    Team home = teams.get(i);
                    Team away = teams.get(NUM_TEAMS - 1 - i);

                    if (!home.equals(away)) {
                        Match match = new FootballMatch(home, away);
                        fixtures.add(match);
                    }
                }

                rotateTeams(teams);
            }
        }
    }

    private void rotateTeams(List<Team> teams) {
        if (teams.size() <= 2) return;
        Team temp = teams.remove(teams.size() - 1);
        teams.add(1, temp);
    }

    @Override
    public List<Team> applyTiebreaker(List<Team> tiedTeams) {
        if (tiedTeams.size() <= 1) {
            return tiedTeams;
        }

        List<Team> result = new ArrayList<>(tiedTeams);
        Random random = new Random();

        result.sort((t1, t2) -> {
            // 1. Points first
            TeamStanding s1 = standings.get(t1);
            TeamStanding s2 = standings.get(t2);
            int p1 = s1.getPoints(2, 1);
            int p2 = s2.getPoints(2, 1);
            if (p1 != p2) return Integer.compare(p2, p1);

            // 2. Head to head
            int h2hCompare = compareHeadToHead(t1, t2);
            if (h2hCompare != 0) {
                return h2hCompare;
            }

            // 3. Goal difference
            if (s1.getGoalDifference() != s2.getGoalDifference()) {
                return Integer.compare(s2.getGoalDifference(), s1.getGoalDifference());
            }

            // 4. Coin toss
            return random.nextInt(2) == 0 ? -1 : 1;
        });

        return result;
    }

    private int compareHeadToHead(Team t1, Team t2) {
        int t1Points = 0;
        int t2Points = 0;

        for (Match match : fixtures) {
            if (!match.isCompleted()) {
                continue;
            }

            Team home = match.getHomeTeam();
            Team away = match.getAwayTeam();
            int homeScore = match.getHomeScore();
            int awayScore = match.getAwayScore();

            if (home.equals(t1) && away.equals(t2)) {
                if (homeScore > awayScore) {
                    t1Points += 3;
                } else if (homeScore == awayScore) {
                    t1Points += 1;
                    t2Points += 1;
                } else {
                    t2Points += 3;
                }
            } else if (home.equals(t2) && away.equals(t1)) {
                if (homeScore > awayScore) {
                    t2Points += 3;
                } else if (homeScore == awayScore) {
                    t2Points += 1;
                    t1Points += 1;
                } else {
                    t1Points += 3;
                }
            }
        }

        return Integer.compare(t2Points, t1Points);
    }
}

package com.sportsmanager.sports.football;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FootballMatchRegressionTest {

    @Test
    void substitutedPlayerShouldNotRecoverAsIfRested() throws Exception {
        FootballTeam home = new FootballTeam("Home FC");
        FootballPlayer[] homeRefs = addRoster(home, "Home", true);
        FootballPlayer homeStarter = homeRefs[0];
        FootballPlayer homeBench = homeRefs[1];

        FootballTeam away = new FootballTeam("Away FC");
        addRoster(away, "Away", false);

        FootballMatch match = new FootballMatch(home, away);
        setDeterministicRandom(match);

        match.simulatePeriod(1);
        match.substitutePlayer(home, homeStarter, homeBench);
        match.simulatePeriod(2);

        assertEquals(85, homeStarter.getStamina(),
                "A player who was subbed out should still count as having played the match");
        assertEquals(85, homeBench.getStamina(),
                "The substitute should still receive fatigue from the period they played");
    }

    private FootballPlayer[] addRoster(FootballTeam team, String prefix, boolean includeBench) {
        team.addPlayer(new FootballPlayer(prefix + " GK", 25, "GK", "Male", 20, 20, 20, 90, 80, 20));
        for (int i = 1; i <= 4; i++) {
            team.addPlayer(new FootballPlayer(prefix + " DF" + i, 25, "DF", "Male",
                    60 + i, 60 + i, 60 + i, 70 + i, 60 + i, 60 + i));
        }
        for (int i = 1; i <= 4; i++) {
            team.addPlayer(new FootballPlayer(prefix + " MF" + i, 25, "MF", "Male",
                    65 + i, 70 + i, 70 + i, 60 + i, 60 + i, 65 + i));
        }

        FootballPlayer starter = new FootballPlayer(prefix + " FW Starter", 25, "FW", "Male",
                95, 90, 90, 40, 20, 90);
        FootballPlayer support = new FootballPlayer(prefix + " FW Support", 25, "FW", "Male",
                85, 80, 80, 35, 20, 85);
        team.addPlayer(starter);
        team.addPlayer(support);

        FootballPlayer bench = null;
        if (includeBench) {
            bench = new FootballPlayer(prefix + " FW Bench", 25, "FW", "Male",
                    10, 10, 10, 10, 10, 10);
            team.addPlayer(bench);
        }

        team.setTactic(new FootballTactic("4-4-2", 0.0, 0.0));
        return new FootballPlayer[]{starter, bench};
    }

    private void setDeterministicRandom(FootballMatch match) throws Exception {
        Field randomField = FootballMatch.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(match, new Random() {
            @Override
            public double nextDouble() {
                return 0.99;
            }

            @Override
            public boolean nextBoolean() {
                return false;
            }

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        });
    }
}

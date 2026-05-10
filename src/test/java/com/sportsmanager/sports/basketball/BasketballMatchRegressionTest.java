package com.sportsmanager.sports.basketball;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasketballMatchRegressionTest {

    @Test
    void substitutedPlayerShouldNotRecoverAsIfRested() throws Exception {
        BasketballTeam home = new BasketballTeam("Home Hoops");
        BasketballPlayer[] homeRefs = addRoster(home, "Home", true);
        BasketballPlayer homeStarter = homeRefs[0];
        BasketballPlayer homeBench = homeRefs[1];

        BasketballTeam away = new BasketballTeam("Away Hoops");
        addRoster(away, "Away", false);

        BasketballMatch match = new BasketballMatch(home, away);
        setDeterministicRandom(match);

        match.simulatePeriod(1);
        match.substitutePlayer(home, homeStarter, homeBench);
        match.simulatePeriod(2);
        match.simulatePeriod(3);
        match.simulatePeriod(4);

        assertEquals(85, homeStarter.getStamina(),
                "A player who was subbed out should still count as having played the match");
        assertEquals(55, homeBench.getStamina(),
                "The substitute should still receive fatigue from the periods they played");
    }

    private BasketballPlayer[] addRoster(BasketballTeam team, String prefix, boolean includeBench) {
        BasketballPlayer starter = new BasketballPlayer(prefix + " PG Starter", 25, "PG", "Male",
                95, 90, 90, 70, 60, 90);
        BasketballPlayer bench = null;
        if (includeBench) {
            bench = new BasketballPlayer(prefix + " PG Bench", 25, "PG", "Male",
                    10, 10, 10, 10, 10, 10);
        }

        team.addPlayer(starter);
        if (bench != null) {
            team.addPlayer(bench);
        }
        team.addPlayer(new BasketballPlayer(prefix + " SG", 25, "SG", "Male", 80, 82, 79, 55, 50, 80));
        team.addPlayer(new BasketballPlayer(prefix + " SF", 25, "SF", "Male", 78, 76, 77, 60, 55, 79));
        team.addPlayer(new BasketballPlayer(prefix + " PF", 25, "PF", "Male", 74, 72, 70, 76, 78, 74));
        team.addPlayer(new BasketballPlayer(prefix + " C", 25, "C", "Male", 70, 68, 65, 82, 84, 72));
        team.setTactic(BasketballTactic.motionOffense());
        return new BasketballPlayer[]{starter, bench};
    }

    private void setDeterministicRandom(BasketballMatch match) throws Exception {
        Field randomField = BasketballMatch.class.getDeclaredField("random");
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

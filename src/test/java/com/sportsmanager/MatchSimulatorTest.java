package com.sportsmanager;

import com.sportsmanager.core.*;
import com.sportsmanager.engine.MatchSimulator;
import com.sportsmanager.sports.football.FootballFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class MatchSimulatorTest {

    private Match createMatch() {
        FootballFactory f = new FootballFactory();
        Team home = f.createTeam("Home FC");
        Team away = f.createTeam("Away FC");
        for (int i = 0; i < 11; i++) {
            home.addPlayer(f.createPlayer("H" + i));
            away.addPlayer(f.createPlayer("A" + i));
        }
        home.setTactic(f.createTactic("4-4-2"));
        away.setTactic(f.createTactic("4-4-2"));
        return f.createMatch(home, away);
    }

    @Test
    void testSimulateMatchCompletesMatch() {
        MatchSimulator sim = new MatchSimulator();
        Match match = createMatch();
        sim.simulateMatch(match);
        assertTrue(match.isCompleted());
    }

    @Test
    void testSimulateSeasonCompletesAllMatches() {
        MatchSimulator sim = new MatchSimulator();
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            matches.add(createMatch());
        }
        sim.simulateSeason(matches);
        for (Match m : matches) {
            assertTrue(m.isCompleted());
        }
    }

    @Test
    void testFreshMatchIsCompletedAfterSimulate() {
        MatchSimulator sim = new MatchSimulator();
        Match match = createMatch();
        assertFalse(match.isCompleted());
        sim.simulateMatch(match);
        assertTrue(match.isCompleted());
    }
}

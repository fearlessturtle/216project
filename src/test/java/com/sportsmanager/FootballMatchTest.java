package com.sportsmanager;

import com.sportsmanager.core.*;
import com.sportsmanager.sports.football.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class FootballMatchTest {

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
    void testPlayFiresAtLeastOneEvent() {
        Match match = createMatch();
        match.play();
        List<MatchEvent> events = match.getMatchEvents();
        assertTrue(events.size() >= 1);
    }

    @Test
    void testObserversGetNotified() {
        Match match = createMatch();
        boolean[] notified = {false};
        match.addObserver(event -> notified[0] = true);
        match.play();
        assertTrue(notified[0]);
    }

    @Test
    void testHomeScoreIsNonNegativeAfterPlay() {
        Match match = createMatch();
        match.play();
        assertTrue(match.getHomeScore() >= 0);
    }

    @Test
    void testAwayScoreIsNonNegativeAfterPlay() {
        Match match = createMatch();
        match.play();
        assertTrue(match.getAwayScore() >= 0);
    }

    @Test
    void testIsCompletedAfterPlay() {
        Match match = createMatch();
        match.play();
        assertTrue(match.isCompleted());
    }

    @Test
    void testNotCompletedBeforePlay() {
        Match match = createMatch();
        assertFalse(match.isCompleted());
    }

    @Test
    void testTacticChangeBetweenHalvesDontCrash() {
        FootballFactory f = new FootballFactory();
        Team home = f.createTeam("Home FC");
        Team away = f.createTeam("Away FC");
        for (int i = 0; i < 11; i++) {
            home.addPlayer(f.createPlayer("H" + i));
            away.addPlayer(f.createPlayer("A" + i));
        }
        home.setTactic(f.createTactic("4-4-2"));
        away.setTactic(f.createTactic("4-3-3"));
        Match match = f.createMatch(home, away);
        match.applyTacticChange(home, f.createTactic("3-5-2"));
        match.play();
        assertTrue(match.isCompleted());
    }

    @Test
    void testGetScoreReturnsCorrectLength() {
        Match match = createMatch();
        match.play();
        int[] score = match.getScore();
        assertEquals(2, score.length);
    }
}

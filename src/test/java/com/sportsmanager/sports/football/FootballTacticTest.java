package com.sportsmanager.sports.football;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FootballTacticTest {

    @Test
    void testTacticInitialization() {
        FootballTactic tactic = new FootballTactic("4-3-3", 10, 5);

        assertEquals("4-3-3", tactic.getTacticName());
        assertEquals(10, tactic.getOffensiveBonus());
        assertEquals(5, tactic.getDefensiveBonus());
    }

    @Test
    void testTacticBonusesAreApplied() {
        FootballTactic tactic1 = new FootballTactic("Defensive", 0, 15);
        FootballTactic tactic2 = new FootballTactic("Attacking", 15, 0);

        assertTrue(tactic1.getDefensiveBonus() > tactic2.getDefensiveBonus());
        assertTrue(tactic2.getOffensiveBonus() > tactic1.getOffensiveBonus());
    }
}
package com.sportsmanager.sports.football;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FootballTacticTest {

    @Test
    void testTacticInitialization() {
        FootballTactic tactic = new FootballTactic("4-3-3", 10, 5);

        assertEquals("4-3-3", tactic.getFormation());
        assertEquals(10, tactic.getAttackBonus());
        assertEquals(5, tactic.getDefenseBonus());
    }

    @Test
    void testTacticBonusesAreApplied() {
        FootballTactic tactic1 = new FootballTactic("Defensive", 0, 15);
        FootballTactic tactic2 = new FootballTactic("Attacking", 15, 0);

        assertTrue(tactic1.getDefenseBonus() > tactic2.getDefenseBonus());
        assertTrue(tactic2.getAttackBonus() > tactic1.getAttackBonus());
    }
}

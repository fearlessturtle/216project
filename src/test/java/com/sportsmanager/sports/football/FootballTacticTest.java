package com.sportsmanager.sports.football;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class FootballTacticTest {

    @Test
    void testFormation_433() {
        FootballTactic tactic = new FootballTactic("4-3-3", 1.2, 0.9);

        Map<String, Integer> formation = tactic.getFormation();

        assertEquals(1, formation.get("GK"));
        assertEquals(4, formation.get("DF"));
        assertEquals(3, formation.get("MF"));
        assertEquals(3, formation.get("FW"));
    }

    @Test
    void testFormation_352() {
        FootballTactic tactic = new FootballTactic("3-5-2", 1.1, 1.0);

        Map<String, Integer> formation = tactic.getFormation();

        assertEquals(1, formation.get("GK"));
        assertEquals(3, formation.get("DF"));
        assertEquals(5, formation.get("MF"));
        assertEquals(2, formation.get("FW"));
    }

    @Test
    void testDefaultFormation_whenInvalidInput() {
        FootballTactic tactic = new FootballTactic("invalid", 1.0, 1.0);

        assertEquals("4-4-2", tactic.getTacticName());
        assertEquals(0.0, tactic.getOffensiveBonus(), 0.0001);
        assertEquals(0.0, tactic.getDefensiveBonus(), 0.0001);

        Map<String, Integer> formation = tactic.getFormation();

        assertEquals(1, formation.get("GK"));
        assertEquals(4, formation.get("DF"));
        assertEquals(4, formation.get("MF"));
        assertEquals(2, formation.get("FW"));
    }

    @Test
    void testBonuses() {
        FootballTactic tactic = new FootballTactic("4-3-3", 1.5, 0.8);

        assertEquals(1.5, tactic.getOffensiveBonus());
        assertEquals(0.8, tactic.getDefensiveBonus());
    }

    @Test
    void testFormation_541() {
        FootballTactic tactic = new FootballTactic("5-4-1", -0.2, 0.25);

        assertEquals("5-4-1", tactic.getTacticName());

        Map<String, Integer> formation = tactic.getFormation();

        assertEquals(1, formation.get("GK"));
        assertEquals(5, formation.get("DF"));
        assertEquals(4, formation.get("MF"));
        assertEquals(1, formation.get("FW"));
    }
}

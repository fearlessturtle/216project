package com.sportsmanager.sports.football;

import com.sportsmanager.core.SportFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FootballFactoryTest {

    @Test
    void testFactoryReturnsNoNulls() {
        SportFactory factory = new FootballFactory();

        assertNotNull(factory.createSport());
        assertNotNull(factory.createTeam("A"));
        assertNotNull(factory.createPlayer("B"));
        assertNotNull(factory.createCoach("C"));
        assertNotNull(factory.createTactic("4-4-2"));

        // Ensure no NullPointerExceptions crash the factory when passed null
        assertNotNull(factory.createPlayer(null));
    }

    @Test
    void testBalancedFormationIsNeutral() {
        FootballTactic tactic = (FootballTactic) new FootballFactory().createTactic("4-4-2");

        assertEquals(0.0, tactic.getOffensiveBonus(), 0.0001);
        assertEquals(0.0, tactic.getDefensiveBonus(), 0.0001);
    }

    @Test
    void testFactoryMatchCreation() {
        SportFactory factory = new FootballFactory();
        var match = factory.createMatch(factory.createTeam("Home"), factory.createTeam("Away"));

        assertNotNull(match);
        assertEquals("Home", match.getHomeTeam().getName());
        assertEquals("Away", match.getAwayTeam().getName());
    }

    @Test
    void testFactorySupportsFiveFourOne() {
        FootballTactic tactic = (FootballTactic) new FootballFactory().createTactic("5-4-1");

        assertEquals("5-4-1", tactic.getTacticName());
    }
}

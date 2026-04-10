package com.sportsmanager.sports.football;

import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FootballTeamTest {

    @Test
    void testSelectLineupNeverIncludesInjuredPlayer() {
        FootballTeam team = new FootballTeam("Test FC");

        FootballPlayer healthy = new FootballPlayer("Healthy", 20, "MF", "M", 50, 50, 50, 50, 50, 50);
        FootballPlayer injured = new FootballPlayer("Injured", 20, "MF", "M", 50, 50, 50, 50, 50, 50);
        injured.injure(5); // Mark as injured

        team.addPlayer(healthy);
        team.addPlayer(injured);

        var lineup = team.selectLineup();
        assertTrue(lineup.contains(healthy));
        assertFalse(lineup.contains(injured), "Critical Rule violated: Lineup contains an injured player!");
    }

    @Test
    void testSelectLineupMax11Players() {
        FootballTeam team = new FootballTeam("Test FC");
        for (int i = 0; i < 15; i++) {
            team.addPlayer(new FootballPlayer("Player " + i, 20, "MF", "M", 50, 50, 50, 50, 50, 50));
        }

        assertEquals(11, team.selectLineup().size(), "Football lineup must be capped at 11 players.");
    }

    @Test
    void testTrainWeekUpgradesPlayers() {
        FootballTeam team = new FootballTeam("Test FC");
        FootballPlayer player = new FootballPlayer("P1", 20, "MF", "M", 50, 50, 50, 50, 50, 50);
        Coach coach = new FootballCoach("C1", 40, "Attack", 10); // Bonus = 3

        team.addPlayer(player);
        team.addCoach(coach);

        int initialShooting = player.getShooting();
        team.trainWeek();

        assertTrue(player.getShooting() > initialShooting, "Player stats should increase after trainWeek().");
    }
}

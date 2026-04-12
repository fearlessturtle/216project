package com.sportsmanager.sports.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class FootballPlayerTest {

    private FootballPlayer goalkeeper;
    private FootballPlayer forward;
    private FootballPlayer midfielder;
    private FootballPlayer defender;

    @BeforeEach
    void setUp() {
        goalkeeper = new FootballPlayer("Ali Kaya",   25, "GK", "Male",   30, 40, 35, 60, 80, 50);
        forward    = new FootballPlayer("Carlos Lima", 22, "FW", "Male",  85, 70, 75, 40, 20, 90);
        midfielder = new FootballPlayer("Yuki Tanaka",24, "MF", "Female", 65, 80, 78, 55, 25, 70);
        defender   = new FootballPlayer("Emma Smith", 27, "DF", "Female", 40, 55, 50, 82, 30, 75);
    }


    @Test
    void test01_GK_Rating_UsesGoalkeepingAndTackling() {
        int expected = (int)(80 * 0.6 + 60 * 0.4); // 72
        assertEquals(expected, goalkeeper.getOverallRating(),
                "GK: goalkeeping*0.6 + tackling*0.4");
    }

    @Test
    void test02_FW_Rating_UsesShootingAndPace() {
        int expected = (int)(85 * 0.6 + 90 * 0.4); // 87
        assertEquals(expected, forward.getOverallRating(),
                "FW: shooting*0.6 + pace*0.4");
    }

    @Test
    void test03_MF_Rating_UsesPassingAndDribbling() {
        int expected = (int)(80 * 0.5 + 78 * 0.5); // 79
        assertEquals(expected, midfielder.getOverallRating(),
                "MF: passing*0.5 + dribbling*0.5");
    }

    @Test
    void test04_DF_Rating_UsesTacklingAndPace() {
        int expected = (int)(82 * 0.6 + 75 * 0.4); // 79
        assertEquals(expected, defender.getOverallRating(),
                "DF: tackling*0.6 + pace*0.4");
    }

    @Test
    void test05_NewPlayer_IsAvailableAndNotInjured() {
        assertFalse(forward.isInjured(),  "New player not injured");
        assertTrue(forward.isAvailable(), "New player available");
        assertEquals(0, forward.getInjuryGamesLeft(), "No injury games initially");
    }

    @Test
    void test06_Injure_MakesPlayerUnavailable() {
        forward.injure(3);
        assertTrue(forward.isInjured(),            "Player injured after injure()");
        assertFalse(forward.isAvailable(),          "Injured player not available");
        assertEquals(3, forward.getInjuryGamesLeft(), "3 games left");
    }

    @Test
    void test07_RecoverOneGame_HealsCorrectly() {
        forward.injure(2);
        forward.recoverOneGame();
        assertEquals(1, forward.getInjuryGamesLeft(), "1 game left");
        assertTrue(forward.isInjured(), "Still injured after 1 recovery");

        forward.recoverOneGame();
        assertFalse(forward.isInjured(), "Recovered after 2 games");
        assertEquals(0, forward.getInjuryGamesLeft(), "0 games left");
        assertTrue(forward.isAvailable(), "Recovered player available");
    }

    @Test
    void test08_ApplyMatchFatigue_DecreasesStamina() {
        int before = goalkeeper.getStamina(); // 100
        goalkeeper.applyMatchFatigue();
        assertTrue(goalkeeper.getStamina() < before, "Stamina decreased after fatigue");
    }

    @Test
    void test09_Attributes_ClampedBetween0And100() {
        FootballPlayer p = new FootballPlayer("Max", 20, "FW", "Male", 150, 200, 999, -5, -10, 0);
        assertEquals(100, p.getShooting(),    "Shooting clamped to 100");
        assertEquals(100, p.getPassing(),     "Passing clamped to 100");
        assertEquals(100, p.getDribbling(),   "Dribbling clamped to 100");
        assertEquals(0,   p.getTackling(),    "Tackling clamped to 0");
        assertEquals(0,   p.getGoalkeeping(), "Goalkeeping clamped to 0");
    }

    @Test
    void test10_DifferentPositions_GiveDifferentRatings() {
        FootballPlayer gk = new FootballPlayer("A", 25, "GK", "Male", 50, 50, 50, 50, 80, 50);
        FootballPlayer fw = new FootballPlayer("B", 25, "FW", "Male", 80, 50, 50, 50, 50, 80);
        assertNotEquals(gk.getOverallRating(), fw.getOverallRating(),
                "GK and FW with different key stats should have different ratings");
    }
}

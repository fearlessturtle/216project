package com.sportsmanager.sports.basketball;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasketballPlayerTest {

    private BasketballPlayer createPlayer(String position,
                                           int shooting,
                                           int passing,
                                           int dribbling,
                                           int defense,
                                           int rebounding,
                                           int speed) {
        return new BasketballPlayer(
                "Test Player",
                22,
                position,
                "Male",
                shooting,
                passing,
                dribbling,
                defense,
                rebounding,
                speed
        );
    }

    @Test
    public void test01_PG_Rating() {
        BasketballPlayer player = createPlayer("PG", 70, 80, 60, 50, 40, 90);
        int expected = (int) (80 * 0.30 + 60 * 0.25 + 70 * 0.20 + 90 * 0.15 + 50 * 0.10 + 40 * 0.10);
        assertEquals(expected, player.getOverallRating());
    }

    @Test
    public void test02_SG_Rating() {
        BasketballPlayer player = createPlayer("SG", 90, 60, 80, 50, 40, 70);
        int expected = (int) (90 * 0.30 + 80 * 0.20 + 70 * 0.15 + 60 * 0.15 + 50 * 0.10 + 40 * 0.10);
        assertEquals(expected, player.getOverallRating());
    }

    @Test
    public void test03_C_Rating() {
        BasketballPlayer player = createPlayer("C", 50, 60, 40, 80, 90, 55);
        int expected = (int) (90 * 0.30 + 80 * 0.25 + 50 * 0.15 + 55 * 0.10 + 60 * 0.10 + 40 * 0.10);
        assertEquals(expected, player.getOverallRating());
    }

    @Test
    public void test04_Injure_MakesUnavailable() {
        BasketballPlayer player = createPlayer("PF", 60, 60, 60, 60, 60, 60);
        player.injure(3);
        assertTrue(player.isInjured());
        assertFalse(player.isAvailable());
        assertEquals(3, player.getInjuryGamesLeft());
    }

    @Test
    public void test05_RecoverOneGame() {
        BasketballPlayer player = createPlayer("PF", 60, 60, 60, 60, 60, 60);
        player.injure(2);
        player.recoverOneGame();
        player.recoverOneGame();
        assertFalse(player.isInjured());
        assertTrue(player.isAvailable());
        assertEquals(0, player.getInjuryGamesLeft());
    }

    @Test
    public void test06_ApplyMatchFatigue() {
        BasketballPlayer player = createPlayer("SF", 60, 60, 60, 60, 60, 60);
        int before = player.getStamina();
        player.applyMatchFatigue();
        assertTrue(player.getStamina() < before);
    }

    @Test
    public void test07_AttributeClamp_Upper() {
        BasketballPlayer player = createPlayer("SG", 150, 60, 60, 60, 60, 60);
        assertEquals(100, player.getShooting());
    }

    @Test
    public void test08_AttributeClamp_Lower() {
        BasketballPlayer player = createPlayer("PF", 60, 60, 60, -5, 60, 60);
        assertEquals(0, player.getDefense());
    }

    @Test
    public void test09_DifferentPositions_DifferentRatings() {
        BasketballPlayer pg = createPlayer("PG", 60, 60, 60, 60, 60, 60);
        BasketballPlayer c = createPlayer("C", 60, 60, 60, 60, 60, 60);
        assertNotEquals(pg.getOverallRating(), c.getOverallRating());
    }

    @Test
    public void test10_TrainWithNullCoach_AllAttributesIncrease() {
        BasketballPlayer player = createPlayer("SG", 50, 51, 52, 53, 54, 55);
        player.train(null);
        assertEquals(51, player.getShooting());
        assertEquals(52, player.getPassing());
        assertEquals(53, player.getDribbling());
        assertEquals(54, player.getDefense());
        assertEquals(55, player.getRebounding());
        assertEquals(56, player.getSpeed());
    }
}

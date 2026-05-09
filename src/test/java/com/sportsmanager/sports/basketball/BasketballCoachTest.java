package com.sportsmanager.sports.basketball;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasketballCoachTest {

    private BasketballPlayer createPlayer() {
        return new BasketballPlayer(
                "Test Player",
                22,
                "SG",
                "Male",
                50,
                50,
                50,
                50,
                50,
                50
        );
    }

    @Test
    public void test01_OffenseCoach_ImprovesScoring() {
        BasketballPlayer player = createPlayer();
        BasketballCoach coach = new BasketballCoach("Coach", 40, "Offense", 5);
        coach.train(player);
        assertEquals(52, player.getShooting());
        assertEquals(52, player.getPassing());
        assertEquals(52, player.getDribbling());
    }

    @Test
    public void test02_DefenseCoach_ImprovesBlocking() {
        BasketballPlayer player = createPlayer();
        BasketballCoach coach = new BasketballCoach("Coach", 40, "Defense", 10);
        coach.train(player);
        assertEquals(53, player.getDefense());
        assertEquals(53, player.getRebounding());
    }

    @Test
    public void test03_FitnessCoach_ImprovesSpeed() {
        BasketballPlayer player = createPlayer();
        player.applyMatchFatigue();
        player.applyMatchFatigue();
        BasketballCoach coach = new BasketballCoach("Coach", 40, "Fitness", 0);
        coach.train(player);
        assertEquals(51, player.getSpeed());
    }

    @Test
    public void test04_TrainingBonus_Scales() {
        assertEquals(1, new BasketballCoach("Coach", 40, "General", 0).getTrainingBonus());
        assertEquals(2, new BasketballCoach("Coach", 40, "General", 5).getTrainingBonus());
        assertEquals(3, new BasketballCoach("Coach", 40, "General", 10).getTrainingBonus());
        assertEquals(4, new BasketballCoach("Coach", 40, "General", 20).getTrainingBonus());
    }

    @Test
    public void test05_OffenseCoach_DoesNotAffectDefense() {
        BasketballPlayer player = createPlayer();
        BasketballCoach coach = new BasketballCoach("Coach", 40, "Offense", 5);
        coach.train(player);
        assertEquals(50, player.getDefense());
    }

    @Test
    public void test06_TrainNullPlayer_NoException() {
        BasketballCoach coach = new BasketballCoach("Coach", 40, "Offense", 5);
        assertDoesNotThrow(() -> coach.train(null));
    }

    @Test
    public void test07_MultipleSessionsStack() {
        BasketballPlayer player = createPlayer();
        BasketballCoach coach = new BasketballCoach("Coach", 40, "Offense", 5);
        coach.train(player);
        coach.train(player);
        assertEquals(54, player.getShooting());
        assertEquals(54, player.getPassing());
        assertEquals(54, player.getDribbling());
    }

    @Test
    public void test08_VeteranCoachMaxBonus() {
        BasketballCoach coach = new BasketballCoach("Coach", 50, "General", 20);
        assertEquals(4, coach.getTrainingBonus());
    }
}

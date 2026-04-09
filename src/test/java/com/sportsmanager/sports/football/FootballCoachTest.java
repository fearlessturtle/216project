package com.sportsmanager.sports.football;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class FootballCoachTest {

    private FootballPlayer forward;
    private FootballPlayer defender;
    private FootballPlayer midfielder;

    private FootballCoach attackCoach;
    private FootballCoach defenseCoach;
    private FootballCoach fitnessCoach;

    @BeforeEach
    void setUp() {
        forward    = new FootballPlayer("Carlos", 22, "FW", "Male",   85, 70, 75, 40, 20, 90);
        defender   = new FootballPlayer("Emma",   27, "DF", "Female", 40, 55, 50, 82, 30, 75);
        midfielder = new FootballPlayer("Yuki",   24, "MF", "Female", 65, 80, 78, 55, 25, 70);

        attackCoach  = new FootballCoach("A. Wenger",   60, "Attack",  20);
        defenseCoach = new FootballCoach("J. Mourinho", 58, "Defense", 15);
        fitnessCoach = new FootballCoach("T. Fitness",  45, "Fitness",  5);
    }


    @Test
    void test01_AttackCoach_ImprovesShooting_AndPassing() {
        int shootBefore  = forward.getShooting();  // 85
        int passBefore   = forward.getPassing();   // 70
        int bonus        = attackCoach.getTrainingBonus(); // 4

        attackCoach.train(forward);

        assertEquals(shootBefore + bonus, forward.getShooting(),
                "Attack coach: shooting +" + bonus);
        assertEquals(passBefore + bonus, forward.getPassing(),
                "Attack coach: passing +" + bonus);
    }

    @Test
    void test02_AttackCoach_DoesNotImproveTackling() {
        int tackBefore = forward.getTackling();
        attackCoach.train(forward);
        assertEquals(tackBefore, forward.getTackling(),
                "Attack coach should not change tackling");
    }

    @Test
    void test03_DefenseCoach_ImprovesTackling() {
        int tackBefore = defender.getTackling(); // 82
        int bonus      = defenseCoach.getTrainingBonus(); // 3

        defenseCoach.train(defender);

        assertEquals(tackBefore + bonus, defender.getTackling(),
                "Defense coach: tackling +" + bonus);
    }

    @Test
    void test04_FitnessCoach_ImprovesStamina() {
        int stamBefore = midfielder.getStamina(); // 100
        int bonus      = fitnessCoach.getTrainingBonus(); // 2

        fitnessCoach.train(midfielder);

        assertEquals(stamBefore + bonus, midfielder.getStamina(),
                "Fitness coach: stamina +" + bonus);
    }

    @Test
    void test05_TrainingBonus_ScalesWithExperience() {
        assertEquals(1, new FootballCoach("J", 30, "Attack",  2).getTrainingBonus(), "0-4 yrs  → 1");
        assertEquals(2, new FootballCoach("M", 35, "Attack",  7).getTrainingBonus(), "5-9 yrs  → 2");
        assertEquals(3, new FootballCoach("S", 45, "Attack", 12).getTrainingBonus(), "10-19 yrs→ 3");
        assertEquals(4, new FootballCoach("V", 55, "Attack", 25).getTrainingBonus(), "20+ yrs  → 4");
    }

    @Test
    void test06_VeteranCoach_GivesMaxBonus() {
        assertEquals(4, attackCoach.getTrainingBonus(), "20-year coach gives bonus 4");
    }

    @Test
    void test07_TrainNullPlayer_DoesNotThrow() {
        assertDoesNotThrow(() -> attackCoach.train(null),
                "Coaching null player should not throw");
    }

    @Test
    void test08_MultipleTrainingSessions_Stack() {
        int shootBefore = forward.getShooting(); // 85
        int bonus = attackCoach.getTrainingBonus(); // 4

        attackCoach.train(forward);
        attackCoach.train(forward);

        assertEquals(shootBefore + (bonus * 2), forward.getShooting(),
                "Two training sessions should stack: +" + (bonus * 2));
    }
}

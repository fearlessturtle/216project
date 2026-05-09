package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.AbstractCoach;
import com.sportsmanager.core.Player;

public class BasketballCoach extends AbstractCoach {

    public BasketballCoach(String name, int age, String speciality, int experience) {
        super(name, age, speciality, experience);
    }

    @Override
    public void train(Player player) {
        if (!(player instanceof BasketballPlayer bp)) return;

        int bonus = getTrainingBonus();
        switch (speciality) {
            case "Offense" -> {
                bp.setShooting(clamp(bp.getShooting() + bonus));
                bp.setPassing(clamp(bp.getPassing() + bonus));
                bp.setDribbling(clamp(bp.getDribbling() + bonus));
            }
            case "Defense" -> {
                bp.setDefense(clamp(bp.getDefense() + bonus));
                bp.setRebounding(clamp(bp.getRebounding() + bonus));
            }
            case "Fitness" -> bp.setSpeed(clamp(bp.getSpeed() + bonus));
            default -> {
                bp.setShooting(clamp(bp.getShooting() + bonus));
                bp.setPassing(clamp(bp.getPassing() + bonus));
                bp.setDefense(clamp(bp.getDefense() + bonus));
            }
        }
    }

    @Override
    public int getTrainingBonus() {
        if (experience >= 20) return 4;
        if (experience >= 10) return 3;
        if (experience >= 5) return 2;
        return 1;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}

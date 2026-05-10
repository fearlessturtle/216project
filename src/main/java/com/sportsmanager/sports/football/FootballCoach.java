package com.sportsmanager.sports.football;

import com.sportsmanager.core.AbstractCoach;
import com.sportsmanager.core.Player;

import java.util.Locale;

public class FootballCoach extends AbstractCoach {
    public FootballCoach(String name, int age, String speciality, int experience) {
        super(name, age, speciality, experience);
    }

    @Override
    public void train(Player player) {
        if (player == null) {
            return;
        }
        if (!(player instanceof FootballPlayer)) {
            return;
        }

        FootballPlayer fp = (FootballPlayer) player;
        int bonus = getTrainingBonus();
        String role = speciality == null ? "" : speciality.trim().toLowerCase(Locale.ROOT);

        switch (role) {
            case "attack":
            case "offense":
                fp.setShooting(clamp(fp.getShooting() + bonus));
                fp.setPassing(clamp(fp.getPassing() + bonus));
                break;
            case "defense":
                fp.setTackling(clamp(fp.getTackling() + bonus));
                break;
            case "fitness":
                fp.setStamina(Math.min(100, fp.getStamina() + bonus));
                break;
            default:
                fp.setDribbling(clamp(fp.getDribbling() + bonus));
                break;
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

package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.AbstractPlayer;
import com.sportsmanager.core.Coach;

public class BasketballPlayer extends AbstractPlayer {
    private int shooting;
    private int passing;
    private int dribbling;
    private int defense;
    private int rebounding;
    private int speed;

    public BasketballPlayer(String name, int age, String position, String gender,
                            int shooting, int passing, int dribbling,
                            int defense, int rebounding, int speed) {
        super(name, age, position, gender);
        this.shooting = clamp(shooting);
        this.passing = clamp(passing);
        this.dribbling = clamp(dribbling);
        this.defense = clamp(defense);
        this.rebounding = clamp(rebounding);
        this.speed = clamp(speed);
    }

    @Override
    public int getOverallRating() {
        return switch (position) {
            case "PG" -> (int) (passing * 0.30 + dribbling * 0.25 + shooting * 0.20 + speed * 0.15 + defense * 0.10 + rebounding * 0.10);
            case "SG" -> (int) (shooting * 0.30 + dribbling * 0.20 + speed * 0.15 + passing * 0.15 + defense * 0.10 + rebounding * 0.10);
            case "SF" -> (int) (shooting * 0.25 + dribbling * 0.20 + defense * 0.20 + rebounding * 0.15 + speed * 0.10 + passing * 0.10);
            case "PF" -> (int) (rebounding * 0.30 + defense * 0.25 + shooting * 0.18 + passing * 0.12 + speed * 0.10 + dribbling * 0.05);
            case "C" -> (int) (rebounding * 0.30 + defense * 0.25 + shooting * 0.15 + speed * 0.10 + passing * 0.10 + dribbling * 0.10);
            default -> (shooting + passing + dribbling + defense + rebounding + speed) / 6;
        };
    }

    @Override
    public void train(Coach coach) {
        if (coach != null) {
            coach.train(this);
        } else {
            shooting = clamp(shooting + 1);
            passing = clamp(passing + 1);
            dribbling = clamp(dribbling + 1);
            defense = clamp(defense + 1);
            rebounding = clamp(rebounding + 1);
            speed = clamp(speed + 1);
        }
    }

    public int getShooting() { return shooting; }
    public int getPassing() { return passing; }
    public int getDribbling() { return dribbling; }
    public int getDefense() { return defense; }
    public int getRebounding() { return rebounding; }
    public int getSpeed() { return speed; }

    public void setStamina(int value) {
        stamina = Math.max(0, Math.min(100, value));
    }

    public void setShooting(int value) { shooting = clamp(value); }
    public void setPassing(int value) { passing = clamp(value); }
    public void setDribbling(int value) { dribbling = clamp(value); }
    public void setDefense(int value) { defense = clamp(value); }
    public void setRebounding(int value) { rebounding = clamp(value); }
    public void setSpeed(int value) { speed = clamp(value); }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    @Override
    public String toString() {
        return super.toString()
                + " | SHO:" + shooting
                + " PAS:" + passing
                + " DRI:" + dribbling
                + " DEF:" + defense
                + " REB:" + rebounding
                + " SPD:" + speed;
    }
}

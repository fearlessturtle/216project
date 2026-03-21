package com.sportsmanager.core;

public abstract class AbstractPlayer implements Player {

    protected String name;
    protected int age;
    protected String position;
    protected int stamina;
    protected boolean injured;
    protected int injuryGames;
    protected String gender;

    public AbstractPlayer(String name, int age, String position, String gender) {
        this.name = name;
        this.age = age;
        this.position = position;
        this.gender = gender;
        this.stamina = 100;
        this.injured = false;
        this.injuryGames = 0;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getPosition() {
        return position;
    }
    @Override
    public boolean isAvailable() {
        return !injured && stamina > 20;
    }

    @Override
    public boolean isInjured() {
        return injured;
    }

    @Override
    public int getInjuryGamesLeft() {
        return injuryGames;
    }
    @Override
    public void applyMatchFatigue() {
        stamina = Math.max(0, stamina - 15);
    }
    @Override
    public void recoverOneGame() {
        if (injured) {
            injuryGames--;
            if (injuryGames <= 0) {
                injured = false;
                injuryGames = 0;
            }
        } else {
            stamina = Math.min(100, stamina + 10);
        }
    }
    @Override
    public abstract int getOverallRating();
    @Override
    public abstract void train(Coach coach);

    public void injure(int games) {
        this.injured = true;
        this.injuryGames = games;
    }
    public int getStamina() {
        return stamina;
    }
    public String getGender() {
        return gender;
    }
    @Override
    public String toString() {
        return name + " (" + position + ") | Rating: " + getOverallRating()
                + " | Stamina: " + stamina
                + (injured ? " | INJURED (" + injuryGames + " games)" : "");
    }
}

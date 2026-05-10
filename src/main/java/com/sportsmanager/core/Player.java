package com.sportsmanager.core;

public interface Player {

    String getName();

    int getAge();

    String getPosition();

    boolean isAvailable();

    boolean isInjured();

    int getInjuryGamesLeft();

    int getStamina();

    int getOverallRating();

    void train(Coach coach);

    void applyMatchFatigue();

    void recoverOneGame();
}

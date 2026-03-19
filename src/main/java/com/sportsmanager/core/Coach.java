package com.sportsmanager.core;

public interface Coach {

    String getName();

    String getSpeciality();

    int getExperience();

    void train(Player player);

    int getTrainingBonus();
}

package com.sportsmanager.core;

public abstract class AbstractCoach implements Coach {
    protected String name;
    protected int age;
    protected String speciality;
    protected int experience;

    public AbstractCoach(String name, int age, String speciality, int experience) {
        this.name = name;
        this.age = age;
        this.speciality = speciality;
        this.experience = experience;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSpeciality() {
        return speciality;
    }

    @Override
    public int getExperience() {
        return experience;
    }

    @Override
    public abstract void train(Player player);
    @Override
    public abstract int getTrainingBonus();

    @Override
    public String toString() {
        return name + " | Speciality: " + speciality
                + " | Experience: " + experience + " yrs";
    }
}

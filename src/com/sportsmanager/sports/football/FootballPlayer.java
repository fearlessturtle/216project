package com.sportsmanager.sports.football;

public class FootballPlayer {

    private String name;
    private int age;
    private int rating;

    public FootballPlayer(String name, int age, int rating) {
        this.name = name;
        this.age = age;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }
}

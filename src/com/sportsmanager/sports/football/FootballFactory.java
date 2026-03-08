package com.sportsmanager.sports.football;

public class FootballFactory {

    public FootballPlayer createPlayer(String name) {
        return new FootballPlayer(name, 20, 70);
    }

    public FootballTeam createTeam(String name) {
        return new FootballTeam(name);
    }
}
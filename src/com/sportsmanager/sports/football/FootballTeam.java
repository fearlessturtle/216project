package com.sportsmanager.sports.football;

import java.util.ArrayList;
import java.util.List;

public class FootballTeam {

    private String name;
    private List<FootballPlayer> players = new ArrayList<>();

    public FootballTeam(String name) {
        this.name = name;
    }

    public void addPlayer(FootballPlayer player) {
        players.add(player);
    }

    public List<FootballPlayer> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }
}


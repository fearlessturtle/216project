package com.sportsmanager.sports.football;

import java.util.ArrayList;
import java.util.List;

public class FootballLeague {

    private List<FootballTeam> teams = new ArrayList<>();

    public void addTeam(FootballTeam team) {
        teams.add(team);
    }

    public List<FootballTeam> getTeams() {
        return teams;
    }
}

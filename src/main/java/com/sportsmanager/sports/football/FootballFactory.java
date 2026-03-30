package com.sportsmanager.sports.football;

import com.sportsmanager.core.*;
import java.util.List;
import java.util.Arrays;


public class FootballFactory implements SportFactory {

    @Override
    public Sport createSport() {

        return new FootballSport();
    }

    @Override
    public Player createPlayer(String name) {

        return new FootballPlayer(
                name != null ? name : "Unknown Player",
                20,
                "Reserves",
                "Unspecified",
                50,
                50,
                50,
                50,
                50,
                50
        );
    }

    @Override
    public Team createTeam(String name) {
        return new FootballTeam(name != null ? name : "Unknown Team");
    }

    @Override
    public Match createMatch(Team homeTeam, Team awayTeam) {
        return new FootballMatch(homeTeam, awayTeam);
    }

    @Override
    public Tactic createTactic(String name) {
        // The name passed is used as the formation (e.g. "4-4-2")
        return new FootballTactic(
                name != null ? name : "4-4-2",
                5,
                5
        );
    }

    @Override
    public Coach createCoach(String name) {

        return new FootballCoach(
                name != null ? name : "Unknown Coach",
                40,
                "General",
                5
        );
    }

    @Override
    public List<String> getSupportedSports() {
        return Arrays.asList("Football");
    }
}

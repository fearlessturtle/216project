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
        String formation = name != null ? name : "4-4-2";
        double offensive = 0.0;
        double defensive = 0.0;

        if ("4-3-3".equalsIgnoreCase(formation)) {
            offensive = 0.2;
            defensive = -0.1;
        } else if ("3-5-2".equalsIgnoreCase(formation)) {
            offensive = 0.1;
            defensive = 0.05;
        } else if ("5-4-1".equalsIgnoreCase(formation)) {
            offensive = -0.2;
            defensive = 0.25;
        } else {
            formation = "4-4-2";
            offensive = 0.0;
            defensive = 0.0;
        }

        return new FootballTactic(formation, offensive, defensive);
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
    public League createLeague() {
        return new FootballLeague();
    }

    @Override
    public List<String> getSupportedSports() {
        return Arrays.asList("Football");
    }
}

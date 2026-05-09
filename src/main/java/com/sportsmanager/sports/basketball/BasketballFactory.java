package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.AbstractLeague;
import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Match;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.Tactic;
import com.sportsmanager.core.Team;
import java.util.Arrays;
import java.util.List;

public class BasketballFactory implements SportFactory {

    @Override
    public Sport createSport() {
        return new BasketballSport();
    }

    @Override
    public Player createPlayer(String name) {
        return new BasketballPlayer(
                name != null ? name : "Unknown",
                20,
                "PG",
                "Unspecified",
                70,
                65,
                70,
                60,
                60,
                75
        );
    }

    @Override
    public Team createTeam(String name) {
        return new BasketballTeam(name != null ? name : "Unknown");
    }

    @Override
    public Match createMatch(Team homeTeam, Team awayTeam) {
        return new BasketballMatch(homeTeam, awayTeam);
    }

    @Override
    public Tactic createTactic(String name) {
        return new BasketballTactic(
                name != null ? name : "Unknown",
                1.0,
                1.0
        );
    }

    @Override
    public Coach createCoach(String name) {
        return new BasketballCoach(
                name != null ? name : "Unknown",
                40,
                "General",
                5
        );
    }

    @Override
    public AbstractLeague createLeague() {
        return new BasketballLeague();
    }

    @Override
    public List<String> getSupportedSports() {
        return Arrays.asList("Basketball");
    }
}

package com.sportsmanager.core;

import java.util.List;

public interface SportFactory {

    Sport createSport();

    Player createPlayer(String name);

    Team createTeam(String name);

    Match createMatch(Team homeTeam, Team awayTeam);

    Tactic createTactic(String name);

    Coach createCoach(String name);

    List<String> getSupportedSports();
}

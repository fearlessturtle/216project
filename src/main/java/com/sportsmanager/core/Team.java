package com.sportsmanager.core;

import java.util.List;

public interface Team {
    String getName();
    List<Player> getPlayers();
    void addPlayer(Player player);
    void removePlayer(Player player);
    List<Player> getAvailablePlayers();
    List<Player> selectLineup();
    void trainWeek();


    List<Coach> getCoaches();
    void addCoach(Coach coach);
    Tactic getTactic();
    void setTactic(Tactic tactic);
}

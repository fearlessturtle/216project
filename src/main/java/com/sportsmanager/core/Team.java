package com.sportsmanager.core;

import java.util.List;

public interface Team {
    String getName();
    List<com.sportsmanager.core.Player> getPlayers();
    void addPlayer(com.sportsmanager.core.Player player);
    void removePlayer(com.sportsmanager.core.Player player);
    List<com.sportsmanager.core.Player> getAvailablePlayers();
    List<com.sportsmanager.core.Player> selectLineup();
    void trainWeek();


    List<com.sportsmanager.core.Coach> getCoaches();
    Tactic getTactic();
    void setTactic(Tactic tactic);
}

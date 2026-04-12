package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTeam implements Team {
    protected String name;
    protected List<Player> players;
    protected List<Coach> coaches;
    protected Tactic tactic;

    public AbstractTeam(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.tactic = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public void addPlayer(Player player) {
        if (player != null && !players.contains(player)) {
            players.add(player);
        }
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player);
    }

    @Override
    public List<Player> getAvailablePlayers() {
        List<Player> available = new ArrayList<>();
        for (Player p : players) {
            if (p != null && p.isAvailable()) {
                available.add(p);
            }
        }
        return available;
    }

    @Override
    public List<Coach> getCoaches() {
        return new ArrayList<>(coaches);
    }

    public void addCoach(Coach coach) {
        if (coach != null && !coaches.contains(coach)) {
            coaches.add(coach);
        }
    }

    public void removeCoach(Coach coach) {
        coaches.remove(coach);
    }

    @Override
    public Tactic getTactic() {
        return tactic;
    }

    @Override
    public void setTactic(Tactic tactic) {
        this.tactic = tactic;
    }

    @Override
    public abstract List<Player> selectLineup();

    @Override
    public abstract void trainWeek();
}

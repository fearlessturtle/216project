package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTeam implements Team {
    protected String name;
    protected String crest;
    protected String accentColor;
    protected List<Player> players;
    protected List<Coach> coaches;
    protected Tactic tactic;

    private static final String[] CREST_SYMBOLS = {
            "\u25C6", "\u25B2", "\u25CF", "\u2726", "\u25A0",
            "\u2605", "\u25BC", "\u25C7", "\u25C9", "\u2730"
    };

    private static final String[] ACCENT_COLORS = {
            "#c94f4f", "#e26d5a", "#c89557", "#1f6feb", "#1b5a5f",
            "#7b61ff", "#2f8f83", "#b85fb8", "#4a7c59", "#d9a441"
    };

    public AbstractTeam(String name) {
        this.name = name;
        int index = Math.floorMod(name != null ? name.hashCode() : 0, CREST_SYMBOLS.length);
        this.crest = CREST_SYMBOLS[index];
        this.accentColor = ACCENT_COLORS[Math.floorMod(name != null ? name.hashCode() : 0, ACCENT_COLORS.length)];
        this.players = new ArrayList<>();
        this.coaches = new ArrayList<>();
        this.tactic = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCrest() {
        return crest;
    }

    @Override
    public String getAccentColor() {
        return accentColor;
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
    public void resetSeasonState() {
        for (Player player : players) {
            if (player == null) {
                continue;
            }

            while (player.isInjured()) {
                player.recoverOneGame();
            }

            while (player.getStamina() < 100) {
                player.recoverOneGame();
            }
        }
    }

    @Override
    public abstract List<Player> selectLineup();

    @Override
    public abstract void trainWeek();
}

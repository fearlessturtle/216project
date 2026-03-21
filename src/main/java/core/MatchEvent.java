package com.sportsmanager.core;

public class MatchEvent {

    public enum EventType {
        GOAL,
        YELLOW_CARD,
        RED_CARD,
        SUBSTITUTION,
        INJURY,
        PERIOD_END,
        MATCH_END
    }

    private EventType type;
    private int minute;
    private String player;
    private String team;
    private String desc;

    public MatchEvent(EventType type, int minute, String player, String team, String desc) {
        this.type = type;
        this.minute = minute;
        this.desc = desc;

        if (type == EventType.PERIOD_END || type == EventType.MATCH_END) {
            this.player = null;
            this.team = null;
        } else {
            this.player = player;
            this.team = team;
        }
    }

    public static MatchEvent periodEnd(int minute) {
        return new MatchEvent(EventType.PERIOD_END, minute, null, null, null);
    }

    public static MatchEvent matchEnd(int minute) {
        return new MatchEvent(EventType.MATCH_END, minute, null, null, null);
    }

    public EventType getType() {
        return type;
    }

    public int getMinute() {
        return minute;
    }

    public String getPlayer() {
        return player;
    }

    public String getTeam() {
        return team;
    }

    public String getDescription() {
        return desc;
    }

    @Override
    public String toString() {
        return type + " at minute " + minute + " | player: " + player + " team: " + team;
    }
}
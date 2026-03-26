package com.sportsmanager.sports.football;
import com.sportsmanager.core.AbstractPlayer;
import com.sportsmanager.core.Coach;

public class FootballPlayer extends AbstractPlayer {
    private int shooting;
    private int passing;
    private int dribbling;
    private int tackling;
    private int goalkeeping;
    private int pace;

    public FootballPlayer(String name, int age, String position, String gender,
                          int shooting, int passing, int dribbling,
                          int tackling, int goalkeeping, int pace) {
        super(name, age, position, gender);
        this.shooting    = clamp(shooting);
        this.passing     = clamp(passing);
        this.dribbling   = clamp(dribbling);
        this.tackling    = clamp(tackling);
        this.goalkeeping = clamp(goalkeeping);
        this.pace        = clamp(pace);
    }


    @Override
    public int getOverallRating() {
        switch (position) {
            case "GK": return (int) (goalkeeping * 0.6 + tackling * 0.4);
            case "DF": return (int) (tackling * 0.6 + pace * 0.4);
            case "MF": return (int) (passing * 0.5 + dribbling * 0.5);
            case "FW": return (int) (shooting * 0.6 + pace * 0.4);
            default:   return (shooting + passing + dribbling + tackling + goalkeeping + pace) / 6;
        }
    }


    @Override
    public void train(Coach coach) {
        if (coach != null) {
            coach.train(this);
        } else {
            shooting    = clamp(shooting + 1);
            passing     = clamp(passing + 1);
            dribbling   = clamp(dribbling + 1);
            tackling    = clamp(tackling + 1);
            goalkeeping = clamp(goalkeeping + 1);
            pace        = clamp(pace + 1);
        }
    }


    public int getShooting()    { return shooting; }
    public int getPassing()     { return passing; }
    public int getDribbling()   { return dribbling; }
    public int getTackling()    { return tackling; }
    public int getGoalkeeping() { return goalkeeping; }
    public int getPace()        { return pace; }


    public void setShooting(int v)    { shooting    = clamp(v); }
    public void setPassing(int v)     { passing     = clamp(v); }
    public void setDribbling(int v)   { dribbling   = clamp(v); }
    public void setTackling(int v)    { tackling    = clamp(v); }
    public void setGoalkeeping(int v) { goalkeeping = clamp(v); }
    public void setPace(int v)        { pace        = clamp(v); }


    public void setStamina(int v)     { stamina = Math.max(0, Math.min(100, v)); }


    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    @Override
    public String toString() {
        return super.toString()
                + " | SHO:" + shooting
                + " PAS:" + passing
                + " DRI:" + dribbling
                + " TAC:" + tackling
                + " GK:"  + goalkeeping
                + " PAC:" + pace;
    }
}

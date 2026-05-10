package com.sportsmanager.core;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMatch implements Match {

    protected Team homeTeam;
    protected Team awayTeam;
    protected int homeScore;
    protected int awayScore;
    protected boolean completed;
    protected List<MatchObserver> observers;
    protected List<MatchEvent> events;

    public AbstractMatch(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = 0;
        this.awayScore = 0;
        this.completed = false;
        this.observers = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    @Override
    public final void play() {
        if (completed) return;
        for (int i = 1; i <= getPeriodCount(); i++) {
            simulatePeriod(i);
        }
        completed = true;
        notifyObservers(MatchEvent.matchEnd(getPeriodCount() * getPeriodLength()));
    }

    protected abstract void simulatePeriod(int periodNumber);

    protected abstract int getPeriodCount();

    protected int getPeriodLength() {
        return 45;
    }

    protected void notifyObservers(MatchEvent event) {
        events.add(event);
        for (MatchObserver observer : observers) {
            observer.onEvent(event);
        }
    }

    public void addObserver(MatchObserver observer) {
        observers.add(observer);
    }

    public void restoreState(int homeScore, int awayScore, boolean completed) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.completed = completed;
        this.events = new ArrayList<>();
    }

    public List<MatchEvent> getEvents() {
        return new ArrayList<>(events);
    }

    @Override
    public Team getHomeTeam() {
        return homeTeam;
    }

    @Override
    public Team getAwayTeam() {
        return awayTeam;
    }

    @Override
    public int getHomeScore() {
        return homeScore;
    }

    @Override
    public int getAwayScore() {
        return awayScore;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public int[] getScore() {
        return new int[]{homeScore, awayScore};
    }

    @Override
    public List<MatchEvent> getMatchEvents() {
        return new ArrayList<>(events);
    }

    @Override
    public void substitutePlayer(Team team, Player playerOut, Player playerIn) {
    }
}

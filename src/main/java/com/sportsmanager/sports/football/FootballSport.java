package com.sportsmanager.sports.football;

import com.sportsmanager.core.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FootballSport implements Sport {

    private List<Team> teams = new ArrayList<>();
    private List<TeamStanding> table = new ArrayList<>();
    private int week = 0;
    private static final int TOTAL_WEEKS = 38;

    @Override
    public String getSportName() {
        return "Football";
    }

    @Override
    public void generateLeague() {
    }

    @Override
    public void simulateWeek() {
        week++;
    }

    @Override
    public void playMatch(Match match) {
        match.play();
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }

    @Override
    public List<TeamStanding> getLeagueTable() {
        return new ArrayList<>(table);
    }

    @Override
    public int getCurrentWeek() {
        return week;
    }

    @Override
    public boolean isSeasonOver() {
        return week >= TOTAL_WEEKS;
    }

    @Override
    public int getPeriodCount() {
        return 2;
    }

    @Override
    public List<String> getPositions() {
        return Arrays.asList("GK", "DF", "MF", "FW");
    }
}

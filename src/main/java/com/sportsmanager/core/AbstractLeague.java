package com.sportsmanager.core;

import java.util.*;

public abstract class AbstractLeague implements League {

    protected List<Team> teams;
    protected List<Match> fixtures;
    protected Map<Team, TeamStanding> standings;
    protected int currentWeek;
    private Set<Match> processedMatches;
    protected long tieBreakSeed;

    public AbstractLeague() {
        this.teams = new ArrayList<>();
        this.fixtures = new ArrayList<>();
        this.standings = new HashMap<>();
        this.currentWeek = 0;
        this.processedMatches = new HashSet<>();
        this.tieBreakSeed = new Random().nextLong();
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }

    @Override
    public List<Match> getFixtures() {
        return new ArrayList<>(fixtures);
    }

    @Override
    public List<TeamStanding> getStandings() {
        List<TeamStanding> standingsList = new ArrayList<>(standings.values());
        
        standingsList.sort((s1, s2) -> {
            int p1 = s1.getPoints(2, 1);
            int p2 = s2.getPoints(2, 1);
            if (p1 != p2) {
                return Integer.compare(p2, p1);
            }
            
            Team t1 = s1.getTeam();
            Team t2 = s2.getTeam();
            List<Team> tiedTeams = applyTiebreaker(Arrays.asList(t1, t2));
            
            if (tiedTeams.get(0).equals(t1)) {
                return -1;
            } else if (tiedTeams.get(0).equals(t2)) {
                return 1;
            }
            return 0;
        });
        
        return standingsList;
    }

    @Override
    public List<Match> getNextMatches() {
        List<Match> nextMatches = new ArrayList<>();
        for (Match match : fixtures) {
            if (match != null && !match.isCompleted()) {
                nextMatches.add(match);
            }
        }
        return nextMatches;
    }

    @Override
    public abstract void generateFixture();

    @Override
    public void updateStandings(Match match) {
        if (match == null || !match.isCompleted() || processedMatches.contains(match)) {
            return;
        }

        Team homeTeam = match.getHomeTeam();
        Team awayTeam = match.getAwayTeam();

        if (!standings.containsKey(homeTeam)) {
            standings.put(homeTeam, new TeamStanding(homeTeam, 0, 0, 0, 0, 0));
        }
        if (!standings.containsKey(awayTeam)) {
            standings.put(awayTeam, new TeamStanding(awayTeam, 0, 0, 0, 0, 0));
        }

        TeamStanding homeStanding = standings.get(homeTeam);
        TeamStanding awayStanding = standings.get(awayTeam);

        int homeScore = match.getHomeScore();
        int awayScore = match.getAwayScore();

        homeStanding.setGoalsFor(homeStanding.getGoalsFor() + homeScore);
        homeStanding.setGoalsAgainst(homeStanding.getGoalsAgainst() + awayScore);

        awayStanding.setGoalsFor(awayStanding.getGoalsFor() + awayScore);
        awayStanding.setGoalsAgainst(awayStanding.getGoalsAgainst() + homeScore);

        if (homeScore > awayScore) {
            homeStanding.setWins(homeStanding.getWins() + 1);
            awayStanding.setLosses(awayStanding.getLosses() + 1);
        } else if (homeScore < awayScore) {
            homeStanding.setLosses(homeStanding.getLosses() + 1);
            awayStanding.setWins(awayStanding.getWins() + 1);
        } else {
            homeStanding.setDraws(homeStanding.getDraws() + 1);
            awayStanding.setDraws(awayStanding.getDraws() + 1);
        }

        processedMatches.add(match);
    }

    @Override
    public boolean isSeasonOver() {
        for (Match match : fixtures) {
            if (!match.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public abstract List<Team> applyTiebreaker(List<Team> teams);

    public void addTeam(Team team) {
        if (team != null && !teams.contains(team)) {
            teams.add(team);
            standings.put(team, new TeamStanding(team, 0, 0, 0, 0, 0));
        }
    }

    @Override
    public void resetSeason() {
        processedMatches.clear();
        fixtures = new ArrayList<>();
        standings = new HashMap<>();
        currentWeek = 0;
        tieBreakSeed = new Random().nextLong();

        for (Team team : teams) {
            if (team == null) {
                continue;
            }
            team.resetSeasonState();
            standings.put(team, new TeamStanding(team, 0, 0, 0, 0, 0));
        }
    }

    public void setCurrentWeek(int week) {
        this.currentWeek = week;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    protected long coinTossScore(Team team) {
        String teamName = team != null && team.getName() != null ? team.getName() : "";
        long mixed = tieBreakSeed ^ (long) teamName.hashCode() * 0x9E3779B97F4A7C15L;
        mixed ^= (mixed >>> 33);
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= (mixed >>> 33);
        mixed *= 0xc4ceb9fe1a85ec53L;
        mixed ^= (mixed >>> 33);
        return mixed;
    }
}

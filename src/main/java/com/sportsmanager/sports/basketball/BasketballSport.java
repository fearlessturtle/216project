package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.League;
import com.sportsmanager.core.Match;
import com.sportsmanager.core.ProceduralNameSource;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.Team;
import com.sportsmanager.core.TeamStanding;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BasketballSport implements Sport {

    private static final List<String> DEFAULT_TEAM_NAMES = List.of(
            "Chicago", "Boston", "Miami", "Dallas", "Denver",
            "Phoenix", "Brooklyn", "Toronto", "Milwaukee", "Atlanta",
            "Los Angeles", "San Francisco", "Seattle", "Houston", "Orlando",
            "Cleveland", "Detroit", "Portland", "Sacramento", "San Antonio",
            "New York", "Philadelphia", "Charlotte", "Memphis", "Minnesota",
            "New Orleans", "Indiana", "Oklahoma City", "Utah", "Washington"
    );

    private League league;

    public BasketballSport() {
        this.league = new BasketballLeague();
    }

    @Override
    public String getSportName() {
        return "Basketball";
    }

    @Override
    public int getPeriodCount() {
        return 4;
    }

    @Override
    public List<String> getPositions() {
        return Arrays.asList("PG", "SG", "SF", "PF", "C");
    }

    @Override
    public void generateLeague() {
        league = new BasketballLeague();

        List<String> teamNames = ProceduralNameSource.loadTeamNames("basketball", DEFAULT_TEAM_NAMES);
        Collections.shuffle(teamNames);
        ProceduralNameSource.NameBank nameBank = ProceduralNameSource.loadNameBank();
        Random random = new Random();
        String[] specialities = {"Offense", "Defense", "Fitness"};

        for (int i = 0; i < 10; i++) {
            String teamName = i < teamNames.size() ? teamNames.get(i) : "Basketball Team " + (i + 1);
            BasketballTeam team = new BasketballTeam(teamName);

            for (String position : getPositions()) {
                for (int j = 0; j < 3; j++) {
                    ProceduralNameSource.PersonName person = ProceduralNameSource.randomPerson(nameBank, random);
                    team.addPlayer(new BasketballPlayer(
                            person.fullName,
                            18 + random.nextInt(15),
                            position,
                            person.gender,
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41)
                    ));
                }
            }

            ProceduralNameSource.PersonName coachName = ProceduralNameSource.randomPerson(nameBank, random);
            String speciality = specialities[random.nextInt(specialities.length)];
            team.addCoach(new BasketballCoach(
                    coachName.fullName,
                    40 + random.nextInt(20),
                    speciality,
                    5 + random.nextInt(20)
            ));

            team.setTactic(BasketballTactic.motionOffense());
            league.addTeam(team);
        }

        league.generateFixture();
    }

    @Override
    public void simulateWeek() {
        if (league == null || isSeasonOver()) {
            return;
        }

        int simulated = 0;
        for (Match match : league.getNextMatches()) {
            if (!match.isCompleted()) {
                playMatch(match);
                simulated++;
                if (simulated == 5) {
                    break;
                }
            }
        }

        league.setCurrentWeek(league.getCurrentWeek() + 1);
    }

    @Override
    public void playMatch(Match match) {
        if (match == null) {
            return;
        }

        match.play();
        league.updateStandings(match);
    }

    @Override
    public List<Team> getTeams() {
        if (league == null) {
            return List.of();
        }
        return league.getTeams();
    }

    @Override
    public List<TeamStanding> getLeagueTable() {
        if (league == null) {
            return List.of();
        }
        return league.getStandings();
    }

    @Override
    public League getLeague() {
        return league;
    }

    @Override
    public int getCurrentWeek() {
        return league != null ? league.getCurrentWeek() : 0;
    }

    @Override
    public boolean isSeasonOver() {
        return league != null && league.isSeasonOver();
    }

    @Override
    public void setCurrentWeek(int week) {
        if (league != null) {
            league.setCurrentWeek(week);
        }
    }
}

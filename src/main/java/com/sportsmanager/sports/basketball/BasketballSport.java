package com.sportsmanager.sports.basketball;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsmanager.core.Match;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.Team;
import com.sportsmanager.core.TeamStanding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BasketballSport implements Sport {

    private BasketballLeague league;
    private int currentWeek = 0;

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

        List<String> names = loadNames();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {

            String teamName;

            if (i < names.size()) {
                teamName = names.get(i);
            } else {
                teamName = "Basketball Team " + (i + 1);
            }

            BasketballTeam team = new BasketballTeam(teamName);

            for (String position : getPositions()) {

                for (int j = 0; j < 3; j++) {

                    BasketballPlayer player = new BasketballPlayer(
                            position + " Player " + (j + 1),
                            18 + random.nextInt(15),
                            position,
                            "Male",
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41),
                            60 + random.nextInt(41)
                    );

                    team.addPlayer(player);
                }
            }

            team.addCoach(
                    new BasketballCoach(
                            "Coach " + (i + 1),
                            40 + random.nextInt(20),
                            "Offense",
                            5 + random.nextInt(20)
                    )
            );



            league.addTeam(team);
        }

        league.generateFixture();
    }

    @Override
    public void simulateWeek() {

        if (league == null) return;

        List<Match> matches = league.getFixtures();

        int startIndex = currentWeek * 5;

        for (int i = startIndex;
             i < startIndex + 5 && i < matches.size();
             i++) {

            playMatch(matches.get(i));
        }

        currentWeek++;
    }

    @Override
    public void playMatch(Match match) {

        if (match == null) return;

        match.play();
        league.updateStandings(match);
    }

    @Override
    public List<Team> getTeams() {
        return List.of();
    }

    @Override
    public List<TeamStanding> getLeagueTable() {
        return List.of();
    }

    public BasketballLeague getLeague() {
        return league;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    @Override
    public boolean isSeasonOver() {
        return false;
    }

    @Override
    public void setCurrentWeek(int week) {
        this.currentWeek = week;
    }

    private List<String> loadNames() {

        try {

            ObjectMapper mapper = new ObjectMapper();

            InputStream inputStream =
                    getClass().getResourceAsStream("/names.json");

            if (inputStream == null) {
                return new ArrayList<>();
            }

            return mapper.readValue(
                    inputStream,
                    new TypeReference<List<String>>() {}
            );

        } catch (Exception e) {

            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
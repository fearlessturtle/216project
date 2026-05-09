package com.sportsmanager.sports.basketball;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sportsmanager.core.Match;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.Team;
import com.sportsmanager.core.TeamStanding;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BasketballSport implements Sport {

    private BasketballLeague league;

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
        Collections.shuffle(names);
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

        if (league == null || isSeasonOver()) return;

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

        if (match == null) return;

        match.play();
        if (league != null) {
            league.updateStandings(match);
        }
    }

    @Override
    public List<Team> getTeams() {
        if (league == null) return List.of();
        return league.getTeams();
    }

    @Override
    public List<TeamStanding> getLeagueTable() {
        if (league == null) return List.of();
        return league.getStandings();
    }

    public BasketballLeague getLeague() {
        return league;
    }

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

    private List<String> loadNames() {

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("names.json");
            if (is != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> names = gson.fromJson(new InputStreamReader(is), listType);
                if (names != null && names.size() >= 10) {
                    return names;
                }
            }
        } catch (Exception e) {
        }

        return new ArrayList<>(Arrays.asList(
                "Chicago", "Boston", "Miami", "Dallas", "Denver",
                "Phoenix", "Brooklyn", "Toronto", "Milwaukee", "Atlanta"
        ));
    }
}
package com.sportsmanager.sports.football;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sportsmanager.core.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class FootballSport implements Sport {

    private SportFactory factory;
    private AbstractLeague league; // Using AbstractLeague to access addTeam(), setCurrentWeek()

    public FootballSport() {
        this.factory = new FootballFactory();
        this.league = (AbstractLeague) factory.createLeague();
    }

    @Override
    public String getSportName() {
        return "Football";
    }

    @Override
    public void generateLeague() {
        List<String> names = loadNames();
        Collections.shuffle(names);

        // Creates 20 randomly named teams
        for (int i = 0; i < 20; i++) {
            String teamName = (i < names.size()) ? names.get(i) : "Team " + (i + 1);
            Team team = factory.createTeam(teamName);
            league.addTeam(team);
        }

        // Generate the 38-week round robin fixture schedule
        league.generateFixture();
    }

    private List<String> loadNames() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("names.json");
            if (is != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> names = gson.fromJson(new InputStreamReader(is), listType);
                if (names != null && names.size() >= 20) {
                    return names;
                }
            }
        } catch (Exception e) {
            // Ignore exception and use fallback names below
        }

        // Fallback names ensuring we always have at least 20 unique teams
        return new ArrayList<>(Arrays.asList(
                "Lions", "Tigers", "Eagles", "Bears", "Wolves", "Sharks", "Panthers", "Hawks", "Falcons", "Ravens",
                "Bulls", "Rhinos", "Stallions", "Cobras", "Vipers", "Jaguars", "Pythons", "Dragons", "Griffins", "Knights"
        ));
    }

    @Override
    public void simulateWeek() {
        if (isSeasonOver()) return;

        // Simulate all matches for the current round/week (up to 10 matches for 20 teams)
        int simulated = 0;
        for (Match match : league.getNextMatches()) {
            if (!match.isCompleted()) {
                playMatch(match);
                simulated++;
                if (simulated == 10) break; // 10 matches = 1 full week
            }
        }
        league.setCurrentWeek(league.getCurrentWeek() + 1);
    }

    @Override
    public void playMatch(Match match) {
        // ALWAYS go through FootballMatch as requested
        match.play();
        league.updateStandings(match);
    }

    @Override
    public List<Team> getTeams() {
        return league.getTeams();
    }

    @Override
    public List<TeamStanding> getLeagueTable() {
        return league.getStandings();
    }

    @Override
    public int getCurrentWeek() {
        return league.getCurrentWeek();
    }

    @Override
    public boolean isSeasonOver() {
        return league.isSeasonOver();
    }

    @Override
    public int getPeriodCount() {
        return 2; // Football always has 2 halves
    }

    @Override
    public List<String> getPositions() {
        return Arrays.asList("GK", "DF", "MF", "FW");
    }

    @Override
    public void setCurrentWeek(int week) {
        league.setCurrentWeek(week);
    }
}

package com.sportsmanager.sports.football;

import com.sportsmanager.core.League;
import com.sportsmanager.core.Match;
import com.sportsmanager.core.ProceduralNameSource;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.Team;
import com.sportsmanager.core.TeamStanding;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FootballSport implements Sport {

    private static final List<String> DEFAULT_TEAM_NAMES = List.of(
            "Lions", "Tigers", "Eagles", "Bears", "Wolves",
            "Sharks", "Panthers", "Hawks", "Falcons", "Ravens",
            "Bulls", "Rhinos", "Stallions", "Cobras", "Vipers",
            "Jaguars", "Pythons", "Dragons", "Griffins", "Knights",
            "Titans", "Rangers", "Warriors", "Royals"
    );

    private static final String[] SPECIALITIES = {"Attack", "Defense", "Fitness"};

    private final SportFactory factory;
    private League league;

    public FootballSport() {
        this.factory = new FootballFactory();
        this.league = factory.createLeague();
    }

    @Override
    public String getSportName() {
        return "Football";
    }

    @Override
    public void generateLeague() {
        league = factory.createLeague();

        List<String> teamNames = ProceduralNameSource.loadTeamNames("football", DEFAULT_TEAM_NAMES);
        Collections.shuffle(teamNames);
        ProceduralNameSource.NameBank nameBank = ProceduralNameSource.loadNameBank();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            String teamName = i < teamNames.size() ? teamNames.get(i) : "Team " + (i + 1);
            Team team = factory.createTeam(teamName);

            Map<String, Integer> positionCounts = new LinkedHashMap<>();
            positionCounts.put("GK", 2);
            positionCounts.put("DF", 4);
            positionCounts.put("MF", 4);
            positionCounts.put("FW", 2);

            for (Map.Entry<String, Integer> entry : positionCounts.entrySet()) {
                String position = entry.getKey();
                int count = entry.getValue();
                for (int j = 0; j < count; j++) {
                    ProceduralNameSource.PersonName person = ProceduralNameSource.randomPerson(nameBank, random);
                    team.addPlayer(new FootballPlayer(
                            person.fullName,
                            18 + random.nextInt(15),
                            position,
                            person.gender,
                            50 + random.nextInt(51),
                            50 + random.nextInt(51),
                            50 + random.nextInt(51),
                            50 + random.nextInt(51),
                            50 + random.nextInt(51),
                            50 + random.nextInt(51)
                    ));
                }
            }

            ProceduralNameSource.PersonName coachName = ProceduralNameSource.randomPerson(nameBank, random);
            String speciality = SPECIALITIES[random.nextInt(SPECIALITIES.length)];
            team.addCoach(new FootballCoach(
                    coachName.fullName,
                    35 + random.nextInt(15),
                    speciality,
                    5 + random.nextInt(16)
            ));

            team.setTactic(factory.createTactic("4-4-2"));
            league.addTeam(team);
        }

        league.generateFixture();
    }

    @Override
    public void simulateWeek() {
        if (isSeasonOver()) {
            return;
        }

        int simulated = 0;
        for (Match match : league.getNextMatches()) {
            if (!match.isCompleted()) {
                playMatch(match);
                simulated++;
                if (simulated == 10) {
                    break;
                }
            }
        }
        league.setCurrentWeek(league.getCurrentWeek() + 1);
    }

    @Override
    public void playMatch(Match match) {
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
    public League getLeague() {
        return league;
    }

    @Override
    public boolean isSeasonOver() {
        return league.isSeasonOver();
    }

    @Override
    public int getPeriodCount() {
        return 2;
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

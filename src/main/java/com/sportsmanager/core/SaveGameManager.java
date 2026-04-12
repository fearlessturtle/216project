package com.sportsmanager.core;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SaveGameManager {

    private static final String SAVE_DIR = "saves";
    private static final String META_FILE = "meta.json";
    private static final String LEAGUE_FILE = "league.json";
    private static final String TEAMS_DIR = "teams";

    private Gson gson;

    public SaveGameManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Sport.class, new SportSerializer())
                .registerTypeAdapter(League.class, new LeagueSerializer())
                .registerTypeAdapter(Team.class, new TeamSerializer())
                .registerTypeAdapter(Player.class, new PlayerSerializer())
                .registerTypeAdapter(Coach.class, new CoachSerializer())
                .registerTypeAdapter(Tactic.class, new TacticSerializer())
                .create();
    }

    public void saveGame(Sport sport, String sportName) throws IOException {
        String timestamp = System.currentTimeMillis() + "";
        Path saveDir = Paths.get(SAVE_DIR, sportName + "_" + timestamp);
        Files.createDirectories(saveDir);

        saveMeta(saveDir, sportName);
        saveLeague(saveDir, sport);
        saveTeams(saveDir, sport);
    }

    public Sport loadGame(SportFactory factory, String savePath) throws IOException {
        Path saveDir = Paths.get(savePath);

        String sportName = loadMeta(saveDir);
        Sport sport = factory.createSport();
        loadLeague(saveDir, sport);
        loadTeams(saveDir, factory, sport);

        return sport;
    }

    private void saveMeta(Path saveDir, String sportName) throws IOException {
        JsonObject meta = new JsonObject();
        meta.addProperty("version", "1.0");
        meta.addProperty("sportType", sportName);
        meta.addProperty("timestamp", System.currentTimeMillis());

        Path metaFile = saveDir.resolve(META_FILE);
        Files.writeString(metaFile, gson.toJson(meta));
    }

    private String loadMeta(Path saveDir) throws IOException {
        Path metaFile = saveDir.resolve(META_FILE);
        String content = Files.readString(metaFile);
        JsonObject meta = JsonParser.parseString(content).getAsJsonObject();
        return meta.get("sportType").getAsString();
    }

    private void saveLeague(Path saveDir, Sport sport) throws IOException {
        JsonObject leagueData = new JsonObject();
        leagueData.addProperty("currentWeek", sport.getCurrentWeek());
        leagueData.addProperty("isSeasonOver", sport.isSeasonOver());
        leagueData.add("standings", gson.toJsonTree(sport.getLeagueTable()));

        Path leagueFile = saveDir.resolve(LEAGUE_FILE);
        Files.writeString(leagueFile, gson.toJson(leagueData));
    }

    private void loadLeague(Path saveDir, Sport sport) throws IOException {
        Path leagueFile = saveDir.resolve(LEAGUE_FILE);
        if (Files.exists(leagueFile)) {
            String content = Files.readString(leagueFile);
            JsonObject leagueData = JsonParser.parseString(content).getAsJsonObject();
            
            int week = leagueData.get("currentWeek").getAsInt();
            sport.setCurrentWeek(week);
        }
    }

    private void saveTeams(Path saveDir, Sport sport) throws IOException {
        Path teamsDir = saveDir.resolve(TEAMS_DIR);
        Files.createDirectories(teamsDir);

        for (Team team : sport.getTeams()) {
            JsonObject teamData = new JsonObject();
            teamData.addProperty("name", team.getName());
            teamData.add("players", gson.toJsonTree(team.getPlayers()));
            teamData.add("coaches", gson.toJsonTree(team.getCoaches()));
            teamData.add("activeTactic", gson.toJsonTree(team.getTactic()));

            String fileName = team.getName().replaceAll("\\s+", "_") + ".json";
            Path teamFile = teamsDir.resolve(fileName);
            Files.writeString(teamFile, gson.toJson(teamData));
        }
    }

    private void loadTeams(Path saveDir, SportFactory factory, Sport sport) throws IOException {
        Path teamsDir = saveDir.resolve(TEAMS_DIR);

        if (Files.exists(teamsDir)) {
            Files.list(teamsDir)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(teamFile -> {
                        try {
                            String content = Files.readString(teamFile);
                            JsonObject teamData = JsonParser.parseString(content).getAsJsonObject();

                            String teamName = teamData.get("name").getAsString();
                            Team team = factory.createTeam(teamName);

                            if (teamData.has("players") && teamData.get("players").isJsonArray()) {
                                JsonArray playersArray = teamData.getAsJsonArray("players");
                                for (JsonElement playerElem : playersArray) {
                                    JsonObject playerObj = playerElem.getAsJsonObject();
                                    Player player = deserializePlayer(factory, playerObj);
                                    if (player != null) {
                                        team.addPlayer(player);
                                    }
                                }
                            }

                            if (teamData.has("coaches") && teamData.get("coaches").isJsonArray()) {
                                JsonArray coachesArray = teamData.getAsJsonArray("coaches");
                                for (JsonElement coachElem : coachesArray) {
                                    JsonObject coachObj = coachElem.getAsJsonObject();
                                    Coach coach = deserializeCoach(factory, coachObj);
                                    if (coach != null) {
                                        team.addCoach(coach);
                                    }
                                }
                            }

                            if (teamData.has("activeTactic")) {
                                JsonObject tacticObj = teamData.getAsJsonObject("activeTactic");
                                Tactic tactic = deserializeTactic(factory, tacticObj);
                                if (tactic != null) {
                                    team.setTactic(tactic);
                                }
                            }

                            sport.getTeams().add(team);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private Player deserializePlayer(SportFactory factory, JsonObject playerObj) {
        Player player = factory.createPlayer(playerObj.get("name").getAsString());
        if (player instanceof AbstractPlayer) {
            AbstractPlayer abstractPlayer = (AbstractPlayer) player;
            if (playerObj.has("injured")) {
                boolean injured = playerObj.get("injured").getAsBoolean();
                if (injured && playerObj.has("injuryGamesLeft")) {
                    int gamesLeft = playerObj.get("injuryGamesLeft").getAsInt();
                    abstractPlayer.setInjuryGamesLeft(gamesLeft);
                }
            }
        }
        return player;
    }

    private Coach deserializeCoach(SportFactory factory, JsonObject coachObj) {
        return factory.createCoach(coachObj.get("name").getAsString());
    }

    private Tactic deserializeTactic(SportFactory factory, JsonObject tacticObj) {
        return factory.createTactic(tacticObj.get("name").getAsString());
    }

    private static class SportSerializer implements JsonSerializer<Sport> {
        @Override
        public JsonElement serialize(Sport src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getSportName());
        }
    }

    private static class LeagueSerializer implements JsonSerializer<League> {
        @Override
        public JsonElement serialize(League src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("fixtures", context.serialize(src.getFixtures()));
            obj.add("standings", context.serialize(src.getStandings()));
            return obj;
        }
    }

    private static class TeamSerializer implements JsonSerializer<Team> {
        @Override
        public JsonElement serialize(Team src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", src.getName());
            obj.add("players", context.serialize(src.getPlayers()));
            obj.add("coaches", context.serialize(src.getCoaches()));
            return obj;
        }
    }

    private static class PlayerSerializer implements JsonSerializer<Player> {
        @Override
        public JsonElement serialize(Player src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", src.getName());
            obj.addProperty("age", src.getAge());
            obj.addProperty("position", src.getPosition());
            obj.addProperty("injured", src.isInjured());
            obj.addProperty("injuryGamesLeft", src.getInjuryGamesLeft());
            obj.addProperty("overallRating", src.getOverallRating());
            return obj;
        }
    }

    private static class CoachSerializer implements JsonSerializer<Coach> {
        @Override
        public JsonElement serialize(Coach src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", src.getName());
            obj.addProperty("speciality", src.getSpeciality());
            obj.addProperty("experience", src.getExperience());
            return obj;
        }
    }

    private static class TacticSerializer implements JsonSerializer<Tactic> {
        @Override
        public JsonElement serialize(Tactic src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", src.getTacticName());
            obj.addProperty("offensive", src.getOffensiveBonus());
            obj.addProperty("defensive", src.getDefensiveBonus());
            return obj;
        }
    }
}

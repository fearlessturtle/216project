package com.sportsmanager.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveGameManager {

    private static final String SAVE_DIR = "saves";
    private static final String META_FILE = "meta.json";
    private static final String LEAGUE_FILE = "league.json";
    private static final String TEAMS_FILE = "teams.json";
    private static final String TEAMS_DIR = "teams";

    private final Gson gson;

    public SaveGameManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveGame(Sport sport, String sportName) throws IOException {
        saveGame(sport, sportName, null);
    }

    public void saveGame(Sport sport, String sportName, String managedTeamName) throws IOException {
        if (sport == null) {
            throw new IllegalArgumentException("sport cannot be null");
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        Path saveDir = Paths.get(SAVE_DIR, sportName + "_" + timestamp);
        Files.createDirectories(saveDir);

        saveMeta(saveDir, sportName, managedTeamName);
        saveTeams(saveDir, sport);
        saveLeague(saveDir, sport);
    }

    public String readSportType(String savePath) throws IOException {
        return loadMetaObject(Paths.get(savePath)).get("sportType").getAsString();
    }

    public String readManagedTeamName(String savePath) throws IOException {
        JsonObject meta = loadMetaObject(Paths.get(savePath));
        if (meta.has("managedTeam") && !meta.get("managedTeam").isJsonNull()) {
            String teamName = meta.get("managedTeam").getAsString();
            return teamName == null || teamName.isBlank() ? null : teamName;
        }
        return null;
    }

    public Sport loadGame(SportFactory factory, String savePath) throws IOException {
        Path saveDir = Paths.get(savePath);
        JsonObject meta = loadMetaObject(saveDir);
        String sportType = meta.get("sportType").getAsString();

        if (factory == null) {
            throw new IllegalArgumentException("factory cannot be null");
        }

        List<String> supported = factory.getSupportedSports();
        if (supported != null && !supported.contains(sportType)) {
            throw new IOException("Save file contains " + sportType + " but the selected factory supports " + supported);
        }

        Sport sport = factory.createSport();
        League league = sport.getLeague();
        if (!(league instanceof AbstractLeague abstractLeague)) {
            throw new IOException("Loaded sport does not expose a compatible league implementation.");
        }

        List<Team> loadedTeams = loadTeams(saveDir, factory);
        for (Team team : loadedTeams) {
            league.addTeam(team);
        }

        league.generateFixture();
        restoreLeagueState(saveDir, abstractLeague, league);

        return sport;
    }

    private void saveMeta(Path saveDir, String sportName, String managedTeamName) throws IOException {
        JsonObject meta = new JsonObject();
        meta.addProperty("version", "1.0");
        meta.addProperty("sportType", sportName);
        meta.addProperty("timestamp", System.currentTimeMillis());
        if (managedTeamName != null && !managedTeamName.isBlank()) {
            meta.addProperty("managedTeam", managedTeamName);
        }

        Files.writeString(saveDir.resolve(META_FILE), gson.toJson(meta));
    }

    private JsonObject loadMetaObject(Path saveDir) throws IOException {
        Path metaFile = saveDir.resolve(META_FILE);
        if (!Files.exists(metaFile)) {
            throw new IOException("Missing meta.json in save folder: " + saveDir);
        }

        return JsonParser.parseString(Files.readString(metaFile)).getAsJsonObject();
    }

    private void saveTeams(Path saveDir, Sport sport) throws IOException {
        JsonArray teamsArray = new JsonArray();
        Path teamsDir = saveDir.resolve(TEAMS_DIR);
        Files.createDirectories(teamsDir);

        List<Team> teams = sport.getTeams();
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            JsonObject teamJson = serializeTeam(team);
            teamsArray.add(teamJson);

            String fileName = String.format("%02d_%s.json", i + 1, sanitizeFileName(team.getName()));
            Files.writeString(teamsDir.resolve(fileName), gson.toJson(teamJson));
        }

        Files.writeString(saveDir.resolve(TEAMS_FILE), gson.toJson(teamsArray));
    }

    private void saveLeague(Path saveDir, Sport sport) throws IOException {
        League league = sport.getLeague();
        JsonObject leagueJson = new JsonObject();
        leagueJson.addProperty("currentWeek", league.getCurrentWeek());
        leagueJson.addProperty("isSeasonOver", league.isSeasonOver());
        if (league instanceof AbstractLeague abstractLeague) {
            leagueJson.addProperty("tieBreakSeed", abstractLeague.tieBreakSeed);
        }

        JsonArray fixturesArray = new JsonArray();
        for (Match match : league.getFixtures()) {
            fixturesArray.add(serializeMatch(match));
        }
        leagueJson.add("fixtures", fixturesArray);

        JsonArray standingsArray = new JsonArray();
        for (TeamStanding standing : league.getStandings()) {
            standingsArray.add(serializeStanding(standing));
        }
        leagueJson.add("standings", standingsArray);

        Files.writeString(saveDir.resolve(LEAGUE_FILE), gson.toJson(leagueJson));
    }

    private List<Team> loadTeams(Path saveDir, SportFactory factory) throws IOException {
        Path teamsFile = saveDir.resolve(TEAMS_FILE);
        if (Files.exists(teamsFile)) {
            JsonArray teamsArray = JsonParser.parseString(Files.readString(teamsFile)).getAsJsonArray();
            return deserializeTeams(factory, teamsArray);
        }

        Path teamsDir = saveDir.resolve(TEAMS_DIR);
        if (!Files.exists(teamsDir)) {
            return List.of();
        }

        List<Path> files;
        try (var stream = Files.list(teamsDir)) {
            files = stream
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted()
                    .toList();
        }

        List<Team> teams = new ArrayList<>();
        for (Path file : files) {
            JsonObject teamJson = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            teams.add(deserializeTeam(factory, teamJson));
        }
        return teams;
    }

    private List<Team> deserializeTeams(SportFactory factory, JsonArray teamsArray) {
        List<Team> teams = new ArrayList<>();
        for (JsonElement element : teamsArray) {
            teams.add(deserializeTeam(factory, element.getAsJsonObject()));
        }
        return teams;
    }

    private Team deserializeTeam(SportFactory factory, JsonObject teamJson) {
        String teamName = teamJson.has("name") ? teamJson.get("name").getAsString() : "Unnamed Team";
        Team team = factory.createTeam(teamName);

        if (teamJson.has("players") && teamJson.get("players").isJsonArray()) {
            for (JsonElement playerElement : teamJson.getAsJsonArray("players")) {
                Player player = deserializePlayer(factory, playerElement.getAsJsonObject());
                if (player != null) {
                    team.addPlayer(player);
                }
            }
        }

        if (teamJson.has("coaches") && teamJson.get("coaches").isJsonArray()) {
            for (JsonElement coachElement : teamJson.getAsJsonArray("coaches")) {
                Coach coach = deserializeCoach(factory, coachElement.getAsJsonObject());
                if (coach != null && team instanceof AbstractTeam abstractTeam) {
                    abstractTeam.addCoach(coach);
                }
            }
        }

        if (teamJson.has("activeTactic") && teamJson.get("activeTactic").isJsonObject()) {
            Tactic tactic = deserializeTactic(factory, teamJson.getAsJsonObject("activeTactic"));
            if (tactic != null) {
                team.setTactic(tactic);
            }
        }

        return team;
    }

    private Player deserializePlayer(SportFactory factory, JsonObject playerJson) {
        String name = playerJson.has("name") ? playerJson.get("name").getAsString() : "Unknown Player";
        Player player = factory.createPlayer(name);

        if (player instanceof AbstractPlayer abstractPlayer) {
            abstractPlayer.name = name;
            if (playerJson.has("age")) {
                abstractPlayer.age = playerJson.get("age").getAsInt();
            }
            if (playerJson.has("position")) {
                abstractPlayer.position = playerJson.get("position").getAsString();
            }
            if (playerJson.has("gender")) {
                abstractPlayer.gender = playerJson.get("gender").getAsString();
            }
            if (playerJson.has("stamina")) {
                abstractPlayer.setStamina(playerJson.get("stamina").getAsInt());
            }
            if (playerJson.has("injuryGamesLeft")) {
                abstractPlayer.setInjuryGamesLeft(playerJson.get("injuryGamesLeft").getAsInt());
            } else if (playerJson.has("injured") && playerJson.get("injured").getAsBoolean()) {
                abstractPlayer.injured = true;
            }
        }

        if (playerJson.has("attributes") && playerJson.get("attributes").isJsonObject()) {
            JsonObject attrs = playerJson.getAsJsonObject("attributes");
            for (Map.Entry<String, JsonElement> entry : attrs.entrySet()) {
                invokeSetter(player, entry.getKey(), entry.getValue());
            }
        }

        return player;
    }

    private Coach deserializeCoach(SportFactory factory, JsonObject coachJson) {
        String name = coachJson.has("name") ? coachJson.get("name").getAsString() : "Unknown Coach";
        Coach coach = factory.createCoach(name);

        if (coach instanceof AbstractCoach abstractCoach) {
            abstractCoach.name = name;
            if (coachJson.has("age")) {
                abstractCoach.age = coachJson.get("age").getAsInt();
            }
            if (coachJson.has("speciality")) {
                abstractCoach.speciality = coachJson.get("speciality").getAsString();
            }
            if (coachJson.has("experience")) {
                abstractCoach.experience = coachJson.get("experience").getAsInt();
            }
        }

        return coach;
    }

    private Tactic deserializeTactic(SportFactory factory, JsonObject tacticJson) {
        String name = tacticJson.has("name") ? tacticJson.get("name").getAsString() : "4-4-2";
        return factory.createTactic(name);
    }

    private void restoreLeagueState(Path saveDir, AbstractLeague abstractLeague, League league) throws IOException {
        Path leagueFile = saveDir.resolve(LEAGUE_FILE);
        if (!Files.exists(leagueFile)) {
            return;
        }

        JsonObject leagueJson = JsonParser.parseString(Files.readString(leagueFile)).getAsJsonObject();
        if (leagueJson.has("tieBreakSeed")) {
            abstractLeague.tieBreakSeed = leagueJson.get("tieBreakSeed").getAsLong();
        }
        if (leagueJson.has("fixtures") && leagueJson.get("fixtures").isJsonArray()) {
            restoreFixtures(abstractLeague, leagueJson.getAsJsonArray("fixtures"));
        }

        if (leagueJson.has("currentWeek")) {
            league.setCurrentWeek(leagueJson.get("currentWeek").getAsInt());
        }
    }

    private void restoreFixtures(AbstractLeague abstractLeague, JsonArray fixturesArray) {
        Map<String, Match> fixturesByKey = new HashMap<>();
        for (Match fixture : abstractLeague.fixtures) {
            fixturesByKey.put(buildFixtureKey(fixture.getHomeTeam().getName(), fixture.getAwayTeam().getName()), fixture);
        }

        for (JsonElement element : fixturesArray) {
            JsonObject fixtureJson = element.getAsJsonObject();
            String homeName = fixtureJson.has("homeTeam") ? fixtureJson.get("homeTeam").getAsString() : "";
            String awayName = fixtureJson.has("awayTeam") ? fixtureJson.get("awayTeam").getAsString() : "";
            Match match = fixturesByKey.get(buildFixtureKey(homeName, awayName));
            if (match == null) {
                continue;
            }

            int homeScore = fixtureJson.has("homeScore") ? fixtureJson.get("homeScore").getAsInt() : 0;
            int awayScore = fixtureJson.has("awayScore") ? fixtureJson.get("awayScore").getAsInt() : 0;
            boolean completed = fixtureJson.has("completed") && fixtureJson.get("completed").getAsBoolean();

            if (match instanceof AbstractMatch abstractMatch) {
                abstractMatch.restoreState(homeScore, awayScore, completed);
            }

            if (completed) {
                abstractLeague.updateStandings(match);
            }
        }
    }

    private JsonObject serializeTeam(Team team) {
        JsonObject teamJson = new JsonObject();
        teamJson.addProperty("name", team.getName());

        JsonArray players = new JsonArray();
        for (Player player : team.getPlayers()) {
            players.add(serializePlayer(player));
        }
        teamJson.add("players", players);

        JsonArray coaches = new JsonArray();
        for (Coach coach : team.getCoaches()) {
            coaches.add(serializeCoach(coach));
        }
        teamJson.add("coaches", coaches);

        if (team.getTactic() != null) {
            teamJson.add("activeTactic", serializeTactic(team.getTactic()));
        }

        return teamJson;
    }

    private JsonObject serializePlayer(Player player) {
        JsonObject playerJson = new JsonObject();
        playerJson.addProperty("name", player.getName());
        playerJson.addProperty("age", player.getAge());
        playerJson.addProperty("position", player.getPosition());
        playerJson.addProperty("stamina", player.getStamina());
        playerJson.addProperty("injured", player.isInjured());
        playerJson.addProperty("injuryGamesLeft", player.getInjuryGamesLeft());
        playerJson.addProperty("overallRating", player.getOverallRating());

        if (player instanceof AbstractPlayer abstractPlayer) {
            playerJson.addProperty("gender", abstractPlayer.getGender());
        }

        JsonObject attributes = new JsonObject();
        for (Method method : player.getClass().getMethods()) {
            if (method.getParameterCount() != 0 || Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            String methodName = method.getName();
            if (!methodName.startsWith("get")) {
                continue;
            }

            if (isCommonPlayerGetter(methodName)) {
                continue;
            }

            try {
                Object value = method.invoke(player);
                if (value instanceof Number number) {
                    attributes.addProperty(propertyNameFromGetter(methodName), number);
                } else if (value instanceof Boolean bool) {
                    attributes.addProperty(propertyNameFromGetter(methodName), bool);
                } else if (value instanceof String text) {
                    attributes.addProperty(propertyNameFromGetter(methodName), text);
                }
            } catch (Exception ignored) {
            }
        }

        playerJson.add("attributes", attributes);
        return playerJson;
    }

    private JsonObject serializeCoach(Coach coach) {
        JsonObject coachJson = new JsonObject();
        coachJson.addProperty("name", coach.getName());
        coachJson.addProperty("speciality", coach.getSpeciality());
        coachJson.addProperty("experience", coach.getExperience());

        if (coach instanceof AbstractCoach abstractCoach) {
            coachJson.addProperty("age", abstractCoach.age);
        }

        return coachJson;
    }

    private JsonObject serializeTactic(Tactic tactic) {
        JsonObject tacticJson = new JsonObject();
        tacticJson.addProperty("name", tactic.getTacticName());
        tacticJson.addProperty("offensiveBonus", tactic.getOffensiveBonus());
        tacticJson.addProperty("defensiveBonus", tactic.getDefensiveBonus());
        tacticJson.add("formation", gson.toJsonTree(tactic.getFormation()));
        return tacticJson;
    }

    private JsonObject serializeMatch(Match match) {
        JsonObject matchJson = new JsonObject();
        matchJson.addProperty("homeTeam", match.getHomeTeam().getName());
        matchJson.addProperty("awayTeam", match.getAwayTeam().getName());
        matchJson.addProperty("homeScore", match.getHomeScore());
        matchJson.addProperty("awayScore", match.getAwayScore());
        matchJson.addProperty("completed", match.isCompleted());
        return matchJson;
    }

    private JsonObject serializeStanding(TeamStanding standing) {
        JsonObject standingJson = new JsonObject();
        standingJson.addProperty("team", standing.getTeam().getName());
        standingJson.addProperty("wins", standing.getWins());
        standingJson.addProperty("draws", standing.getDraws());
        standingJson.addProperty("losses", standing.getLosses());
        standingJson.addProperty("goalsFor", standing.getGoalsFor());
        standingJson.addProperty("goalsAgainst", standing.getGoalsAgainst());
        standingJson.addProperty("goalDifference", standing.getGoalDifference());
        standingJson.addProperty("points", standing.getPoints(2, 1));
        return standingJson;
    }

    private void invokeSetter(Object target, String propertyName, JsonElement value) {
        String setterName = "set" + capitalize(propertyName);

        for (Method method : target.getClass().getMethods()) {
            if (!method.getName().equals(setterName) || method.getParameterCount() != 1) {
                continue;
            }

            try {
                Class<?> parameterType = method.getParameterTypes()[0];
                if ((parameterType == int.class || parameterType == Integer.class) && value.isJsonPrimitive()) {
                    method.invoke(target, value.getAsInt());
                } else if ((parameterType == double.class || parameterType == Double.class) && value.isJsonPrimitive()) {
                    method.invoke(target, value.getAsDouble());
                } else if ((parameterType == boolean.class || parameterType == Boolean.class) && value.isJsonPrimitive()) {
                    method.invoke(target, value.getAsBoolean());
                } else if (parameterType == String.class && value.isJsonPrimitive()) {
                    method.invoke(target, value.getAsString());
                }
                return;
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isCommonPlayerGetter(String methodName) {
        return methodName.equals("getName")
                || methodName.equals("getAge")
                || methodName.equals("getPosition")
                || methodName.equals("getStamina")
                || methodName.equals("getOverallRating")
                || methodName.equals("getInjuryGamesLeft")
                || methodName.equals("getGender")
                || methodName.equals("getClass");
    }

    private String propertyNameFromGetter(String getterName) {
        String base = getterName.substring(3);
        return Character.toLowerCase(base.charAt(0)) + base.substring(1);
    }

    private String buildFixtureKey(String homeTeam, String awayTeam) {
        return homeTeam + "||" + awayTeam;
    }

    private String sanitizeFileName(String value) {
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}

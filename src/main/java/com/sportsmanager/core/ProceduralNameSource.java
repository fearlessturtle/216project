package com.sportsmanager.core;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class ProceduralNameSource {

    private static final Gson GSON = new Gson();
    private static final String NAMES_RESOURCE = "names.json";
    private static final String TEAM_NAMES_RESOURCE = "team_names.json";

    private ProceduralNameSource() {
    }

    public static List<String> loadTeamNames(String sportKey, List<String> fallbackNames) {
        TeamNameBank bank = loadResource(TEAM_NAMES_RESOURCE, TeamNameBank.class);
        List<String> names = bank != null ? bank.namesFor(sportKey) : null;
        if (names != null && !names.isEmpty()) {
            return new ArrayList<>(names);
        }
        if (fallbackNames == null || fallbackNames.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(fallbackNames);
    }

    public static NameBank loadNameBank() {
        return loadResource(NAMES_RESOURCE, NameBank.class);
    }

    public static PersonName randomPerson(NameBank bank, Random random) {
        Random source = random != null ? random : new Random();
        boolean male = source.nextBoolean();
        NameGroup group = bank != null ? (male ? bank.male : bank.female) : null;
        String first = pickName(group != null ? group.first : null, male ? "Alex" : "Taylor", source);
        String last = pickName(group != null ? group.last : null, "Jordan", source);
        return new PersonName(first + " " + last, male ? "Male" : "Female");
    }

    private static <T> T loadResource(String resourceName, Class<T> type) {
        InputStream inputStream = ProceduralNameSource.class.getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            return null;
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, type);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String pickName(List<String> names, String fallback, Random random) {
        if (names == null || names.isEmpty()) {
            return fallback;
        }
        return names.get(random.nextInt(names.size()));
    }

    public static final class NameBank {
        public NameGroup male;
        public NameGroup female;
    }

    public static final class NameGroup {
        public List<String> first;
        public List<String> last;
    }

    public static final class TeamNameBank {
        public List<String> football;
        public List<String> basketball;
        public List<String> volleyball;
        public List<String> handball;

        public List<String> namesFor(String sportKey) {
            if (sportKey == null) {
                return null;
            }
            return switch (sportKey.toLowerCase(Locale.ROOT)) {
                case "football" -> football;
                case "basketball" -> basketball;
                case "volleyball" -> volleyball;
                case "handball" -> handball;
                default -> null;
            };
        }
    }

    public static final class PersonName {
        public final String fullName;
        public final String gender;

        public PersonName(String fullName, String gender) {
            this.fullName = fullName;
            this.gender = gender;
        }
    }
}

package com.sportsmanager.sports.football;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FootballTeam extends AbstractTeam {

    private static final Map<String, Integer> DEFAULT_FORMATION = Map.of(
            "GK", 1,
            "DF", 4,
            "MF", 4,
            "FW", 2
    );

    public FootballTeam(String name) {
        super(name);
    }

    @Override
    public List<Player> selectLineup() {
        List<Player> available = new ArrayList<>(getAvailablePlayers());
        List<Player> lineup = new ArrayList<>();
        Map<String, Integer> formation = resolveFormation();

        addBestPlayersForPosition(lineup, available, "GK", formation.getOrDefault("GK", 1), 11);
        addBestPlayersForPosition(lineup, available, "DF", formation.getOrDefault("DF", 4), 11);
        addBestPlayersForPosition(lineup, available, "MF", formation.getOrDefault("MF", 4), 11);
        addBestPlayersForPosition(lineup, available, "FW", formation.getOrDefault("FW", 2), 11);
        fillRemainingSlots(lineup, available, 11);

        return lineup;
    }

    @Override
    public void trainWeek() {
        for (Coach c : getCoaches()) {
            if (c instanceof FootballCoach) {
                FootballCoach fc = (FootballCoach) c;
                for (Player p : getAvailablePlayers()) {
                    if (p instanceof FootballPlayer) {
                        FootballPlayer fp = (FootballPlayer) p;
                        fp.train(fc);
                    }
                }
            }
        }
    }

    private Map<String, Integer> resolveFormation() {
        if (getTactic() != null) {
            Map<String, Integer> formation = getTactic().getFormation();
            if (formation != null && !formation.isEmpty()) {
                return formation;
            }
        }
        return DEFAULT_FORMATION;
    }

    private void addBestPlayersForPosition(List<Player> lineup,
                                           List<Player> available,
                                           String position,
                                           int count,
                                           int maxSize) {
        if (count <= 0 || lineup.size() >= maxSize) {
            return;
        }

        List<Player> candidates = new ArrayList<>();
        for (Player player : available) {
            if (player != null
                    && player.getPosition() != null
                    && position.equalsIgnoreCase(player.getPosition())) {
                candidates.add(player);
            }
        }

        candidates.sort(playerComparator());
        for (Player player : candidates) {
            if (lineup.size() >= maxSize || count <= 0) {
                break;
            }
            lineup.add(player);
            available.remove(player);
            count--;
        }
    }

    private void fillRemainingSlots(List<Player> lineup, List<Player> available, int maxSize) {
        if (lineup.size() >= maxSize) {
            return;
        }

        available.sort(playerComparator());
        for (Player player : available) {
            if (lineup.size() >= maxSize) {
                break;
            }
            lineup.add(player);
        }
    }

    private Comparator<Player> playerComparator() {
        return Comparator.comparingInt(Player::getOverallRating)
                .reversed()
                .thenComparing(player -> player.getName() == null ? "" : player.getName(),
                        String.CASE_INSENSITIVE_ORDER);
    }
}

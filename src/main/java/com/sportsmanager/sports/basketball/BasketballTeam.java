package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BasketballTeam extends AbstractTeam {

    private static final List<String> POSITION_ORDER = List.of("PG", "SG", "SF", "PF", "C");

    public BasketballTeam(String name) {
        super(name);
    }

    @Override
    public List<Player> selectLineup() {
        List<Player> available = new ArrayList<>(getAvailablePlayers());
        List<Player> lineup = new ArrayList<>();
        Map<String, Integer> formation = Map.of("PG", 1, "SG", 1, "SF", 1, "PF", 1, "C", 1);
        if (getTactic() != null) {
            Map<String, Integer> tacticFormation = getTactic().getFormation();
            if (tacticFormation != null && !tacticFormation.isEmpty()) {
                formation = tacticFormation;
            }
        }

        for (String position : POSITION_ORDER) {
            addBestPlayersForPosition(lineup, available, position, formation.getOrDefault(position, 1), 5);
        }

        fillRemainingSlots(lineup, available, 5);
        return lineup;
    }

    @Override
    public void trainWeek() {
        for (Coach coach : getCoaches()) {
            if (coach instanceof BasketballCoach) {
                BasketballCoach bc = (BasketballCoach) coach;
                for (Player player : getAvailablePlayers()) {
                    if (player instanceof BasketballPlayer) {
                        bc.train(player);
                    }
                }
            }
        }
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

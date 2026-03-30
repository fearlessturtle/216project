package com.sportsmanager.sports.football;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;

import java.util.ArrayList;
import java.util.List;

public class FootballTeam extends AbstractTeam {

    public FootballTeam(String name) {
        super(name);
    }

    @Override
    public List<Player> selectLineup() {
        List<Player> lineup = new ArrayList<>();


        List<Player> available = getAvailablePlayers();


        for (Player p : available) {
            if (!p.isInjured()) {
                lineup.add(p);

                if (lineup.size() == 11) {
                    break;
                }
            }
        }
        return lineup;
    }

    @Override
    public void trainWeek() {

        List<Coach> teamCoaches = getCoaches();
        if (teamCoaches.isEmpty()) {
            return;
        }


        Coach primaryCoach = teamCoaches.get(0);

        for (Player p : getAvailablePlayers()) {
            if (!p.isInjured()) {
                p.train(primaryCoach);
            }
        }
    }
}

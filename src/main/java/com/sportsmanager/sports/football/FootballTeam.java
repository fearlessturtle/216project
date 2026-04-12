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

        for (Player p : getAvailablePlayers()) {
            if (p instanceof FootballPlayer) {
                FootballPlayer fp = (FootballPlayer) p;
                if (!fp.isInjured()) {
                    lineup.add(fp);
                }
            }
            if (lineup.size() == 11) break;
        }

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
}
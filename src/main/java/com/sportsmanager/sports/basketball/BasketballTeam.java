package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.AbstractPlayer;
import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;

import java.util.ArrayList;
import java.util.List;

public class BasketballTeam extends AbstractTeam {

    public BasketballTeam(String name) {
        super(name);
    }

    @Override
    public List<Player> selectLineup() {

        List<Player> lineup = new ArrayList<>();

        for (Player player : players) {

            if (player instanceof BasketballPlayer
                    && player.isAvailable()
                    && lineup.size() < 5) {

                lineup.add(player);
            }
        }

        return lineup;
    }

    @Override
    public void trainWeek() {

        for (Coach coach : coaches) {

            if (coach instanceof BasketballCoach) {

                BasketballCoach bc = (BasketballCoach) coach;

                for (Player player : players) {

                    if (player instanceof BasketballPlayer
                            && player.isAvailable()) {

                        bc.train(player);
                    }
                }
            }
        }
    }
}
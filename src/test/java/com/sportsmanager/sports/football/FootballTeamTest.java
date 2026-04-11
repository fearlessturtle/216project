package com.sportsmanager.sports.football;

import com.sportsmanager.core.AbstractTeam;
import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FootballTeamTest extends AbstractTeam {

    public FootballTeamTest() {
        super("Test Team");
    }

    @Override
    public List<Player> selectLineup() {
        List<Player> lineup = new ArrayList<>();

        for (Player p : getAvailablePlayers()) {
            if (p instanceof FootballPlayer fp && !fp.isInjured()) {
                lineup.add(fp);
            }
            if (lineup.size() == 11) break;
        }

        return lineup;
    }

    @Override
    public void trainWeek() {
        for (Coach c : getCoaches()) {
            if (c instanceof FootballCoach) {
                for (Player p : getAvailablePlayers()) {
                    if (p instanceof FootballPlayer fp) {
                        fp.train((FootballCoach) c);
                    }
                }
                return;
            }
        }
    }

    // ===================== TESTS =====================

    @Test
    void lineup_should_not_exceed_11_players() {
        List<Player> lineup = selectLineup();
        assertTrue(lineup.size() <= 11);
    }

    @Test
    void lineup_should_not_include_injured_players() {
        List<Player> lineup = selectLineup();

        for (Player p : lineup) {
            if (p instanceof FootballPlayer fp) {
                assertFalse(fp.isInjured());
            } else {
                fail("Non-football player in lineup");
            }
        }
    }

    @Test
    void trainWeek_should_not_throw_exception() {
        assertDoesNotThrow(this::trainWeek);
    }
}
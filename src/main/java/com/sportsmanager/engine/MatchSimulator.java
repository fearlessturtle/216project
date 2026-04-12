package com.sportsmanager.engine;

import com.sportsmanager.core.Match;
import java.util.List;

public class MatchSimulator {

    public MatchSimulator() {
    }

    public void simulateMatch(Match match) {
        match.play();
    }

    public void simulateSeason(List<Match> matches) {
        for (Match m : matches) {
            m.play();
        }
    }
}

package com.sportsmanager.engine;

import com.sportsmanager.core.Match;
import java.util.List;

public class MatchSimulator {

    public MatchSimulator() {
    }

    public void simulateMatch(Match match) {
        if (match != null) {
            match.play();
        }
    }

    public void simulateSeason(List<Match> matches) {
        if (matches == null) {
            return;
        }

        for (Match m : matches) {
            if (m != null) {
                m.play();
            }
        }
    }
}

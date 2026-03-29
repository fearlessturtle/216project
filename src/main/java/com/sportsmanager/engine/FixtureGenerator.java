package com.sportsmanager.engine;

import com.sportsmanager.core.Team;

import java.util.ArrayList;
import java.util.List;


public class FixtureGenerator {

    public List<Team[]> generatePairs(List<Team> teams) {
        List<Team> list = new ArrayList<>(teams);

        if (list.size() % 2 != 0) {
            list.add(null);
        }

        int n = list.size();
        int totalRounds = n - 1;
        int matchesPerRound = n / 2;

        List<Team[]> firstLeg  = new ArrayList<>();
        List<Team[]> secondLeg = new ArrayList<>();

        List<Team> rotating = new ArrayList<>(list.subList(1, n));

        for (int round = 0; round < totalRounds; round++) {
            for (int i = 0; i < matchesPerRound; i++) {
                Team home = (i == 0) ? list.get(0) : rotating.get(i - 1);
                Team away = rotating.get(n - 2 - i);

                if (home != null && away != null) {
                    firstLeg.add(new Team[]{home, away});
                    secondLeg.add(new Team[]{away, home});
                }
            }

            rotating.add(0, rotating.remove(rotating.size() - 1));
        }

        List<Team[]> allPairs = new ArrayList<>();
        allPairs.addAll(firstLeg);
        allPairs.addAll(secondLeg);
        return allPairs;
    }

    public int calculateTotalWeeks(int teamCount) {
        int n = (teamCount % 2 == 0) ? teamCount : teamCount + 1;
        return (n - 1) * 2;
    }
}
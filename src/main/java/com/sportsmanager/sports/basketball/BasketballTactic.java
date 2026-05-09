package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.Tactic;

import java.util.HashMap;
import java.util.Map;

public class BasketballTactic implements Tactic {

    private final String tacticName;
    private final double offensiveBonus;
    private final double defensiveBonus;

    public BasketballTactic(String tacticName,
                            double offensiveBonus,
                            double defensiveBonus) {
        this.tacticName = tacticName;
        this.offensiveBonus = offensiveBonus;
        this.defensiveBonus = defensiveBonus;
    }

    public static BasketballTactic motionOffense() {
        return new BasketballTactic("Motion Offense", 1.3, 0.8);
    }

    public static BasketballTactic zoneDefense() {
        return new BasketballTactic("Zone Defense", 0.8, 1.4);
    }

    public static BasketballTactic pickAndRoll() {
        return new BasketballTactic("Pick and Roll", 1.2, 1.0);
    }

    public static BasketballTactic fullCourtPress() {
        return new BasketballTactic("Full Court Press", 1.0, 1.3);
    }

    @Override
    public String getTacticName() {
        return tacticName;
    }

    @Override
    public double getOffensiveBonus() {
        return offensiveBonus;
    }

    @Override
    public double getDefensiveBonus() {
        return defensiveBonus;
    }

    @Override
    public Map<String, Integer> getFormation() {

        Map<String, Integer> formation = new HashMap<>();

        formation.put("PG", 1);
        formation.put("SG", 1);
        formation.put("SF", 1);
        formation.put("PF", 1);
        formation.put("C", 1);

        return formation;
    }

    @Override
    public String toString() {
        return tacticName
                + " [OFF=" + offensiveBonus
                + ", DEF=" + defensiveBonus + "]";
    }
}
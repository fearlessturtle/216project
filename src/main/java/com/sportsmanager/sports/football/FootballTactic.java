package com.sportsmanager.sports.football;

import com.sportsmanager.core.Tactic;
import java.util.HashMap;
import java.util.Map;

public class FootballTactic implements Tactic {
    private String tacticName;
    private Map<String, Integer> formation;
    private double offensiveBonus;
    private double defensiveBonus;

    public FootballTactic(String tacticName, double offensiveBonus, double defensiveBonus) {
        this.tacticName = tacticName;
        this.offensiveBonus = offensiveBonus;
        this.defensiveBonus = defensiveBonus;
        this.formation = new HashMap<>();

        // Every football tactic always requires 1 Goalkeeper
        formation.put("GK", 1);

        if ("4-3-3".equals(tacticName)) {
            formation.put("DF", 4);
            formation.put("MF", 3);
            formation.put("FW", 3);
        } else if ("3-5-2".equals(tacticName)) {
            formation.put("DF", 3);
            formation.put("MF", 5);
            formation.put("FW", 2);
        } else {
            // Default to 4-4-2 if no specific format is passed
            this.tacticName = "4-4-2";
            formation.put("DF", 4);
            formation.put("MF", 4);
            formation.put("FW", 2);
        }
    }

    @Override
    public String getTacticName() {
        return tacticName;
    }

    @Override
    public Map<String, Integer> getFormation() {
        return new HashMap<>(formation);
    }

    @Override
    public double getOffensiveBonus() {
        return offensiveBonus;
    }

    @Override
    public double getDefensiveBonus() {
        return defensiveBonus;
    }
}

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
        this.formation = new HashMap<>();

        String requestedName = tacticName != null ? tacticName.trim() : "";

        // Every football tactic always requires 1 Goalkeeper.
        formation.put("GK", 1);

        if ("4-3-3".equalsIgnoreCase(requestedName)) {
            this.tacticName = "4-3-3";
            this.offensiveBonus = offensiveBonus;
            this.defensiveBonus = defensiveBonus;
            formation.put("DF", 4);
            formation.put("MF", 3);
            formation.put("FW", 3);
        } else if ("3-5-2".equalsIgnoreCase(requestedName)) {
            this.tacticName = "3-5-2";
            this.offensiveBonus = offensiveBonus;
            this.defensiveBonus = defensiveBonus;
            formation.put("DF", 3);
            formation.put("MF", 5);
            formation.put("FW", 2);
        } else if ("5-4-1".equalsIgnoreCase(requestedName)) {
            this.tacticName = "5-4-1";
            this.offensiveBonus = offensiveBonus;
            this.defensiveBonus = defensiveBonus;
            formation.put("DF", 5);
            formation.put("MF", 4);
            formation.put("FW", 1);
        } else if (requestedName.isEmpty() || "4-4-2".equalsIgnoreCase(requestedName)) {
            this.tacticName = "4-4-2";
            this.offensiveBonus = 0.0;
            this.defensiveBonus = 0.0;
            formation.put("DF", 4);
            formation.put("MF", 4);
            formation.put("FW", 2);
        } else {
            // Default to a neutral 4-4-2 if an unsupported format is passed.
            this.tacticName = "4-4-2";
            this.offensiveBonus = 0.0;
            this.defensiveBonus = 0.0;
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

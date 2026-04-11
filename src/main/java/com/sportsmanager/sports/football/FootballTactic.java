package com.sportsmanager.sports.football;

import com.sportsmanager.core.Tactic;
import java.util.*;

public class FootballTactic implements Tactic {
    private String formation;
    private Map<String, Integer> formationMap;
    private int attackBonus;
    private int defenseBonus;

    public FootballTactic(String formation, int attackBonus, int defenseBonus) {
        this.formation = formation;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.formationMap = parseFormation(formation);
    }

    @Override
    public String getTacticName() {
        return formation;
    }

    @Override
    public Map<String, Integer> getFormation() {
        return new HashMap<>(formationMap);
    }

    private Map<String, Integer> parseFormation(String formationStr) {
        Map<String, Integer> map = new HashMap<>();
        String[] parts = formationStr.split("-");
        if (parts.length == 3) {
            map.put("defenders", Integer.parseInt(parts[0]));
            map.put("midfielders", Integer.parseInt(parts[1]));
            map.put("forwards", Integer.parseInt(parts[2]));
        }
        return map;
    }

    @Override
    public double getOffensiveBonus() {
        return attackBonus;
    }

    @Override
    public double getDefensiveBonus() {
        return defenseBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }
}

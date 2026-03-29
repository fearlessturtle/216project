package com.sportsmanager.sports.football;

import com.sportsmanager.core.Tactic;

public class FootballTactic implements Tactic {
    private String formation;
    private int attackBonus;
    private int defenseBonus;

    public FootballTactic(String formation, int attackBonus, int defenseBonus) {
        this.formation = formation;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
    }

    @Override
    public String getTacticName() {
        return "";
    }

    public String getFormation() {
        return formation;
    }

    @Override
    public double getOffensiveBonus() {
        return 0;
    }

    @Override
    public double getDefensiveBonus() {
        return 0;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }
}

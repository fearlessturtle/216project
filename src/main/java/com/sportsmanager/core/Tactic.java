package com.sportsmanager.core;

import java.util.Map;

public interface Tactic {

    String getTacticName();

    Map<String, Integer> getFormation();

    double getOffensiveBonus();

    double getDefensiveBonus();
}

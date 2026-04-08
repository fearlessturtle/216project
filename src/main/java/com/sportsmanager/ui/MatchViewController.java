package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.MatchObserver;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MatchViewController implements MatchObserver {

    @FXML private TextArea eventLog;

    private Match match;

    public void setMatch(Match match) {
        this.match = match;
        match.addObserver(this);
    }

    @Override
    public void onEvent(MatchEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getType().toString());
        if (event.getPlayer() != null) {
            sb.append(" ").append(event.getPlayer());
        }
        if (event.getTeam() != null) {
            sb.append(" (").append(event.getTeam()).append(")");
        }
        eventLog.appendText(sb.toString() + "\n");
    }
}

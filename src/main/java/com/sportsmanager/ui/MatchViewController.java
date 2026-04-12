package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.MatchObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class MatchViewController implements MatchObserver {

    @FXML private TextArea eventLog;
    @FXML private Button continueButton;

    private Match match;

    public void setMatch(Match match) {
        this.match = match;
        match.addObserver(this);
    }

    public void setPostMatch(Match match) {
        this.match = match;
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

    @FXML
    private void handleContinue() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PostMatch.fxml"));
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            PostMatchController controller = loader.getController();
            controller.setMatch(match);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

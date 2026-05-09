package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.Sport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PreMatchController {

    @FXML private Label homeTeamLabel;
    @FXML private Label awayTeamLabel;
    @FXML private Button simulateButton;

    private Match match;
    private Sport sport;

    public void setMatch(Match match) {
        this.match = match;
        homeTeamLabel.setText(match.getHomeTeam().getName());
        awayTeamLabel.setText(match.getAwayTeam().getName());
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @FXML
    private void handleSimulate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MatchView.fxml"));
            Stage stage = (Stage) simulateButton.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            MatchViewController mvc = loader.getController();
            mvc.setMatch(match);
            match.play();
            mvc.setPostMatch(match);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

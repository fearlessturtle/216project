package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
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

    public void setMatch(Match match) {
        this.match = match;
        homeTeamLabel.setText(match.getHomeTeam().getName());
        awayTeamLabel.setText(match.getAwayTeam().getName());
    }

    @FXML
    private void handleSimulate() {
        match.play();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PostMatch.fxml"));
            Stage stage = (Stage) simulateButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            PostMatchController controller = loader.getController();
            controller.setMatch(match);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

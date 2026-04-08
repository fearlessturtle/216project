package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PostMatchController {

    @FXML private Label homeTeamLabel;
    @FXML private Label homeScoreLabel;
    @FXML private Label awayTeamLabel;
    @FXML private Label awayScoreLabel;
    @FXML private Button backButton;

    private Match match;

    public void setMatch(Match match) {
        this.match = match;
        homeTeamLabel.setText(match.getHomeTeam().getName());
        homeScoreLabel.setText(String.valueOf(match.getHomeScore()));
        awayTeamLabel.setText(match.getAwayTeam().getName());
        awayScoreLabel.setText(String.valueOf(match.getAwayScore()));
    }

    @FXML
    private void handleBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

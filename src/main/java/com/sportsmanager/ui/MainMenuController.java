package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.Sport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML private Label titleLabel;
    @FXML private Button startButton;

    private Sport sport;
    private Match match;

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @FXML
    private void handleStartGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PreMatch.fxml"));
            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            PreMatchController controller = loader.getController();
            controller.setSport(sport);
            controller.setMatch(match);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

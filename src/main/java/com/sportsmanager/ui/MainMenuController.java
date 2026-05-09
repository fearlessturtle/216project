package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.SportFactory;
import com.sportsmanager.core.Team;
import com.sportsmanager.sports.basketball.BasketballFactory;
import com.sportsmanager.sports.football.FootballFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainMenuController {

    @FXML private Label titleLabel;
    @FXML private Button startButton;
    @FXML private ComboBox<String> sportComboBox;

    private Sport sport;
    private Match match;
    private final Map<String, SportFactory> sportFactories = new LinkedHashMap<>();

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @FXML
    private void initialize() {
        sportFactories.put("Football", new FootballFactory());
        sportFactories.put("Basketball", new BasketballFactory());

        if (sportComboBox != null) {
            sportComboBox.getItems().setAll(sportFactories.keySet());
            sportComboBox.getSelectionModel().selectFirst();
        }
    }

    private SportFactory getSelectedFactory() {
        if (sportComboBox != null) {
            String selected = sportComboBox.getSelectionModel().getSelectedItem();
            if (selected != null && sportFactories.containsKey(selected)) {
                return sportFactories.get(selected);
            }
        }

        return sportFactories.values().stream().findFirst().orElse(null);
    }

    private void ensureSportAndMatch() {
        if (sport != null && match != null) {
            return;
        }

        SportFactory factory = getSelectedFactory();
        if (factory == null) {
            return;
        }

        sport = factory.createSport();
        sport.generateLeague();

        List<Team> teams = sport.getTeams();
        if (teams != null && teams.size() >= 2) {
            match = factory.createMatch(teams.get(0), teams.get(1));
        }
    }

    @FXML
    private void handleStartGame() {
        try {
            ensureSportAndMatch();
            if (sport == null || match == null) {
                titleLabel.setText("Unable to start game");
                return;
            }
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

package com.sportsmanager.ui;

import com.sportsmanager.core.SaveGameManager;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.SportFactory;
import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class MainMenuController {

    @FXML private Label titleLabel;
    @FXML private Button startButton;
    @FXML private Button loadButton;
    @FXML private Button quitButton;
    @FXML private ComboBox<String> sportComboBox;
    @FXML private VBox menuCard;

    private Sport sport;
    private final Map<String, SportFactory> sportFactories = new LinkedHashMap<>();

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    private void setStatusMessage(String message) {
        if (titleLabel != null) {
            titleLabel.setText(message);
        }
    }

    @FXML
    private void initialize() {
        sportFactories.clear();
        ServiceLoader<SportFactory> loader = ServiceLoader.load(SportFactory.class);
        for (SportFactory factory : loader) {
            List<String> sports = factory.getSupportedSports();
            if (sports == null) {
                continue;
            }
            for (String sportName : sports) {
                if (sportName == null || sportName.isBlank()) {
                    continue;
                }
                sportFactories.put(sportName, factory);
            }
        }

        if (sportComboBox != null) {
            List<String> names = new ArrayList<>(sportFactories.keySet());
            names.sort(String.CASE_INSENSITIVE_ORDER);
            sportComboBox.getItems().setAll(names);
            if (!names.isEmpty()) {
                sportComboBox.getSelectionModel().selectFirst();
            } else {
                setStatusMessage("No sports available");
            }
        }

        if (menuCard != null) {
            menuCard.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(350), menuCard);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
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
        if (sport != null) {
            return;
        }

        SportFactory factory = getSelectedFactory();
        if (factory == null) {
            return;
        }

        sport = factory.createSport();
        sport.generateLeague();
    }

    private void openLeagueView(String managedTeamName) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LeagueView.fxml"));
        Stage stage = (Stage) startButton.getScene().getWindow();
        UiNavigator.setScene(stage, loader.load(),
                sport != null ? "Sports Manager - " + sport.getSportName() + " Control Room" : "Sports Manager - League View");
        LeagueViewController controller = loader.getController();
        controller.setSport(sport);
        controller.setManagedTeamName(managedTeamName);
        stage.show();
    }

    @FXML
    private void handleStartGame() {
        try {
            ensureSportAndMatch();
            if (sport == null) {
                setStatusMessage("Unable to start game");
                return;
            }
            openLeagueView(null);
        } catch (Exception e) {
            setStatusMessage("Unable to start game");
        }
    }

    @FXML
    private void handleLoadGame() {
        try {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Save Folder");
            File dir = chooser.showDialog(startButton.getScene().getWindow());
            if (dir == null) {
                return;
            }

            File metaFile = new File(dir, "meta.json");
            if (!metaFile.isFile()) {
                setStatusMessage("Please select a folder created by Save Game.");
                return;
            }

            SaveGameManager manager = new SaveGameManager();
            String sportType = manager.readSportType(dir.getAbsolutePath());
            String managedTeamName = manager.readManagedTeamName(dir.getAbsolutePath());
            SportFactory factory = sportType != null ? sportFactories.get(sportType) : getSelectedFactory();
            if (factory == null) {
                setStatusMessage("No matching sport factory");
                return;
            }

            Sport loadedSport = manager.loadGame(factory, dir.getAbsolutePath());
            if (loadedSport == null) {
                setStatusMessage("Load failed");
                return;
            }

            sport = loadedSport;
            openLeagueView(managedTeamName);
        } catch (Exception e) {
            setStatusMessage("Load failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleQuit() {
        Platform.exit();
    }
}

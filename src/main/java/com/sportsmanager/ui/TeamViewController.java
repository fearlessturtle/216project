package com.sportsmanager.ui;

import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.Team;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class TeamViewController implements Initializable {

    private Sport sport;
    @FXML private Label  budgetLabel;
    @FXML private Label  statusLabel;
    @FXML private Button playersTabBtn;
    @FXML private Button coachesTabBtn;
    @FXML private VBox   playersPanel;
    @FXML private VBox   coachesPanel;

    @FXML private TableView<Player>           playersTable;
    @FXML private TableColumn<Player, String> colName;
    @FXML private TableColumn<Player, String> colPosition;
    @FXML private TableColumn<Player, Number> colRating;
    @FXML private TableColumn<Player, String> colStatus;

    @FXML private TableView<Coach>            coachesTable;
    @FXML private TableColumn<Coach, String>  colCoachName;
    @FXML private TableColumn<Coach, String>  colCoachSpeciality;
    @FXML private TableColumn<Coach, Number>  colCoachExperience;
    @FXML private TableColumn<Coach, Number>  colCoachBonus;

    private Team currentTeam;

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPlayersTable();
        setupCoachesTable();
        showPlayers();
    }

    private void setupPlayersTable() {
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));
        colPosition.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPosition()));
        colRating.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getOverallRating()));
        colStatus.setCellValueFactory(d -> {
            Player p = d.getValue();
            if (p.isInjured())
                return new SimpleStringProperty("INJURED (" + p.getInjuryGamesLeft() + " games)");
            String availability = p.isAvailable() ? "Available" : "Tired";
            return new SimpleStringProperty(availability + " (" + p.getStamina() + " stamina)");
        });

        playersTable.setRowFactory(tv -> new TableRow<Player>() {
            @Override
            protected void updateItem(Player p, boolean empty) {
                super.updateItem(p, empty);
                getStyleClass().removeAll("injured-row", "tired-row");
                if (p == null || empty) {
                    setStyle("");
                } else if (p.isInjured()) {
                    getStyleClass().add("injured-row");
                } else if (!p.isAvailable()) {
                    getStyleClass().add("tired-row");
                }
            }
        });
        UiStyles.useConstrainedResizePolicy(playersTable);
    }

    private void setupCoachesTable() {
        colCoachName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));
        colCoachSpeciality.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getSpeciality()));
        colCoachExperience.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getExperience()));
        colCoachBonus.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getTrainingBonus()));
        UiStyles.useConstrainedResizePolicy(coachesTable);
    }

    public void setTeam(Team team) {
        this.currentTeam = team;
        refreshPlayers();
        refreshCoaches();
        if (statusLabel != null && team != null) {
            statusLabel.setText("Managing: " + team.getCrest() + " " + team.getName());
            UiStyles.applyAccentBadge(statusLabel, team.getAccentColor());
        } else if (statusLabel != null) {
            statusLabel.setText("");
            UiStyles.clearAccentBadge(statusLabel);
        }
        if (budgetLabel != null && team != null) {
            budgetLabel.setText(team.getCrest() + " Active Club: " + team.getName());
            UiStyles.applyAccentBadge(budgetLabel, team.getAccentColor());
        } else if (budgetLabel != null) {
            budgetLabel.setText("Active Club");
            UiStyles.clearAccentBadge(budgetLabel);
        }
    }

    @FXML public void showPlayers() {
        playersPanel.setVisible(true);  playersPanel.setManaged(true);
        coachesPanel.setVisible(false); coachesPanel.setManaged(false);
        updateTabState(true);
    }

    @FXML public void showCoaches() {
        coachesPanel.setVisible(true);  coachesPanel.setManaged(true);
        playersPanel.setVisible(false); playersPanel.setManaged(false);
        updateTabState(false);
    }

    @FXML public void goBack() {
        if (sport == null) {
            if (statusLabel != null) statusLabel.setText("Returning...");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LeagueView.fxml"));
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(),
                    sport != null ? "Sports Manager - " + sport.getSportName() + " Control Room" : "Sports Manager - League View");
            LeagueViewController controller = loader.getController();
            controller.setSport(sport);
            controller.setManagedTeamName(currentTeam != null ? currentTeam.getName() : null);
            stage.show();
        } catch (Exception e) {
            if (statusLabel != null) statusLabel.setText("Could not return to league.");
        }
    }

    private void refreshPlayers() {
        if (currentTeam != null)
            playersTable.setItems(FXCollections.observableArrayList(currentTeam.getPlayers()));
    }

    private void refreshCoaches() {
        if (currentTeam != null)
            coachesTable.setItems(FXCollections.observableArrayList(currentTeam.getCoaches()));
    }

    private void updateTabState(boolean playersActive) {
        if (playersTabBtn != null) {
            playersTabBtn.getStyleClass().removeAll("tab-button-active", "tab-button-inactive");
            playersTabBtn.getStyleClass().add(playersActive ? "tab-button-active" : "tab-button-inactive");
        }
        if (coachesTabBtn != null) {
            coachesTabBtn.getStyleClass().removeAll("tab-button-active", "tab-button-inactive");
            coachesTabBtn.getStyleClass().add(playersActive ? "tab-button-inactive" : "tab-button-active");
        }
    }
}

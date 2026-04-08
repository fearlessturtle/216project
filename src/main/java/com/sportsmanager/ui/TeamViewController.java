package com.sportsmanager.ui;

import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Team;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class TeamViewController implements Initializable {


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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPlayersTable();
        setupCoachesTable();
        showPlayers();
    }


    private void setupPlayersTable() {

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        colPosition.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPosition()));

        colRating.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getOverallRating()));

        colStatus.setCellValueFactory(data -> {
            Player p = data.getValue();
            if (p.isInjured()) {
                return new SimpleStringProperty(
                        "INJURED (" + p.getInjuryGamesLeft() + " games)");
            }
            return new SimpleStringProperty(p.isAvailable() ? "Available" : "Tired");
        });

        playersTable.setRowFactory(tv -> new TableRow<Player>() {
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                if (player == null || empty) {
                    setStyle("");
                } else if (player.isInjured()) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else if (!player.isAvailable()) {
                    setStyle("-fx-background-color: #fff3cc;");
                } else {
                    setStyle("");
                }
            }
        });
    }


    private void setupCoachesTable() {

        colCoachName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        colCoachSpeciality.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSpeciality()));

        colCoachExperience.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getExperience()));

        colCoachBonus.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getTrainingBonus()));
    }


    public void setTeam(Team team) {
        this.currentTeam = team;
        refreshPlayers();
        refreshCoaches();
        if (statusLabel != null) {
            statusLabel.setText("Managing: " + team.getName());
        }
    }


    @FXML
    public void showPlayers() {
        playersPanel.setVisible(true);
        playersPanel.setManaged(true);
        coachesPanel.setVisible(false);
        coachesPanel.setManaged(false);
    }

    @FXML
    public void showCoaches() {
        coachesPanel.setVisible(true);
        coachesPanel.setManaged(true);
        playersPanel.setVisible(false);
        playersPanel.setManaged(false);
    }


    @FXML
    public void goBack() {
        if (statusLabel != null) {
            statusLabel.setText("Returning to league view...");
        }
    }


    private void refreshPlayers() {
        if (currentTeam != null) {
            playersTable.setItems(
                    FXCollections.observableArrayList(currentTeam.getPlayers()));
        }
    }

    private void refreshCoaches() {
        if (currentTeam != null) {
            coachesTable.setItems(
                    FXCollections.observableArrayList(currentTeam.getCoaches()));
        }
    }
}
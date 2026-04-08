package com.sportsmanager.ui;

import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Team;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class TrainingController implements Initializable {


    @FXML private Label    weekLabel;
    @FXML private Label    statusLabel;
    @FXML private Label    expectedGrowthLabel;
    @FXML private Label    selectedCoachLabel;
    @FXML private Label    coachBonusLabel;

    @FXML private CheckBox chkShooting;
    @FXML private CheckBox chkDefensive;
    @FXML private CheckBox chkStamina;
    @FXML private CheckBox chkPassing;

    @FXML private ListView<String> playerListView;
    @FXML private ListView<String> coachListView;


    private Team         currentTeam;
    private Coach        selectedCoach;
    private int          currentWeek = 1;

    private List<Player> playerObjects = new ArrayList<>();
    private List<Coach>  coachObjects  = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCoachSelection();
        updatePreview();
    }


    public void setTeam(Team team) {
        this.currentTeam = team;
        refreshPlayerList();
        refreshCoachList();
    }


    public void setCurrentWeek(int week) {
        this.currentWeek = week;
        if (weekLabel != null) {
            weekLabel.setText("Week " + week);
        }
    }


    private void setupCoachSelection() {
        coachListView.getSelectionModel()
                .selectedIndexProperty()
                .addListener((obs, oldIdx, newIdx) -> {
                    int idx = newIdx.intValue();
                    if (idx >= 0 && idx < coachObjects.size()) {
                        selectedCoach = coachObjects.get(idx);
                        if (selectedCoachLabel != null) {
                            selectedCoachLabel.setText(selectedCoach.getName());
                        }
                        if (coachBonusLabel != null) {
                            coachBonusLabel.setText(
                                    "+" + selectedCoach.getTrainingBonus()
                                            + " Bonus  |  " + selectedCoach.getSpeciality());
                        }
                        updatePreview();
                    }
                });
    }


    @FXML
    public void updatePreview() {
        int bonus      = (selectedCoach != null) ? selectedCoach.getTrainingBonus() : 1;
        int drillCount = countSelectedDrills();
        int growth     = bonus * Math.max(drillCount, 1);

        if (expectedGrowthLabel != null) {
            expectedGrowthLabel.setText(
                    "Expected attribute growth: +" + growth + " Overall");
        }
    }

    @FXML
    public void startTraining() {
        if (currentTeam == null) {
            setStatus("No team loaded.");
            return;
        }
        if (countSelectedDrills() == 0) {
            setStatus("Please select at least one drill.");
            return;
        }

        List<Integer> selectedIndices =
                playerListView.getSelectionModel().getSelectedIndices();

        if (selectedIndices.isEmpty()) {
            setStatus("Please select at least one player.");
            return;
        }

        int trained = 0;
        for (int idx : selectedIndices) {
            if (idx >= 0 && idx < playerObjects.size()) {
                Player player = playerObjects.get(idx);
                if (player.isAvailable()) {
                    // Player interface üzerinden çağrı
                    // FootballPlayer ne yapacağını kendi bilir
                    player.train(selectedCoach);
                    trained++;
                }
            }
        }

        setStatus(trained + " player(s) trained successfully.");
        refreshPlayerList(); // Rating değişmiş olabilir
    }


    @FXML
    public void goBack() {
        setStatus("Returning to league view...");
    }


    private void refreshPlayerList() {
        if (currentTeam == null) return;
        playerObjects = new ArrayList<>(currentTeam.getPlayers());

        List<String> display = new ArrayList<>();
        for (Player p : playerObjects) {
            String status = p.isInjured()
                    ? " [INJURED]"
                    : (p.isAvailable() ? "" : " [TIRED]");
            display.add(p.getName()
                    + "  (" + p.getPosition() + ")"
                    + "  Rating: " + p.getOverallRating()
                    + status);
        }
        playerListView.setItems(FXCollections.observableArrayList(display));
        playerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void refreshCoachList() {
        if (currentTeam == null) return;
        coachObjects = new ArrayList<>(currentTeam.getCoaches());

        List<String> display = new ArrayList<>();
        for (Coach c : coachObjects) {
            display.add(c.getName()
                    + "  |  " + c.getSpeciality()
                    + "  |  +" + c.getTrainingBonus() + " bonus");
        }
        coachListView.setItems(FXCollections.observableArrayList(display));
    }

    private int countSelectedDrills() {
        int count = 0;
        if (chkShooting  != null && chkShooting.isSelected())  count++;
        if (chkDefensive != null && chkDefensive.isSelected()) count++;
        if (chkStamina   != null && chkStamina.isSelected())   count++;
        if (chkPassing   != null && chkPassing.isSelected())   count++;
        return count;
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }
}
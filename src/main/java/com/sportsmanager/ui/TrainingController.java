package com.sportsmanager.ui;

import com.sportsmanager.core.Coach;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Sport;
import com.sportsmanager.core.Team;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class TrainingController implements Initializable {

    private Sport sport;
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
    private List<Player> playerObjects = new ArrayList<>();
    private List<Coach>  coachObjects  = new ArrayList<>();

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCoachSelection();
        updatePreview();
    }

    public void setTeam(Team team) {
        this.currentTeam = team;
        refreshPlayerList();
        refreshCoachList();
        if (statusLabel != null && team != null) {
            statusLabel.setText("Training deck: " + team.getCrest() + " " + team.getName());
            UiStyles.applyAccentBadge(statusLabel, team.getAccentColor());
        } else if (statusLabel != null) {
            statusLabel.setText("");
            UiStyles.clearAccentBadge(statusLabel);
        }
    }

    public void setCurrentWeek(int week) {
        if (weekLabel != null) weekLabel.setText("Week " + week);
    }

    private void setupCoachSelection() {
        coachListView.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            int idx = n.intValue();
            if (idx >= 0 && idx < coachObjects.size()) {
                selectedCoach = coachObjects.get(idx);
                if (selectedCoachLabel != null)
                    selectedCoachLabel.setText(selectedCoach.getName());
                if (coachBonusLabel != null)
                    coachBonusLabel.setText("+" + selectedCoach.getTrainingBonus()
                            + " Bonus  |  " + selectedCoach.getSpeciality());
                updatePreview();
            }
        });
    }

    @FXML public void updatePreview() {
        int bonus  = (selectedCoach != null) ? selectedCoach.getTrainingBonus() : 1;
        int drills = countSelectedDrills();
        if (expectedGrowthLabel != null)
            expectedGrowthLabel.setText("Squad boost: +" + (bonus * Math.max(drills, 1)) + " Level Up");
    }

    @FXML public void startTraining() {
        if (currentTeam == null)         { setStatus("No team loaded.");                    return; }
        if (countSelectedDrills() == 0)  { setStatus("Please select at least one drill."); return; }

        List<Integer> sel = playerListView.getSelectionModel().getSelectedIndices();
        if (sel.isEmpty()) { setStatus("Please select at least one player."); return; }

        int trained = 0;
        for (int idx : sel) {
            if (idx >= 0 && idx < playerObjects.size()) {
                Player p = playerObjects.get(idx);
                if (p.isAvailable()) { p.train(selectedCoach); trained++; }
            }
        }
        
        if (trained > 0 && selectedCoach != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Training Session Complete");
            alert.setHeaderText("Squad Leveled Up");
            alert.setContentText(trained + " player(s) trained successfully by " + selectedCoach.getName() + ".");
            alert.showAndWait();
        }
        
        setStatus(trained + " player(s) trained successfully.");
        refreshPlayerList();
    }

    @FXML public void goBack() {
        if (sport == null) {
            setStatus("Returning...");
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
            setStatus("Could not return to league.");
        }
    }

    private void refreshPlayerList() {
        if (currentTeam == null) return;
        playerObjects = new ArrayList<>(currentTeam.getPlayers());
        List<String> display = new ArrayList<>();
        for (Player p : playerObjects) {
            String st = p.isInjured() ? " [INJURED]" : (p.isAvailable() ? "" : " [TIRED]");
            display.add(p.getName() + " (" + p.getPosition() + ")  Rating:" + p.getOverallRating()
                    + "  Stamina:" + p.getStamina() + st);
        }
        playerListView.setItems(FXCollections.observableArrayList(display));
        playerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void refreshCoachList() {
        if (currentTeam == null) return;
        coachObjects = new ArrayList<>(currentTeam.getCoaches());
        List<String> display = new ArrayList<>();
        for (Coach c : coachObjects)
            display.add(c.getName() + "  |  " + c.getSpeciality() + "  |  +" + c.getTrainingBonus() + " bonus");
        coachListView.setItems(FXCollections.observableArrayList(display));
    }

    private int countSelectedDrills() {
        int c = 0;
        if (chkShooting  != null && chkShooting.isSelected())  c++;
        if (chkDefensive != null && chkDefensive.isSelected()) c++;
        if (chkStamina   != null && chkStamina.isSelected())   c++;
        if (chkPassing   != null && chkPassing.isSelected())   c++;
        return c;
    }

    private void setStatus(String msg) { if (statusLabel != null) statusLabel.setText(msg); }
}

package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.Sport;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PreMatchController {

    @FXML private VBox previewCard;
    @FXML private Label sportBadgeLabel;
    @FXML private Label previewMoodLabel;
    @FXML private Label homeTeamLabel;
    @FXML private Label awayTeamLabel;
    @FXML private Label previewNarrativeLabel;
    @FXML private Label previewDetailLabel;
    @FXML private Label managedTeamLabel;
    @FXML private Label previewTaglineLabel;
    @FXML private Button simulateButton;

    private Match match;
    private Sport sport;
    private String managedTeamName;
    private boolean previewAnimationPlayed;

    public void setMatch(Match match) {
        this.match = match;
        previewAnimationPlayed = false;
        refreshPreview();
    }

    public void setSport(Sport sport) {
        this.sport = sport;
        refreshPreview();
    }

    public void setManagedTeamName(String managedTeamName) {
        this.managedTeamName = managedTeamName;
        refreshPreview();
    }

    @FXML
    private void handleSimulate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MatchView.fxml"));
            Stage stage = (Stage) simulateButton.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(), "Sports Manager - Live Match");
            MatchViewController mvc = loader.getController();
            mvc.setSport(sport);
            mvc.setMatch(match);
            mvc.setManagedTeamName(managedTeamName);
            stage.show();
            Platform.runLater(mvc::startMatch);
        } catch (Exception e) {
            showError("Could not open the live match screen.");
        }
    }

    private void refreshPreview() {
        if (match == null) {
            return;
        }

        homeTeamLabel.setText(match.getHomeTeam().getCrest() + " " + match.getHomeTeam().getName());
        awayTeamLabel.setText(match.getAwayTeam().getCrest() + " " + match.getAwayTeam().getName());

        if (sportBadgeLabel != null) {
            sportBadgeLabel.setText(buildSportBadge());
        }
        if (previewMoodLabel != null) {
            previewMoodLabel.setText(buildMoodLine());
        }
        if (previewNarrativeLabel != null) {
            previewNarrativeLabel.setText(buildNarrative());
        }
        if (previewDetailLabel != null) {
            previewDetailLabel.setText(buildDetail());
        }
        if (managedTeamLabel != null) {
            managedTeamLabel.setText("Managed club: " + (managedTeamName == null || managedTeamName.isBlank() ? "--" : managedTeamName));
        }
        if (previewTaglineLabel != null) {
            previewTaglineLabel.setText(buildTagline());
        }
        if (!previewAnimationPlayed && sport != null && managedTeamName != null) {
            playRevealAnimation();
            previewAnimationPlayed = true;
        }
    }

    private String buildSportBadge() {
        String sportName = sport != null ? sport.getSportName() : "Match";
        return sportName.toUpperCase() + " BROADCAST";
    }

    private String buildMoodLine() {
        if (sport == null) {
            return "You can feel the crowd building";
        }
        if (isBasketball()) {
            return "The floor is about to get loud";
        }
        return "The tunnel is buzzing before kickoff";
    }

    private String buildNarrative() {
        String home = match.getHomeTeam().getName();
        String away = match.getAwayTeam().getName();
        String managed = managedTeamName == null || managedTeamName.isBlank() ? "your club" : managedTeamName;
        if (isBasketball()) {
            return managed + ". " + home + " vs " + away + ". Let's go.";
        }
        return managed + " takes the field. " + home + " meets " + away + ". Your move.";
    }

    private String buildDetail() {
        if (sport == null) {
            return "2 halves / 45 minutes each";
        }
        if (isBasketball()) {
            return "4 quarters / 12 minutes each";
        }
        return "2 halves / 45 minutes each";
    }

    private String buildTagline() {
        if (managedTeamName == null || managedTeamName.isBlank()) {
            return isBasketball() ? "Tip-off energy is loading." : "Kickoff energy is loading.";
        }
        if (managedTeamName.equalsIgnoreCase(match.getHomeTeam().getName()) || managedTeamName.equalsIgnoreCase(match.getAwayTeam().getName())) {
            return "Your club is under the lights.";
        }
        return isBasketball() ? "Tip-off energy is loading." : "Kickoff energy is loading.";
    }

    private boolean isBasketball() {
        return sport != null && "Basketball".equalsIgnoreCase(sport.getSportName());
    }

    private void playRevealAnimation() {
        if (previewCard == null) {
            return;
        }
        previewCard.setOpacity(1.0);
        previewCard.setScaleX(0.97);
        previewCard.setScaleY(0.97);

        FadeTransition fade = new FadeTransition(Duration.millis(320), previewCard);
        fade.setFromValue(0.88);
        fade.setToValue(1.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(320), previewCard);
        scale.setFromX(0.97);
        scale.setFromY(0.97);
        scale.setToX(1.0);
        scale.setToY(1.0);

        fade.play();
        scale.play();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sports Manager");
        alert.setHeaderText("Navigation error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

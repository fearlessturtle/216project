package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.Sport;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class PostMatchController {

    @FXML private VBox resultCard;
    @FXML private Label resultBannerLabel;
    @FXML private Label resultNarrativeLabel;
    @FXML private Label homeTeamLabel;
    @FXML private Label homeScoreLabel;
    @FXML private Label awayTeamLabel;
    @FXML private Label awayScoreLabel;
    @FXML private HBox resultStatRow;
    @FXML private Button backButton;

    private Match match;
    private Sport sport;
    private String managedTeamName;

    @FXML
    private void initialize() {
        if (resultCard != null) {
            resultCard.setMaxHeight(Region.USE_PREF_SIZE);
        }
    }

    public void setMatch(Match match) {
        this.match = match;
        refreshResult();
        buildStats();
        playRevealAnimation();
    }

    private void refreshResult() {
        if (match == null) {
            return;
        }
        homeTeamLabel.setText(match.getHomeTeam().getCrest() + " " + match.getHomeTeam().getName());
        homeScoreLabel.setText(String.valueOf(match.getHomeScore()));
        awayTeamLabel.setText(match.getAwayTeam().getCrest() + " " + match.getAwayTeam().getName());
        awayScoreLabel.setText(String.valueOf(match.getAwayScore()));
        if (resultBannerLabel != null) {
            resultBannerLabel.setText(buildBanner());
        }
        if (resultNarrativeLabel != null) {
            resultNarrativeLabel.setText(buildNarrative());
        }
        applyTone();
    }

    public void setSport(Sport sport) {
        this.sport = sport;
        refreshResult();
        buildStats();
    }

    public void setManagedTeamName(String managedTeamName) {
        this.managedTeamName = managedTeamName;
        refreshResult();
    }

    @FXML
    private void handleBackToMenu() {
        try {
            String targetView = sport != null ? "LeagueView.fxml" : "MainMenu.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(targetView));
            Stage stage = (Stage) backButton.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(),
                    sport != null ? "Sports Manager - " + sport.getSportName() + " Control Room" : "Sports Manager - Main Menu");
            if (sport != null) {
                LeagueViewController controller = loader.getController();
                controller.setSport(sport);
                controller.setManagedTeamName(managedTeamName);
            }
            stage.show();
        } catch (Exception e) {
            showError("Could not return to the menu.");
        }
    }

    private void buildStats() {
        if (resultStatRow == null || match == null) {
            return;
        }

        resultStatRow.getChildren().clear();
        resultStatRow.getChildren().add(createChip(getScoringLabel(), String.valueOf(countEvents(MatchEvent.EventType.GOAL)), "result-chip-goal"));
        resultStatRow.getChildren().add(createChip(getCardLabel(), String.valueOf(countEvents(MatchEvent.EventType.YELLOW_CARD) + countEvents(MatchEvent.EventType.RED_CARD)), "result-chip-card"));
        resultStatRow.getChildren().add(createChip("Injuries", String.valueOf(countEvents(MatchEvent.EventType.INJURY)), "result-chip-injury"));
        resultStatRow.getChildren().add(createChip("Subs", String.valueOf(countEvents(MatchEvent.EventType.SUBSTITUTION)), "result-chip-sub"));
    }

    private Label createChip(String label, String value, String styleClass) {
        Label chip = new Label(label + " " + value);
        chip.getStyleClass().addAll("result-chip", styleClass);
        return chip;
    }

    private int countEvents(MatchEvent.EventType type) {
        if (match == null) {
            return 0;
        }
        int count = 0;
        for (MatchEvent event : match.getMatchEvents()) {
            if (event != null && event.getType() == type) {
                count++;
            }
        }
        return count;
    }

    private String getScoringLabel() {
        return isBasketball() ? "Scores" : "Goals";
    }

    private String getCardLabel() {
        return isBasketball() ? "Fouls" : "Cards";
    }

    private String buildBanner() {
        if (match == null) {
            return "VICTORY";
        }
        if (match.getHomeScore() == match.getAwayScore()) {
            return "ALL SQUARE";
        }
        int diff = Math.abs(match.getHomeScore() - match.getAwayScore());
        String winner = match.getHomeScore() > match.getAwayScore()
                ? match.getHomeTeam().getName()
                : match.getAwayTeam().getName();
        boolean closeFinish = diff <= (isBasketball() ? 3 : 1);
        if (managedTeamName == null || managedTeamName.isBlank()) {
            return closeFinish ? "DOWN TO THE WIRE" : winner + " TAKE IT";
        }
        if (managedTeamName.equalsIgnoreCase(winner)) {
            return closeFinish ? "BUZZER THRILLER" : "YOUR CLUB WINS";
        }
        return closeFinish ? "A HEARTBREAKER" : "YOUR CLUB FALLS SHORT";
    }

    private String buildNarrative() {
        if (match == null) {
            return "The final whistle is loading.";
        }

        int diff = Math.abs(match.getHomeScore() - match.getAwayScore());
        String sportLine;
        if (isBasketball()) {
            sportLine = diff <= 5
                    ? "That came down to the wire. Last basket was everything."
                    : "Ran them off the court. It wasn't even close.";
        } else {
            sportLine = diff <= 1
                    ? "One goal. That was it. One moment changed everything."
                    : "Totally outplayed them. They never had a shot.";
        }

        String lastMoment = findLastMoment();
        if (!lastMoment.isBlank()) {
            return sportLine + " Final moment: " + lastMoment;
        }
        return sportLine;
    }

    private String findLastMoment() {
        if (match == null) {
            return "";
        }
        List<MatchEvent> events = match.getMatchEvents();
        for (int i = events.size() - 1; i >= 0; i--) {
            MatchEvent event = events.get(i);
            if (event == null) {
                continue;
            }
            if (event.getType() == MatchEvent.EventType.GOAL
                    || event.getType() == MatchEvent.EventType.INJURY
                    || event.getType() == MatchEvent.EventType.RED_CARD
                    || event.getType() == MatchEvent.EventType.SUBSTITUTION) {
                StringBuilder builder = new StringBuilder();
                builder.append(event.getMinute()).append("' ");
                builder.append(event.getType() == MatchEvent.EventType.GOAL && isBasketball() ? "SCORE" : event.getType());
                if (event.getPlayer() != null && !event.getPlayer().isBlank()) {
                    builder.append(" ").append(event.getPlayer());
                }
                if (event.getTeam() != null && !event.getTeam().isBlank()) {
                    builder.append(" (").append(event.getTeam()).append(")");
                }
                if (event.getDescription() != null && !event.getDescription().isBlank()) {
                    builder.append(" - ").append(event.getDescription());
                }
                return builder.toString();
            }
        }
        return "";
    }

    private void applyTone() {
        if (resultCard == null || match == null) {
            return;
        }
        resultCard.getStyleClass().removeAll("result-card-home", "result-card-away", "result-card-draw");
        if (match.getHomeScore() == match.getAwayScore()) {
            resultCard.getStyleClass().add("result-card-draw");
            return;
        }
        boolean homeWins = match.getHomeScore() > match.getAwayScore();
        if (homeWins) {
            resultCard.getStyleClass().add("result-card-home");
        } else {
            resultCard.getStyleClass().add("result-card-away");
        }
    }

    private boolean isBasketball() {
        return sport != null && "Basketball".equalsIgnoreCase(sport.getSportName());
    }

    private void playRevealAnimation() {
        if (resultCard == null) {
            return;
        }

        resultCard.setScaleX(0.97);
        resultCard.setScaleY(0.97);

        FadeTransition fade = new FadeTransition(Duration.millis(320), resultCard);
        fade.setFromValue(0.9);
        fade.setToValue(1.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(320), resultCard);
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

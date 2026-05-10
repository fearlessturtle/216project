package com.sportsmanager.ui;

import com.sportsmanager.core.Match;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.MatchObserver;
import com.sportsmanager.core.Sport;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatchViewController implements MatchObserver {

    @FXML private HBox highlightFeed;
    @FXML private ScrollPane digestScrollPane;
    @FXML private VBox eventFeed;
    @FXML private ScrollPane feedScrollPane;
    @FXML private TabPane matchTabPane;
    @FXML private Label matchPulseLabel;
    @FXML private Label momentumLabel;
    @FXML private Label homeScoreLabel;
    @FXML private Label awayScoreLabel;
    @FXML private Button continueButton;

    private Match match;
    private Sport sport;
    private String managedTeamName;
    private boolean matchStarted;
    private final Map<Integer, TimelineGroup> timelineGroups = new LinkedHashMap<>();
    private final Map<Integer, TimelineSnapshot> highlightSnapshots = new LinkedHashMap<>();
    private final List<Integer> highlightOrder = new ArrayList<>();

    public void setSport(Sport sport) {
        this.sport = sport;
        refreshBoard();
        refreshPulse();
        refreshMomentum();
    }

    public void setMatch(Match match) {
        this.match = match;
        this.matchStarted = false;
        resetFeed();
        if (matchTabPane != null) {
            matchTabPane.getSelectionModel().select(0);
        }
        if (highlightFeed != null) {
            highlightFeed.setManaged(true);
            highlightFeed.setVisible(true);
        }
        if (feedScrollPane != null) {
            feedScrollPane.setManaged(true);
            feedScrollPane.setVisible(true);
        }
        refreshBoard();
        refreshPulse();
        refreshMomentum();
        if (continueButton != null) {
            continueButton.setDisable(true);
        }
        if (match != null) {
            match.addObserver(this);
        }
    }

    public void setManagedTeamName(String managedTeamName) {
        this.managedTeamName = managedTeamName;
        refreshPulse();
        refreshMomentum();
    }

    public void startMatch() {
        if (match == null || matchStarted) {
            return;
        }

        matchStarted = true;
        if (eventFeed != null && eventFeed.getChildren().isEmpty()) {
            addIntroCards();
        }
        if (highlightFeed != null && highlightFeed.getChildren().isEmpty()) {
            addIntroHighlights();
        }

        Thread thread = new Thread(() -> {
            if (sport != null) {
                sport.playMatch(match);
            } else {
                match.play();
            }
            Platform.runLater(() -> {
                if (continueButton != null) {
                    continueButton.setDisable(false);
                }
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void onEvent(MatchEvent event) {
        Platform.runLater(() -> {
            if (event == null) {
                return;
            }
            refreshBoard();
            refreshPulse(event);
            refreshMomentum(event);
            addTimelineEvent(event);
            addTimelineHighlight(event);
            if (event.getType() == MatchEvent.EventType.MATCH_END && continueButton != null) {
                continueButton.setDisable(false);
            }
        });
    }

    @FXML
    private void handleContinue() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PostMatch.fxml"));
            Stage stage = (Stage) continueButton.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(), "Sports Manager - Result Board");
            PostMatchController controller = loader.getController();
            controller.setMatch(match);
            controller.setSport(sport);
            controller.setManagedTeamName(managedTeamName);
            stage.show();
        } catch (Exception e) {
            showError("Could not open the result screen.");
        }
    }

    private void resetFeed() {
        if (eventFeed != null) {
            eventFeed.getChildren().clear();
        }
        if (highlightFeed != null) {
            highlightFeed.getChildren().clear();
        }
        timelineGroups.clear();
        highlightSnapshots.clear();
        highlightOrder.clear();
        if (matchPulseLabel != null) {
            matchPulseLabel.setText(buildIdlePulse());
        }
        if (momentumLabel != null) {
            momentumLabel.setText(buildMomentumLine(null));
        }
    }

    private void addIntroCards() {
        addBroadcastCard("Broadcast ready", buildIntroHeadline(), "event-card-period");
        addBroadcastCard("Match line", buildIntroLine(), "event-card-substitution");
    }

    private void addIntroHighlights() {
        addHighlightSnapshot("Broadcast ready", buildIntroHeadline(), "timeline-group-period", "Live", "Opening snapshot");
        addHighlightSnapshot("Match line", buildIntroLine(), "timeline-group-substitution", "Live", "Opening snapshot");
    }

    private void addTimelineEvent(MatchEvent event) {
        if (eventFeed == null) {
            return;
        }

        TimelineGroup group = timelineGroups.get(event.getMinute());
        if (group == null) {
            group = createTimelineGroup(event.getMinute());
            timelineGroups.put(event.getMinute(), group);
            eventFeed.getChildren().add(group.card);
            animateEntry(group.card);
        }

        group.addEvent(event);
        appendTimelineRow(group, event);
        refreshTimelineGroup(group);
        scrollToBottom();
    }

    private void addTimelineHighlight(MatchEvent event) {
        if (highlightFeed == null) {
            return;
        }

        TimelineGroup group = timelineGroups.get(event.getMinute());
        TimelineSnapshot snapshot = highlightSnapshots.get(event.getMinute());
        if (snapshot == null) {
            snapshot = createTimelineSnapshot(event.getMinute());
            highlightSnapshots.put(event.getMinute(), snapshot);
            animateEntry(snapshot.card);
            highlightOrder.add(event.getMinute());
        }

        if (!highlightFeed.getChildren().contains(snapshot.card)) {
            highlightFeed.getChildren().add(snapshot.card);
        }
        if (!highlightOrder.contains(event.getMinute())) {
            highlightOrder.add(event.getMinute());
        }

        if (group == null) {
            group = buildStandaloneGroup(event);
        }

        refreshTimelineSnapshot(snapshot, group);
        trimHighlightRail();
        scrollDigestToEnd();
    }

    private void addBroadcastCard(String titleText, String bodyText, String styleClass) {
        if (eventFeed == null) {
            return;
        }

        VBox card = new VBox(4);
        card.getStyleClass().addAll("event-card", styleClass);
        card.setMaxWidth(Double.MAX_VALUE);

        Label title = new Label(titleText);
        title.getStyleClass().add("event-title");

        Label body = new Label(bodyText);
        body.getStyleClass().add("event-summary");
        body.setWrapText(true);

        card.getChildren().addAll(title, body);
        eventFeed.getChildren().add(card);
        animateEntry(card);
        scrollToBottom();
    }

    private void addHighlightSnapshot(String titleText, String bodyText, String styleClass, String minuteText, String countText) {
        if (highlightFeed == null) {
            return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().addAll("highlight-card", styleClass);
        card.setMaxWidth(260);
        card.setMinWidth(260);

        HBox header = new HBox(10);
        Label minute = new Label(minuteText);
        minute.getStyleClass().add("timeline-minute-badge");

        Label count = new Label(countText);
        count.getStyleClass().add("timeline-count-chip");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(minute, spacer, count);

        Label title = new Label(titleText);
        title.getStyleClass().add("timeline-burst-label");
        title.setWrapText(true);

        Label body = new Label(bodyText);
        body.getStyleClass().add("timeline-row-summary");
        body.setWrapText(true);

        card.getChildren().addAll(header, title, body);
        highlightFeed.getChildren().add(card);
        animateEntry(card);
    }

    private TimelineGroup createTimelineGroup(int minute) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("timeline-group", "timeline-group-period");
        card.setMaxWidth(Double.MAX_VALUE);

        HBox header = new HBox(10);
        header.getStyleClass().add("timeline-group-header");

        Label minuteLabel = new Label(formatMinute(minute));
        minuteLabel.getStyleClass().add("timeline-minute-badge");

        Label headlineLabel = new Label("MATCH SNAPSHOT");
        headlineLabel.getStyleClass().add("timeline-burst-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label("1 moment");
        countLabel.getStyleClass().add("timeline-count-chip");

        header.getChildren().addAll(minuteLabel, headlineLabel, spacer, countLabel);

        Label captionLabel = new Label();
        captionLabel.getStyleClass().add("timeline-group-caption");
        captionLabel.setWrapText(true);
        captionLabel.setManaged(false);
        captionLabel.setVisible(false);

        VBox rowsBox = new VBox(8);
        rowsBox.getStyleClass().add("timeline-rows");
        rowsBox.setFillWidth(true);

        card.getChildren().addAll(header, captionLabel, rowsBox);
        return new TimelineGroup(minute, card, rowsBox, headlineLabel, captionLabel, countLabel);
    }

    private void appendTimelineRow(TimelineGroup group, MatchEvent event) {
        HBox row = new HBox(12);
        row.getStyleClass().addAll("event-card", styleClassFor(event), "timeline-row");
        row.setMaxWidth(Double.MAX_VALUE);

        Label type = new Label(formatTitle(event));
        type.getStyleClass().addAll("timeline-event-type", timelineEventTypeClassFor(event));

        VBox textBlock = new VBox(2);
        textBlock.setFillWidth(true);
        textBlock.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textBlock, Priority.ALWAYS);

        Label summary = new Label(formatSummary(event));
        summary.getStyleClass().add("timeline-row-summary");
        summary.setWrapText(true);

        textBlock.getChildren().add(summary);

        String detailText = formatDetail(event);
        if (!detailText.isBlank()) {
            Label detail = new Label(detailText);
            detail.getStyleClass().add("timeline-row-detail");
            detail.setWrapText(true);
            textBlock.getChildren().add(detail);
        }

        row.getChildren().addAll(type, textBlock);
        group.rowsBox.getChildren().add(row);
        animateEntry(row);
    }

    private void refreshTimelineGroup(TimelineGroup group) {
        group.card.getStyleClass().setAll("timeline-group", timelineGroupClassFor(group));

        int count = group.events.size();
        group.headlineLabel.setText(buildTimelineHeadline(group));
        group.countLabel.setText(formatMomentCount(count));

        boolean showCaption = count > 1;
        group.captionLabel.setManaged(showCaption);
        group.captionLabel.setVisible(showCaption);
        if (showCaption) {
            group.captionLabel.setText(buildTimelineCaption(group));
        } else {
            group.captionLabel.setText("");
        }
    }

    private TimelineSnapshot createTimelineSnapshot(int minute) {
        VBox card = new VBox(10);
        card.getStyleClass().add("highlight-card");
        card.setMaxWidth(260);
        card.setMinWidth(260);

        HBox header = new HBox(10);
        Label minuteLabel = new Label(formatMinute(minute));
        minuteLabel.getStyleClass().add("timeline-minute-badge");

        Label countLabel = new Label("1 moment");
        countLabel.getStyleClass().add("timeline-count-chip");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(minuteLabel, spacer, countLabel);

        Label headlineLabel = new Label();
        headlineLabel.getStyleClass().add("timeline-burst-label");
        headlineLabel.setWrapText(true);

        Label captionLabel = new Label();
        captionLabel.getStyleClass().add("timeline-row-summary");
        captionLabel.setWrapText(true);

        card.getChildren().addAll(header, headlineLabel, captionLabel);
        return new TimelineSnapshot(card, headlineLabel, captionLabel, countLabel);
    }

    private void refreshTimelineSnapshot(TimelineSnapshot snapshot, TimelineGroup group) {
        if (snapshot == null || group == null) {
            return;
        }
        snapshot.card.getStyleClass().setAll("highlight-card", timelineGroupClassFor(group));
        snapshot.headlineLabel.setText(buildTimelineHeadline(group));
        snapshot.captionLabel.setText(buildTimelineCaption(group));
        snapshot.countLabel.setText(formatMomentCount(group.events.size()));
    }

    private void trimHighlightRail() {
        while (highlightOrder.size() > 5) {
            Integer oldestMinute = highlightOrder.remove(0);
            TimelineSnapshot oldestSnapshot = highlightSnapshots.get(oldestMinute);
            if (oldestSnapshot != null) {
                highlightFeed.getChildren().remove(oldestSnapshot.card);
            }
        }
    }

    private TimelineGroup buildStandaloneGroup(MatchEvent event) {
        VBox card = new VBox();
        VBox rows = new VBox();
        Label headline = new Label();
        Label caption = new Label();
        Label count = new Label();
        TimelineGroup group = new TimelineGroup(event.getMinute(), card, rows, headline, caption, count);
        group.addEvent(event);
        return group;
    }

    private String buildIdlePulse() {
        if (sport == null) {
            return "You are on the sideline as the broadcast warms up.";
        }
        return isBasketball()
                ? "Tip-off is loading. The floor is about to get loud."
                : "Kickoff is loading. The atmosphere is ready to crack open.";
    }

    private String buildIntroHeadline() {
        if (match == null) {
            return "The stadium is waiting.";
        }
        String home = match.getHomeTeam().getName();
        String away = match.getAwayTeam().getName();
        return home + " vs " + away + " is locked in.";
    }

    private String buildIntroLine() {
        if (sport == null || match == null) {
            return "The opening sequence is about to begin.";
        }
        String home = match.getHomeTeam().getName();
        String away = match.getAwayTeam().getName();
        if (managedTeamName != null && !managedTeamName.isBlank()) {
            if (managedTeamName.equalsIgnoreCase(home)) {
                return "You are behind " + home + " as they defend home turf against " + away + ".";
            }
            if (managedTeamName.equalsIgnoreCase(away)) {
                return "You are behind " + away + " as they attack the road crowd in " + home + ".";
            }
        }
        if (isBasketball()) {
            return "You watch " + home + " and " + away + " trade runs in a fast, high-scoring night.";
        }
        return "You watch " + home + " and " + away + " brace for a tense, one-moment swing.";
    }

    private String formatMinute(int minute) {
        return minute + "'";
    }

    private String formatTitle(MatchEvent event) {
        if (event == null) {
            return "MOMENT";
        }
        return switch (event.getType()) {
            case GOAL -> isBasketball() ? "SCORE!" : "GOAL!";
            case YELLOW_CARD -> isBasketball() ? "FOUL!" : "YELLOW CARD!";
            case RED_CARD -> "RED CARD!";
            case SUBSTITUTION -> "SUBSTITUTION";
            case INJURY -> "INJURY";
            case PERIOD_END -> isBasketball() ? "QUARTER END" : "HALF-TIME";
            case MATCH_END -> "FULL TIME";
        };
    }

    private String formatSummary(MatchEvent event) {
        if (event == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (event.getPlayer() != null && !event.getPlayer().isBlank()) {
            builder.append(event.getPlayer());
        }
        if (event.getTeam() != null && !event.getTeam().isBlank()) {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(event.getTeam());
        }
        if (builder.length() == 0) {
            builder.append("The match story keeps moving.");
        }
        return builder.toString();
    }

    private String formatDetail(MatchEvent event) {
        if (event == null || event.getDescription() == null || event.getDescription().isBlank()) {
            return "";
        }
        return event.getDescription();
    }

    private String timelineGroupClassFor(TimelineGroup group) {
        if (group == null || group.events.isEmpty()) {
            return "timeline-group-period";
        }

        if (containsType(group, MatchEvent.EventType.MATCH_END)) {
            return "timeline-group-final";
        }
        if (containsType(group, MatchEvent.EventType.GOAL)) {
            return "timeline-group-goal";
        }
        if (containsType(group, MatchEvent.EventType.INJURY)) {
            return "timeline-group-injury";
        }
        if (containsType(group, MatchEvent.EventType.YELLOW_CARD) || containsType(group, MatchEvent.EventType.RED_CARD)) {
            return "timeline-group-card";
        }
        if (containsType(group, MatchEvent.EventType.SUBSTITUTION)) {
            return "timeline-group-substitution";
        }
        return "timeline-group-period";
    }

    private String timelineEventTypeClassFor(MatchEvent event) {
        if (event == null) {
            return "timeline-event-type-period";
        }
        return switch (event.getType()) {
            case GOAL -> "timeline-event-type-goal";
            case YELLOW_CARD, RED_CARD -> "timeline-event-type-card";
            case SUBSTITUTION -> "timeline-event-type-substitution";
            case INJURY -> "timeline-event-type-injury";
            case PERIOD_END -> "timeline-event-type-period";
            case MATCH_END -> "timeline-event-type-final";
        };
    }

    private String buildTimelineHeadline(TimelineGroup group) {
        if (group == null || group.events.isEmpty()) {
            return "MATCH SNAPSHOT";
        }
        if (containsType(group, MatchEvent.EventType.MATCH_END)) {
            return "FULL TIME";
        }
        if (containsType(group, MatchEvent.EventType.PERIOD_END)) {
            return isBasketball() ? "QUARTER TURN" : "HALF-TIME TURN";
        }

        int goalCount = countType(group, MatchEvent.EventType.GOAL);
        int cardCount = countType(group, MatchEvent.EventType.YELLOW_CARD) + countType(group, MatchEvent.EventType.RED_CARD);
        int substitutionCount = countType(group, MatchEvent.EventType.SUBSTITUTION);
        int injuryCount = countType(group, MatchEvent.EventType.INJURY);
        int total = group.events.size();

        if (goalCount >= 2) {
            return "SCORING BURST";
        }
        if (goalCount >= 1 && (cardCount + injuryCount) >= 1) {
            return "HIGH-DRAMA MINUTE";
        }
        if (cardCount >= 2) {
            return "DISCIPLINE CHECK";
        }
        if (injuryCount >= 1) {
            return "MEDICAL ALERT";
        }
        if (substitutionCount >= 2) {
            return "BENCH WAVE";
        }
        if (goalCount >= 1) {
            return isBasketball() ? "SCORE WINDOW" : "GOAL WINDOW";
        }
        if (cardCount == 1) {
            return "PRESSURE CHECK";
        }
        if (substitutionCount == 1) {
            return "TACTICAL SWAP";
        }
        if (injuryCount == 1) {
            return "MEDICAL CHECK";
        }
        if (group.minute >= 45 || (isBasketball() && group.minute >= 40)) {
            return total > 1 ? "LATE DRAMA" : "FINAL STRETCH";
        }
        if (group.minute >= 30) {
            return "MATCH PULSE";
        }
        return "MATCH SNAPSHOT";
    }

    private String buildTimelineCaption(TimelineGroup group) {
        if (group == null || group.events.isEmpty()) {
            return "";
        }
        int count = group.events.size();
        return count + " " + (count == 1 ? "moment" : "moments") + " condensed into the " + formatMinute(group.minute) + " window.";
    }

    private String formatMomentCount(int count) {
        return count + " " + (count == 1 ? "moment" : "moments");
    }

    private boolean containsType(TimelineGroup group, MatchEvent.EventType type) {
        return countType(group, type) > 0;
    }

    private int countType(TimelineGroup group, MatchEvent.EventType type) {
        if (group == null || type == null) {
            return 0;
        }
        int count = 0;
        for (MatchEvent event : group.events) {
            if (event != null && event.getType() == type) {
                count++;
            }
        }
        return count;
    }

    private String styleClassFor(MatchEvent event) {
        if (event == null) {
            return "event-card-period";
        }
        return switch (event.getType()) {
            case GOAL -> "event-card-goal";
            case YELLOW_CARD, RED_CARD -> "event-card-card";
            case SUBSTITUTION -> "event-card-substitution";
            case INJURY -> "event-card-injury";
            case PERIOD_END -> "event-card-period";
            case MATCH_END -> "event-card-final";
        };
    }

    private String buildPulseLine(MatchEvent event) {
        if (event == null) {
            return buildIdlePulse();
        }
        String scoreLine = refreshScoreText();
        return switch (event.getType()) {
            case GOAL -> (isBasketball() ? "Score!" : "Goal!") + " " + formatSummary(event) + " | " + scoreLine;
            case YELLOW_CARD -> isBasketball()
                    ? "You feel the foul pressure rise. " + scoreLine
                    : "You can feel the tempers flare. " + scoreLine;
            case RED_CARD -> "A send-off changes everything. " + scoreLine;
            case SUBSTITUTION -> "Fresh legs are entering the contest. " + scoreLine;
            case INJURY -> "Medical staff are rushing toward the sideline. " + scoreLine;
            case PERIOD_END -> isBasketball()
                    ? "Quarter break. You wait for the next run."
                    : "Half-time. You wait for the next tactical swing.";
            case MATCH_END -> "The final whistle is in. " + scoreLine;
        };
    }

    private String buildMomentumLine(MatchEvent event) {
        if (match == null) {
            return "Broadcast warming up.";
        }

        String scoreLine = refreshScoreText();

        if (event != null) {
            return switch (event.getType()) {
                case GOAL -> isBasketball()
                        ? "The scoreboard just jumped. " + scoreLine
                        : "The place is going nuts. " + scoreLine;
                case YELLOW_CARD -> isBasketball()
                        ? "Foul trouble. Watch it. " + scoreLine
                        : "Getting heated now. " + scoreLine;
                case RED_CARD -> "That changes everything. " + scoreLine;
                case SUBSTITUTION -> "Fresh players on. " + scoreLine;
                case INJURY -> "Someone's down. Backup time. " + scoreLine;
                case PERIOD_END -> isBasketball()
                        ? "Quarter break. Regroup and go."
                        : "Half-time. Time to adjust.";
                case MATCH_END -> "The whistle's coming.";
            };
        }

        int diff = Math.abs(match.getHomeScore() - match.getAwayScore());
        if (diff <= 2) {
            return isBasketball()
                    ? "One run. That's all it takes."
                    : "One goal. Game changer.";
        }

        if (managedTeamName != null && !managedTeamName.isBlank()) {
            boolean managedHome = managedTeamName.equalsIgnoreCase(match.getHomeTeam().getName());
            boolean managedAway = managedTeamName.equalsIgnoreCase(match.getAwayTeam().getName());
            if (managedHome || managedAway) {
                boolean leading = managedHome
                        ? match.getHomeScore() > match.getAwayScore()
                        : match.getAwayScore() > match.getHomeScore();
                if (leading) {
                    return managedTeamName + " is riding the momentum.";
                }
                if (match.getHomeScore() == match.getAwayScore()) {
                    return managedTeamName + " is right in the fight.";
                }
                return managedTeamName + " needs a response.";
            }
        }

        if (match.getHomeScore() == match.getAwayScore()) {
            return "Locked level. Every possession matters.";
        }

        return match.getHomeScore() > match.getAwayScore()
                ? "The lead is starting to stretch."
                : "The chase is becoming urgent.";
    }

    private void refreshMomentum() {
        if (momentumLabel != null) {
            momentumLabel.setText(buildMomentumLine(null));
        }
    }

    private void refreshMomentum(MatchEvent event) {
        if (momentumLabel != null) {
            momentumLabel.setText(buildMomentumLine(event));
        }
    }

    private void refreshBoard() {
        if (match == null) {
            if (homeScoreLabel != null) {
                homeScoreLabel.setText("0");
            }
            if (awayScoreLabel != null) {
                awayScoreLabel.setText("0");
            }
            return;
        }

        if (homeScoreLabel != null) {
            homeScoreLabel.setText(String.valueOf(match.getHomeScore()));
        }
        if (awayScoreLabel != null) {
            awayScoreLabel.setText(String.valueOf(match.getAwayScore()));
        }
    }

    private String refreshScoreText() {
        if (match == null) {
            return "0-0";
        }
        return match.getHomeScore() + "-" + match.getAwayScore();
    }

    private void refreshPulse() {
        if (matchPulseLabel != null) {
            matchPulseLabel.setText(buildIdlePulse());
        }
    }

    private void refreshPulse(MatchEvent event) {
        if (matchPulseLabel != null) {
            matchPulseLabel.setText(buildPulseLine(event));
        }
    }

    private boolean isBasketball() {
        return sport != null && "Basketball".equalsIgnoreCase(sport.getSportName());
    }

    private void animateEntry(Node node) {
        node.setOpacity(0.0);
        node.setScaleX(0.98);
        node.setScaleY(0.98);

        FadeTransition fade = new FadeTransition(Duration.millis(220), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(220), node);
        scale.setFromX(0.98);
        scale.setFromY(0.98);
        scale.setToX(1.0);
        scale.setToY(1.0);

        fade.play();
        scale.play();
    }

    private void scrollToBottom() {
        if (feedScrollPane == null) {
            return;
        }
        Platform.runLater(() -> feedScrollPane.setVvalue(1.0));
    }

    private void scrollDigestToEnd() {
        if (digestScrollPane == null) {
            return;
        }
        Platform.runLater(() -> digestScrollPane.setHvalue(1.0));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sports Manager");
        alert.setHeaderText("Navigation error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static final class TimelineGroup {
        private final int minute;
        private final VBox card;
        private final VBox rowsBox;
        private final Label headlineLabel;
        private final Label captionLabel;
        private final Label countLabel;
        private final List<MatchEvent> events = new ArrayList<>();

        private TimelineGroup(int minute, VBox card, VBox rowsBox, Label headlineLabel, Label captionLabel, Label countLabel) {
            this.minute = minute;
            this.card = card;
            this.rowsBox = rowsBox;
            this.headlineLabel = headlineLabel;
            this.captionLabel = captionLabel;
            this.countLabel = countLabel;
        }

        private void addEvent(MatchEvent event) {
            if (event != null) {
                events.add(event);
            }
        }
    }

    private static final class TimelineSnapshot {
        private final VBox card;
        private final Label headlineLabel;
        private final Label captionLabel;
        private final Label countLabel;

        private TimelineSnapshot(VBox card, Label headlineLabel, Label captionLabel, Label countLabel) {
            this.card = card;
            this.headlineLabel = headlineLabel;
            this.captionLabel = captionLabel;
            this.countLabel = countLabel;
        }
    }
}

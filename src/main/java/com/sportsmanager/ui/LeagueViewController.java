package com.sportsmanager.ui;

import com.sportsmanager.core.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * LeagueViewController manages the primary management dashboard during a season.
 * Displays standings, next fixture, and provides navigation for match play.
 * 
 * This controller only depends on core package interfaces (Sport, Team, Match, League).
 * It has NO dependency on concrete football classes.
 */
public class LeagueViewController implements Initializable {

    private Sport sport;

    @FXML
    private Label seasonLabel;

    @FXML
    private TableView<TeamStanding> standingsTable;
    
    @FXML
    private TableColumn<TeamStanding, String> teamColumn;
    
    @FXML
    private TableColumn<TeamStanding, Integer> playedColumn;
    
    @FXML
    private TableColumn<TeamStanding, Integer> winsColumn;
    
    @FXML
    private TableColumn<TeamStanding, Integer> drawsColumn;
    
    @FXML
    private TableColumn<TeamStanding, Integer> lossesColumn;
    
    @FXML
    private TableColumn<TeamStanding, Integer> pointsColumn;
    
    @FXML
    private TableColumn<TeamStanding, Integer> goalDifferenceColumn;

    @FXML
    private Label nextFixtureLabel;

    @FXML
    private Label nextOpponentLabel;

    @FXML
    private Button proceedButton;

    @FXML
    private Button trainingButton;

    @FXML
    private Label weekLabel;

    /**
     * Sets the Sport object that this controller will manage.
     * Must be called before the UI is displayed.
     * 
     * @param sport The Sport interface implementation
     */
    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeStandingsTable();
        setupEventHandlers();
    }

    /**
     * Initialize the standings table with columns and cell value factories.
     * Uses only core interface methods - no knowledge of concrete implementations.
     */
    private void initializeStandingsTable() {
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        playedColumn.setCellValueFactory(new PropertyValueFactory<>("gamesPlayed"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        drawsColumn.setCellValueFactory(new PropertyValueFactory<>("draws"));
        lossesColumn.setCellValueFactory(new PropertyValueFactory<>("losses"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        goalDifferenceColumn.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));
    }

    /**
     * Setup event handlers for buttons.
     */
    private void setupEventHandlers() {
        proceedButton.setOnAction(e -> handleProceed());
        trainingButton.setOnAction(e -> handleTraining());
    }

    /**
     * Refresh the league view with current season data.
     * Called when navigating to this screen or after a match completes.
     */
    public void refreshLeagueView() {
        if (sport == null) {
            return;
        }

        // Update week information
        int currentWeek = sport.getCurrentWeek();
        weekLabel.setText("Week: " + currentWeek);
        seasonLabel.setText("Season Week " + currentWeek);

        // Update standings table
        updateStandingsTable();

        // Update next fixture information
        updateNextFixture();

        // Check if season is over
        if (sport.isSeasonOver()) {
            proceedButton.setText("Season Over");
            proceedButton.setDisable(true);
            trainingButton.setDisable(true);
        }
    }

    /**
     * Update the standings table with current league standings.
     */
    private void updateStandingsTable() {
        List<TeamStanding> standings = sport.getLeagueTable();
        ObservableList<TeamStandingRow> rows = FXCollections.observableArrayList();

        for (int i = 0; i < standings.size(); i++) {
            TeamStanding standing = standings.get(i);
            rows.add(new TeamStandingRow(
                    i + 1,
                    standing.getTeam().getName(),
                    standing.getWins() + standing.getDraws() + standing.getLosses(),
                    standing.getWins(),
                    standing.getDraws(),
                    standing.getLosses(),
                    standing.getPoints(2, 1),
                    standing.getGoalDifference()
            ));
        }

        standingsTable.setItems(rows);
    }

    /**
     * Update the next fixture information at the top of the view.
     */
    private void updateNextFixture() {
        List<Team> teams = sport.getTeams();
        if (teams.isEmpty()) {
            nextFixtureLabel.setText("No fixtures available");
            nextOpponentLabel.setText("");
            return;
        }

        // Get the user's team (first team for now - can be enhanced)
        Team userTeam = teams.get(0);

        // Find next match for user's team
        Match nextMatch = findNextMatch(userTeam);

        if (nextMatch != null) {
            Team opponent = nextMatch.getHomeTeam().equals(userTeam) 
                    ? nextMatch.getAwayTeam() 
                    : nextMatch.getHomeTeam();
            
            boolean isHome = nextMatch.getHomeTeam().equals(userTeam);
            String venue = isHome ? "Home" : "Away";

            nextFixtureLabel.setText("Next Fixture: " + venue + " vs " + opponent.getName());
            nextOpponentLabel.setText("Opponent: " + opponent.getName());
        } else {
            nextFixtureLabel.setText("No more fixtures");
            nextOpponentLabel.setText("");
        }
    }

    /**
     * Find the next incomplete match for a given team.
     * Only concerns itself with Match interface - no knowledge of Football implementations.
     */
    private Match findNextMatch(Team team) {
        // This would query the league for matches involving the team
        // Implementation depends on available league query methods
        // For now, return null - implementation will be enhanced
        return null;
    }

    @FXML
    private void handleProceed() {
        // Proceed with match or next week
        if (sport != null && !sport.isSeasonOver()) {
            sport.simulateWeek();
            refreshLeagueView();
        }
    }

    @FXML
    private void handleTraining() {
        // Navigate to training view
        // This would trigger a scene change to TrainingController
    }

    /**
     * Helper class for displaying standings in TableView.
     * Provides properties for FXML binding.
     */
    public static class TeamStandingRow {
        private final int position;
        private final String teamName;
        private final int gamesPlayed;
        private final int wins;
        private final int draws;
        private final int losses;
        private final int points;
        private final int goalDifference;

        public TeamStandingRow(int position, String teamName, int gamesPlayed, 
                              int wins, int draws, int losses, int points, int goalDifference) {
            this.position = position;
            this.teamName = teamName;
            this.gamesPlayed = gamesPlayed;
            this.wins = wins;
            this.draws = draws;
            this.losses = losses;
            this.points = points;
            this.goalDifference = goalDifference;
        }

        public int getPosition() { return position; }
        public String getTeamName() { return teamName; }
        public int getGamesPlayed() { return gamesPlayed; }
        public int getWins() { return wins; }
        public int getDraws() { return draws; }
        public int getLosses() { return losses; }
        public int getPoints() { return points; }
        public int getGoalDifference() { return goalDifference; }
    }
}

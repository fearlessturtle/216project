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

public class LeagueViewController implements Initializable {

    private Sport sport;

    @FXML
    private Label seasonLabel;

    @FXML
    private TableView<TeamStandingRow> standingsTable;
    
    @FXML
    private TableColumn<TeamStandingRow, String> teamColumn;
    
    @FXML
    private TableColumn<TeamStandingRow, Integer> playedColumn;
    
    @FXML
    private TableColumn<TeamStandingRow, Integer> winsColumn;
    
    @FXML
    private TableColumn<TeamStandingRow, Integer> drawsColumn;
    
    @FXML
    private TableColumn<TeamStandingRow, Integer> lossesColumn;
    
    @FXML
    private TableColumn<TeamStandingRow, Integer> pointsColumn;
    
    @FXML
    private TableColumn<TeamStandingRow, Integer> goalDifferenceColumn;

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

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeStandingsTable();
        setupEventHandlers();
    }

    private void initializeStandingsTable() {
        teamColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, String>("teamName"));
        playedColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("gamesPlayed"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("wins"));
        drawsColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("draws"));
        lossesColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("losses"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("points"));
        goalDifferenceColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("goalDifference"));
    }

    private void setupEventHandlers() {
        proceedButton.setOnAction(e -> handleProceed());
        trainingButton.setOnAction(e -> handleTraining());
    }

    public void refreshLeagueView() {
        if (sport == null) {
            return;
        }

        int currentWeek = sport.getCurrentWeek();
        weekLabel.setText("Week: " + currentWeek);
        seasonLabel.setText("Season Week " + currentWeek);
        updateStandingsTable();
        updateNextFixture();
        if (sport.isSeasonOver()) {
            proceedButton.setText("Season Over");
            proceedButton.setDisable(true);
            trainingButton.setDisable(true);
        }
    }

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

    private void updateNextFixture() {
        List<Team> teams = sport.getTeams();
        if (teams.isEmpty()) {
            nextFixtureLabel.setText("No fixtures available");
            nextOpponentLabel.setText("");
            return;
        }

        Team userTeam = teams.get(0);
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

    private Match findNextMatch(Team team) {
        return null;
    }

    @FXML
    private void handleProceed() {
        if (sport != null && !sport.isSeasonOver()) {
            sport.simulateWeek();
            refreshLeagueView();
        }
    }

    @FXML
    private void handleTraining() {
    }

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

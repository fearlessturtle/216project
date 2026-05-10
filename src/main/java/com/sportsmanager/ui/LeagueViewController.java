package com.sportsmanager.ui;

import com.sportsmanager.core.*;
import com.sportsmanager.core.SaveGameManager;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LeagueViewController implements Initializable {

    private Sport sport;
    private Team managedTeam;
    private Match nextMatch;
    private String requestedTeamName;

    @FXML
    private Label seasonLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label managedTeamLabel;

    @FXML
    private TableView<TeamStandingRow> standingsTable;
    
    @FXML
    private TableColumn<TeamStandingRow, Integer> rankColumn;

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
    private ComboBox<Team> teamComboBox;

    @FXML
    private Button proceedButton;

    @FXML
    private Button trainingButton;

    @FXML
    private Button teamButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private Label weekLabel;

    public void setSport(Sport sport) {
        this.sport = sport;
        populateTeamComboBox();
        refreshLeagueView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeStandingsTable();
        initializeTeamComboBox();
        if (sport != null) {
            refreshLeagueView();
        }
    }

    private void initializeStandingsTable() {
        rankColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("position"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, String>("teamName"));
        playedColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("gamesPlayed"));
        winsColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("wins"));
        drawsColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("draws"));
        lossesColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("losses"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("points"));
        goalDifferenceColumn.setCellValueFactory(new PropertyValueFactory<TeamStandingRow, Integer>("goalDifference"));
        UiStyles.useConstrainedResizePolicy(standingsTable);
    }

    private void initializeTeamComboBox() {
        if (teamComboBox == null) {
            return;
        }

        teamComboBox.setCellFactory(list -> new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(team.getCrest() + " " + team.getName());
                }
            }
        });

        teamComboBox.setButtonCell(new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText("Pick a club");
                } else {
                    setText(team.getCrest() + " " + team.getName());
                }
            }
        });
        teamComboBox.setPromptText("Pick a club");

        teamComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldTeam, newTeam) -> {
            managedTeam = newTeam;
            if (newTeam != null) {
                requestedTeamName = newTeam.getName();
            }
            updateManagedTeamBadge();
            updateNextFixture();
            if (newTeam != null) {
                setStatus("Managing " + managedTeamName());
            }
        });
    }

    private void populateTeamComboBox() {
        if (teamComboBox == null) {
            managedTeam = null;
            updateManagedTeamBadge();
            return;
        }

        if (sport == null) {
            teamComboBox.getItems().clear();
            managedTeam = null;
            updateManagedTeamBadge();
            return;
        }

        List<Team> teams = sport.getTeams();
        teamComboBox.getItems().setAll(teams);

        if (teams.isEmpty()) {
            managedTeam = null;
            teamComboBox.getSelectionModel().clearSelection();
            updateManagedTeamBadge();
            return;
        }

        Team target = findTeamByName(requestedTeamName, teams);
        if (target == null) {
            target = teams.get(0);
        }

        teamComboBox.getSelectionModel().select(target);
        managedTeam = target;
        updateManagedTeamBadge();
    }

    public void refreshLeagueView() {
        if (sport == null) {
            setStatus("No sport loaded.");
            updateManagedTeamBadge();
            return;
        }

        int currentWeek = sport.getCurrentWeek();
        weekLabel.setText("Week: " + currentWeek);
        seasonLabel.setText(sport.getSportName() + " Control Room");
        updateStandingsTable();
        updateNextFixture();
        updateManagedTeamBadge();

        boolean noTeams = sport.getTeams().isEmpty();
        if (sport.isSeasonOver()) {
            proceedButton.setText("New Season");
            proceedButton.setDisable(noTeams);
        } else {
            proceedButton.setText("Proceed to Match");
            proceedButton.setDisable(noTeams);
        }

        if (teamComboBox != null) {
            teamComboBox.setDisable(noTeams);
        }
        if (trainingButton != null) {
            trainingButton.setDisable(noTeams);
        }
        if (teamButton != null) {
            teamButton.setDisable(noTeams);
        }
        if (saveButton != null) {
            saveButton.setDisable(false);
        }
        setStatus(noTeams ? "League is still warming up." : "Managing " + managedTeamName());
    }

    private void updateStandingsTable() {
        List<TeamStanding> standings = sport.getLeagueTable();
        ObservableList<TeamStandingRow> rows = FXCollections.observableArrayList();

        for (int i = 0; i < standings.size(); i++) {
            TeamStanding standing = standings.get(i);
            Team team = standing.getTeam();
            rows.add(new TeamStandingRow(
                    i + 1,
                    team.getCrest() + " " + team.getName(),
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
        nextMatch = findNextMatch(managedTeam);
        if (nextMatch == null) {
            nextFixtureLabel.setText("No fixtures available");
            nextOpponentLabel.setText("Season complete");
            return;
        }

        Team userTeam = managedTeam;
        Team home = nextMatch.getHomeTeam();
        Team away = nextMatch.getAwayTeam();

        if (userTeam != null && (home.equals(userTeam) || away.equals(userTeam))) {
            Team opponent = home.equals(userTeam) ? away : home;
            String venue = home.equals(userTeam) ? "Home game" : "Away game";
            nextFixtureLabel.setText(venue + " vs " + opponent.getCrest() + " " + opponent.getName());
            nextOpponentLabel.setText("Opponent: " + opponent.getCrest() + " " + opponent.getName());
            return;
        }

        nextFixtureLabel.setText(home.getCrest() + " " + home.getName()
                + " vs " + away.getCrest() + " " + away.getName());
        nextOpponentLabel.setText("League schedule");
    }

    private Match findNextMatch(Team team) {
        if (sport == null) {
            return null;
        }

        List<Match> fixtures = sport.getLeague().getFixtures();
        if (team != null) {
            for (Match match : fixtures) {
                if (!match.isCompleted()
                        && (match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team))) {
                    return match;
                }
            }
        }

        for (Match match : fixtures) {
            if (!match.isCompleted()) {
                return match;
            }
        }

        return null;
    }

    public void setManagedTeamName(String teamName) {
        this.requestedTeamName = teamName;
        if (teamComboBox != null && sport != null) {
            populateTeamComboBox();
            updateManagedTeamBadge();
            updateNextFixture();
        }
    }

    @FXML
    private void handleProceed() {
        if (sport == null) {
            return;
        }

        if (sport.isSeasonOver()) {
            startNewSeason();
            return;
        }

        Match match = findNextMatch(managedTeam);
        if (match == null) {
            setStatus("No upcoming match.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PreMatch.fxml"));
            Stage stage = (Stage) proceedButton.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(), "Sports Manager - Pre-Match");
            PreMatchController controller = loader.getController();
            controller.setSport(sport);
            controller.setMatch(match);
            controller.setManagedTeamName(managedTeam != null ? managedTeam.getName() : null);
            stage.show();
        } catch (Exception e) {
            setStatus("Could not open match screen.");
        }
    }

    @FXML
    private void handleTraining() {
        if (sport == null || managedTeam == null) {
            setStatus("No team selected.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TrainingView.fxml"));
            Stage stage = (Stage) trainingButton.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(), "Sports Manager - Training");
            TrainingController controller = loader.getController();
            controller.setSport(sport);
            controller.setTeam(managedTeam);
            controller.setCurrentWeek(sport.getCurrentWeek());
            stage.show();
        } catch (Exception e) {
            setStatus("Could not open training screen.");
        }
    }

    @FXML
    private void handleTeamView() {
        if (sport == null || managedTeam == null) {
            setStatus("No team selected.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamView.fxml"));
            Stage stage = (Stage) teamButton.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(), "Sports Manager - Team View");
            TeamViewController controller = loader.getController();
            controller.setSport(sport);
            controller.setTeam(managedTeam);
            stage.show();
        } catch (Exception e) {
            setStatus("Could not open team screen.");
        }
    }

    @FXML
    private void handleSaveGame() {
        if (sport == null) {
            setStatus("No game to save.");
            return;
        }

        try {
            SaveGameManager manager = new SaveGameManager();
            manager.saveGame(sport, sport.getSportName(), managedTeam != null ? managedTeam.getName() : null);
            setStatus("Game saved.");
        } catch (Exception e) {
            setStatus("Save failed.");
        }
    }

    @FXML
    private void handleBackToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            UiNavigator.setScene(stage, loader.load(), "Sports Manager - Main Menu");
            stage.show();
        } catch (Exception e) {
            setStatus("Could not return to menu.");
        }
    }

    private void startNewSeason() {
        if (sport == null || sport.getLeague() == null) {
            return;
        }

        sport.getLeague().resetSeason();
        sport.getLeague().generateFixture();
        sport.setCurrentWeek(0);
        populateTeamComboBox();
        refreshLeagueView();
        setStatus("New season started.");
    }

    private Team findTeamByName(String teamName, List<Team> teams) {
        if (teamName == null || teams == null) {
            return null;
        }

        for (Team team : teams) {
            if (team != null && teamName.equals(team.getName())) {
                return team;
            }
        }
        return null;
    }

    private void updateManagedTeamBadge() {
        if (managedTeamLabel == null) {
            return;
        }

        if (managedTeam == null) {
            managedTeamLabel.setText("No club selected");
            UiStyles.clearAccentBadge(managedTeamLabel);
            return;
        }

        managedTeamLabel.setText(managedTeam.getCrest() + " " + managedTeam.getName());
        UiStyles.applyAccentBadge(managedTeamLabel, managedTeam.getAccentColor(), "22", "88");
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private String managedTeamName() {
        return managedTeam != null ? managedTeam.getCrest() + " " + managedTeam.getName() : "no active club";
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

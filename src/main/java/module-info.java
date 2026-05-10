module com.sportsmanager {
    requires com.google.gson;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;

    exports com.sportsmanager to javafx.graphics;
    exports com.sportsmanager.core;
    exports com.sportsmanager.engine;
    exports com.sportsmanager.sports.basketball;
    exports com.sportsmanager.sports.football;
    exports com.sportsmanager.ui;

    opens com.sportsmanager.core to com.google.gson;
    opens com.sportsmanager.ui to javafx.fxml;

    uses com.sportsmanager.core.SportFactory;
    provides com.sportsmanager.core.SportFactory with
            com.sportsmanager.sports.basketball.BasketballFactory,
            com.sportsmanager.sports.football.FootballFactory;
}

package com.sportsmanager;

import com.sportsmanager.ui.LeagueViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SportsManagerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LeagueViewController.fxml"));
        BorderPane root = loader.load();

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Sports Manager - League View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

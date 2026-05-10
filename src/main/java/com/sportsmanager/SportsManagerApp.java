package com.sportsmanager;

import com.sportsmanager.ui.UiNavigator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class SportsManagerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sportsmanager/ui/MainMenu.fxml"));
        Parent root = loader.load();
        UiNavigator.setScene(primaryStage, root, "Sports Manager - Main Menu");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

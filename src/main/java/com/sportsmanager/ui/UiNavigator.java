package com.sportsmanager.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class UiNavigator {

    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 720;

    private UiNavigator() {
    }

    public static void setScene(Stage stage, Parent root, String title) {
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        if (title != null && !title.isBlank()) {
            stage.setTitle(title);
        }
    }
}

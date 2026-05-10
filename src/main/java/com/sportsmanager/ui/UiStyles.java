package com.sportsmanager.ui;

import javafx.scene.control.Labeled;
import javafx.scene.control.TableView;

public final class UiStyles {

    private UiStyles() {
    }

    public static void applyAccentBadge(Labeled control, String accentColor) {
        applyAccentBadge(control, accentColor, "18", "66");
    }

    public static void applyAccentBadge(Labeled control, String accentColor, String backgroundAlpha, String borderAlpha) {
        if (control == null) {
            return;
        }
        if (accentColor == null || accentColor.isBlank()) {
            control.setStyle("");
            return;
        }
        control.setStyle(
                "-fx-background-color: " + accentColor + backgroundAlpha + ";"
                        + "-fx-border-color: " + accentColor + borderAlpha + ";"
                        + "-fx-text-fill: " + accentColor + ";"
        );
    }

    public static void clearAccentBadge(Labeled control) {
        if (control != null) {
            control.setStyle("");
        }
    }

    public static void useConstrainedResizePolicy(TableView<?> tableView) {
        if (tableView != null) {
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        }
    }
}

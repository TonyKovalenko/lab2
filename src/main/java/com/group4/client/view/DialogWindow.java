package com.group4.client.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 * Realizes methods for displaying dialog windows
 */
public class DialogWindow {
    private static Alert lastInstance;

    /**
     * Shows the dialog and waits for the user response (in other words, brings
     * up a blocking dialog, with the returned value the users input)
     *
     * @param title  title for dialog window
     * @param header text to show in the dialog header area
     * @param text   text to show in the dialog content area
     * @param type   alert type of the dialog window
     */
    public static void showDialogWindow(String title, String header, String text, Alert.AlertType type, String okButtonText) {
        ButtonType ok = new ButtonType(okButtonText, ButtonBar.ButtonData.OK_DONE);
        lastInstance = new Alert(type, text, ok);
        lastInstance.initStyle(StageStyle.UNDECORATED);
        lastInstance.setTitle(title);
        lastInstance.setHeaderText(header);
    }

    /**
     * Shows the warning dialog
     *
     * @param header text to show in the dialog header area
     * @param text   text to show in the dialog content area
     */
    public static void showWarningWindow(String header, String text) {
        Platform.runLater(() -> {
            showDialogWindow("Warning!", header, text, Alert.AlertType.WARNING, "OK");
            lastInstance.showAndWait();
        });
    }

    /**
     * Shows the information dialog
     *
     * @param text text to show in the dialog content area
     */
    public static void showInfoWindow(String text) {
        Platform.runLater(() -> {
            showDialogWindow("Information Dialog", null, text, Alert.AlertType.INFORMATION, "OK");
            lastInstance.showAndWait();
        });
    }

    /**
     * Shows the error dialog
     *
     * @param text text to show in the dialog content area
     */
    public static void showErrorWindow(String text) {
        Platform.runLater(() -> {
            showDialogWindow("Error Dialog", "Error!", text, Alert.AlertType.ERROR, "OK");
            lastInstance.showAndWait();
        });
    }

    /**
     * Shows the error dialog that can't be closed by the user
     *
     * @param text text to show in the dialog content area
     */
    public static void showErrorWindowWithoutButtons(String text) {
        Platform.runLater(() -> {
            showDialogWindow("Error Dialog", "Error!", text, Alert.AlertType.ERROR, "OK");
            lastInstance.getButtonTypes().clear();
            lastInstance.setResult(ButtonType.OK);
            lastInstance.show();
        });
    }

    /**
     * Shows the confirmation dialog and returns {@code true} if user pressed "OK" button, otherwise {@code false}
     *
     * @param header text to show in the dialog header area
     * @param text   text to show in the dialog content area
     * @return {@code true}  if user pressed "OK" button
     */
    public static boolean showConfirmationWindow(String header, String text) {
        showDialogWindow("Confirmation Dialog", header, text, Alert.AlertType.CONFIRMATION, "Yes");
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        lastInstance.getButtonTypes().add(noButton);
        Optional<ButtonType> result = lastInstance.showAndWait();
        return result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    /**
     * Gets last created instance of this class
     *
     * @return
     */
    public static Alert getLastInstance() {
        return lastInstance;
    }
}
